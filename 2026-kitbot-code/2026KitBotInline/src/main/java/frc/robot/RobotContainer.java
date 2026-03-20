// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
// NetworkTableEntry not used directly; use GenericEntry from networktables
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import static frc.robot.Constants.OperatorConstants.*;
import static frc.robot.Constants.FuelConstants.*;
import frc.robot.commands.Autos;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem();
  private final CANFuelSubsystem ballSubsystem = new CANFuelSubsystem();

  // Possible driver controllers (we detect which is attached at runtime)
  private final CommandXboxController xboxController;
  private final CommandJoystick flightJoystick;
  private final boolean usingXbox;
  // Dashboard entries (Shuffleboard)
  // Controller chooser (Auto / Force Xbox / Force Flight)
  private final SendableChooser<String> controllerChooser;
  private final GenericEntry testingEntry;
  private final GenericEntry detectedControllerEntry;

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Instantiate both controller wrappers (we'll choose which to use at runtime)
    xboxController = new CommandXboxController(DRIVER_CONTROLLER_PORT);
    flightJoystick = new CommandJoystick(DRIVER_CONTROLLER_PORT);

    // Try to detect controller by name reported by the Driver Station. Fall back
    // to Xbox mapping if detection fails. Allow dashboard override (see below).
    String jsName = DriverStation.getJoystickName(DRIVER_CONTROLLER_PORT);
    boolean isXboxLocal = false;
    if (jsName != null) {
      String n = jsName.toLowerCase();
      if (n.contains("xbox") || n.contains("x-box")) {
        isXboxLocal = true;
      }
    }
    usingXbox = isXboxLocal;

  // Create a SendableChooser for controller selection: Auto / Force Xbox / Force Flight
  controllerChooser = new SendableChooser<>();
  controllerChooser.setDefaultOption("Auto", "Auto");
  controllerChooser.addOption("Force Xbox", "Xbox");
  controllerChooser.addOption("Force Flight", "Flight");
  // Put chooser on Shuffleboard (larger widget)
  Shuffleboard.getTab("Controls")
    .add("Controller Mode", controllerChooser)
    .withPosition(0, 0)
    .withSize(3, 2);

  // Testing mode toggle (bigger toggle button)
  testingEntry = Shuffleboard.getTab("Controls")
    .add("TestingMode", true)
    .withWidget("Toggle Button")
    .withPosition(3, 0)
    .withSize(2, 2)
    .getEntry();

  // Detected controller label
  detectedControllerEntry = Shuffleboard.getTab("Controls")
    .add("Detected Controller", jsName == null ? "Unknown" : jsName)
    .withPosition(0, 2)
    .withSize(5, 1)
    .getEntry();
  detectedControllerEntry.setString(jsName == null ? "Unknown" : jsName);

  // Info/help panel
  Shuffleboard.getTab("Controls")
    .add("Info",
      "Logitech Extreme 3D Pro mappings: Trigger=Shoot, Thumb=Intake, Button5=Eject. Twist=Rotation, Stick Y=Forward/Back, Stick X=Turn fallback.")
    .withWidget("Text View")
    .withPosition(0, 3)
    .withSize(5, 1);

  // Start a small background poller to report currently-pressed flight-stick
  // buttons to the Detected Controller widget. This helps identify which
  // physical button corresponds to which button ID so you can update
  // `Constants` if needed.
  final String jsNameFinal = jsName == null ? "Unknown" : jsName;
  java.util.Timer poller = new java.util.Timer(true);
  poller.scheduleAtFixedRate(new java.util.TimerTask() {
    @Override
    public void run() {
      try {
        // Only poll when using/forcing the flight joystick so we don't mix
        // results with an Xbox controller on the same port.
        String sel = controllerChooser.getSelected();
        boolean flightActive = (sel != null && sel.equals("Flight")) || (sel != null && sel.equals("Auto") && !usingXbox);
        if (!flightActive) {
          // show detected controller name when not actively detecting buttons
          detectedControllerEntry.setString(jsNameFinal);
          return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 12; i++) {
          if (flightJoystick.getHID().getRawButton(i)) {
            if (sb.length() > 0) {
              sb.append(",");
            }
            sb.append(i);
          }
        }
        if (sb.length() == 0) {
          detectedControllerEntry.setString(jsNameFinal);
        } else {
          detectedControllerEntry.setString("Buttons pressed: " + sb.toString());
        }
      } catch (Throwable t) {
        // Avoid any uncaught exceptions killing the timer thread; log to
        // stdout so it's visible in driver station logs.
        System.out.println("Button poller error: " + t.getMessage());
      }
    }
  }, 0, 100);

    configureBindings();

    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Autonomous", Autos.exampleAuto(driveSubsystem, ballSubsystem));
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the {@link Trigger#Trigger(java.util.function.BooleanSupplier)}
   * constructor with an arbitrary predicate, or via the named factories in
   * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
   * for {@link CommandXboxController Xbox}/
   * {@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Helper to interpret chooser selection
    // "Auto" -> use runtime-detected `usingXbox`; "Xbox" -> force Xbox; "Flight" -> force flight
    java.util.function.BooleanSupplier isUsingXboxSupplier = () -> {
      String sel = controllerChooser.getSelected();
      if (sel == null || sel.equals("Auto")) {
        return usingXbox;
      }
      return sel.equals("Xbox");
    };
    // Create triggers that check the dashboard override so bindings can be
    // switched at runtime without rebinding. When Override/UseXbox is true,
    // Xbox inputs respond; when false, flight-joystick inputs respond.
    // Intake (Xbox left bumper OR flight thumb button)
  // Intake: Xbox A or flight button 5 (swapped per user request)
  new Trigger(() -> isUsingXboxSupplier.getAsBoolean()
    && xboxController.getHID().getRawButton(XBOX_A_BUTTON))
            .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.intake(), () -> ballSubsystem.stop()));

  new Trigger(() -> !isUsingXboxSupplier.getAsBoolean()
    && flightJoystick.getHID().getRawButton(FLIGHT_THUMB_BUTTON))
            .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.intake(), () -> ballSubsystem.stop()));

    // Shoot (Xbox right bumper OR flight trigger)
  new Trigger(() -> isUsingXboxSupplier.getAsBoolean()
    && xboxController.getHID().getRawButton(XBOX_RB_BUTTON))
            .whileTrue(ballSubsystem.spinUpCommand().withTimeout(SPIN_UP_SECONDS)
                .andThen(ballSubsystem.launchCommand())
                .finallyDo(() -> ballSubsystem.stop()));

  new Trigger(() -> !isUsingXboxSupplier.getAsBoolean()
    && flightJoystick.getHID().getRawButton(FLIGHT_TRIGGER_BUTTON))
            .whileTrue(ballSubsystem.spinUpCommand().withTimeout(SPIN_UP_SECONDS)
                .andThen(ballSubsystem.launchCommand())
                .finallyDo(() -> ballSubsystem.stop()));

    // Eject (Xbox A OR flight button 5)
  // Eject: Xbox LB or flight thumb (swapped per user request)
  new Trigger(() -> isUsingXboxSupplier.getAsBoolean()
    && xboxController.getHID().getRawButton(XBOX_LB_BUTTON))
            .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.eject(), () -> ballSubsystem.stop()));

  new Trigger(() -> !isUsingXboxSupplier.getAsBoolean()
    && flightJoystick.getHID().getRawButton(FLIGHT_BUTTON_5))
            .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.eject(), () -> ballSubsystem.stop()));

    // Default drive command: dynamically read which controller to use and the
    // TestingMode flag to choose scaling. This lets the driver flip modes on
    // the dashboard while driving.
    driveSubsystem.setDefaultCommand(
        driveSubsystem.driveArcade(
            () -> {
              boolean useXbox = isUsingXboxSupplier.getAsBoolean();
              boolean testing = testingEntry.getBoolean(true);
              double driveScale = testing ? DRIVE_SCALING : Constants.OperatorConstants.NORMAL_DRIVE_SCALING;
              if (useXbox) {
                return -xboxController.getLeftY() * driveScale;
              }
              return -flightJoystick.getHID().getRawAxis(FLIGHT_AXIS_Y) * driveScale;
            },
            () -> {
              boolean useXbox = isUsingXboxSupplier.getAsBoolean();
              boolean testing = testingEntry.getBoolean(true);
              double rotScale = testing ? ROTATION_SCALING : Constants.OperatorConstants.NORMAL_ROTATION_SCALING;
              if (useXbox) {
                return -xboxController.getRightX() * rotScale;
              }
              double twist = flightJoystick.getHID().getRawAxis(FLIGHT_AXIS_TWIST);
              if (Math.abs(twist) > 0.05) {
                return -twist * rotScale;
              }
              return -flightJoystick.getHID().getRawAxis(FLIGHT_AXIS_X) * rotScale;
            }));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}
