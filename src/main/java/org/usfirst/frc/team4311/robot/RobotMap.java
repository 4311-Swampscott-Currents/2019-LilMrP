package org.usfirst.frc.team4311.robot;

import edu.wpi.first.wpilibj.*;

public final class RobotMap {
	
	public static Spark FrontLeftMotor = new Spark(1);
	public static Spark FrontRightMotor = new Spark(4);
	public static Spark BackLeftMotor = new Spark(2);
	public static Spark BackRightMotor = new Spark(3);
	public static Spark BackClimbMotor = new Spark(5);

	public static Servo CameraYawServo = new Servo(8);
	public static Servo CameraPitchServo = new Servo(9);
	
	public static DoubleSolenoid ClawSolenoid = new DoubleSolenoid(1, 0);
	public static DoubleSolenoid BackClimbSolenoid = new DoubleSolenoid(4, 5);
	public static DoubleSolenoid FrontClimbSolenoid = new DoubleSolenoid(6, 7);
	public static DoubleSolenoid ClawExtensionSolenoid = new DoubleSolenoid(2, 3);

	public static ADXRS450_Gyro Gyroscope = new ADXRS450_Gyro();
	
	public static Joystick MainJoystick = new Joystick(0);
}
