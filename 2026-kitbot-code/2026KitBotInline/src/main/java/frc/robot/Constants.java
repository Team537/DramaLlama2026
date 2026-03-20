// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final class DriveConstants {
    // Motor controller IDs for drivetrain motors
    public static final int LEFT_LEADER_ID = 2;
    public static final int LEFT_FOLLOWER_ID = 8;
    public static final int RIGHT_LEADER_ID = 3;
    public static final int RIGHT_FOLLOWER_ID = 4;

    // Current limit for drivetrain motors. 60A is a reasonable maximum to reduce
    // likelihood of tripping breakers or damaging CIM motors
    public static final int DRIVE_MOTOR_CURRENT_LIMIT = 60;
  }

  public static final class FuelConstants {
    // Motor controller IDs for Fuel Mechanism motors
    public static final int FEEDER_MOTOR_ID = 5;
    public static final int INTAKE_LAUNCHER_MOTOR_ID = 6;

    // Current limit and nominal voltage for fuel mechanism motors.
    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 60;
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 60;

    // Voltage values for various fuel operations. These values may need to be tuned
    // based on exact robot construction.
    // See the Software Guide for tuning information
  public static final double INTAKING_FEEDER_VOLTAGE = 12;
  public static final double INTAKING_INTAKE_VOLTAGE = -4;
  public static final double LAUNCHING_FEEDER_VOLTAGE = 12;
  public static final double LAUNCHING_LAUNCHER_VOLTAGE = -12;
  public static final double SPIN_UP_FEEDER_VOLTAGE = -6;
    public static final double SPIN_UP_SECONDS = 1;
    // Launcher velocity (RPM) and PID constants for closed-loop velocity control
    public static final double LAUNCHER_TARGET_RPM = 4000.0;
    public static final double LAUNCHER_PID_P = 6e-5; // example P (tune on bot)
    public static final double LAUNCHER_PID_I = 0.0;
    public static final double LAUNCHER_PID_D = 0.0;
    public static final double LAUNCHER_PID_FF = 1e-5; // feedforward term (tune)
  }

  public static final class OperatorConstants {
    // Port constants for driver and operator controllers. These should match the
    // values in the Joystick tab of the Driver Station software
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;

    // These values are multiplied by the joystick value when driving the robot
    // to help avoid driving and turning too fast and being difficult to
    // control. Reduced for safer on-floor testing to lower the risk of injury.
    // If you need to re-enable normal speeds, raise these back toward their
    // original values (.7 / .8) or implement a runtime "testing mode" toggle.
    // Testing-mode (safe) scalings. Keep these as the defaults so the robot
    // starts in a safe, reduced-speed mode.`
    public static final double DRIVE_SCALING = 0.7; // ~50% of previous
    public static final double ROTATION_SCALING = 0.8; // ~50% of previous

    // Normal (full) scalings. These are used when TestingMode is turned off on
    // the dashboard.
    public static final double NORMAL_DRIVE_SCALING = 0.7;
    public static final double NORMAL_ROTATION_SCALING = 0.8;
    
    // Logitech Extreme 3D Pro (flight stick) button/axis mapping. If your
    // joystick reports different IDs in Driver Station, change them here.
    public static final int FLIGHT_TRIGGER_BUTTON = 1;
    public static final int FLIGHT_THUMB_BUTTON = 2;
    public static final int FLIGHT_BUTTON_5 = 5;

    // Axis indices for flight stick
    public static final int FLIGHT_AXIS_X = 0; // stick left/right
    public static final int FLIGHT_AXIS_Y = 1; // stick forward/back
    public static final int FLIGHT_AXIS_TWIST = 2; // twist rotation

  // Xbox controller button IDs (standard mapping). If your controller uses
  // a different mapping, change these values.
  public static final int XBOX_A_BUTTON = 1;
  public static final int XBOX_B_BUTTON = 2;
  public static final int XBOX_X_BUTTON = 3;
  public static final int XBOX_Y_BUTTON = 4;
  public static final int XBOX_LB_BUTTON = 5;
  public static final int XBOX_RB_BUTTON = 6;
  }
}
