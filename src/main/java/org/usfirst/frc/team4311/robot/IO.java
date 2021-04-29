package org.usfirst.frc.team4311.robot;

import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import org.opencv.core.CvException;
import org.usfirst.frc.team4311.robot.autonomous.*;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


import edu.wpi.first.wpilibj.CameraServer;

public final class IO {
	
	private static boolean ControlsEnabled = false;
	
	public static ArrayList<AutonomousTask> AutonomousTasks = new ArrayList<AutonomousTask>();
	public static AutonomousTask CurrentAutonomousTask;
	public static double DriveSpeed = 1, TurnSpeed = 1;
	public static boolean ClawOpen = true;
	public static boolean ClawExtended = false;

	protected static double CameraYaw = 0.5097774999999928, CameraPitch = 0.355;
	protected static String OptionText = "";
	
	public static void Start() {
		StartCamera();
		//AutonomousTasks.add(new ParallelPark());
		//AutonomousTasks.add(new Climb());
		RobotMap.ClawSolenoid.set(ClawOpen ? Value.kForward : Value.kReverse);
		RobotMap.ClawExtensionSolenoid.set(ClawExtended ? Value.kForward : Value.kReverse);
	}
	
	public static void StartCamera() {
		//CameraManager.initialize();
		CameraServer.getInstance().startAutomaticCapture(0);
	}
	
	public static void Update() {
		UpdateCamera();
		if(IsDriverOperable()) {
			double x = -RobotMap.MainJoystick.getX() * TurnSpeed;
			double leftRightPositive = (1 - Math.abs(RobotMap.MainJoystick.getY())) * (x) + x;
			double leftRightNegative = (1 - Math.abs(x)) * (RobotMap.MainJoystick.getY()) + RobotMap.MainJoystick.getY();
			double rightMotorSpeed = (leftRightPositive + leftRightNegative) / 2 * DriveSpeed;
			double leftMotorSpeed = (leftRightPositive - leftRightNegative) / 2 * DriveSpeed;
			RobotMap.BackLeftMotor.set(-leftMotorSpeed);
			RobotMap.FrontLeftMotor.set(-leftMotorSpeed);
			RobotMap.BackRightMotor.set(-rightMotorSpeed);
			RobotMap.FrontRightMotor.set(-rightMotorSpeed);
		
			if(RobotMap.MainJoystick.getRawButtonPressed(1)) {
				ClawOpen = !ClawOpen;
				RobotMap.ClawSolenoid.set(ClawOpen ? Value.kForward : Value.kReverse);
			}
			if(RobotMap.MainJoystick.getRawButton(2)) {
				CameraPitch = Math.min(1,CameraPitch + (Robot.TimeDelta * 0.5));
			}
			else if(RobotMap.MainJoystick.getRawButton(3)) {
				CameraPitch = Math.max(0,CameraPitch - (Robot.TimeDelta * 0.5));
			}
			if(RobotMap.MainJoystick.getRawButton(5)) {
				CameraYaw = Math.min(1,CameraYaw + (Robot.TimeDelta * 0.5));
			}
			else if(RobotMap.MainJoystick.getRawButton(4)) {
				CameraYaw = Math.max(0,CameraYaw - (Robot.TimeDelta * 0.5));
			}

			if(RobotMap.MainJoystick.getRawButton(8)) {
				CameraPitch = 0.355;
				CameraYaw = 0.5097774999999928;
			}

			//if(RobotMap.MainJoystick.getZ() < 0.15) {
				if(RobotMap.MainJoystick.getRawButtonPressed(6)) {
					RobotMap.FrontClimbSolenoid.set(Value.kForward);
				}
				else if(RobotMap.MainJoystick.getRawButtonReleased(6)) {
					RobotMap.FrontClimbSolenoid.set(Value.kReverse);
				}

				if(RobotMap.MainJoystick.getRawButtonPressed(7)) {
					RobotMap.BackClimbSolenoid.set(Value.kForward);
				}
				else if(RobotMap.MainJoystick.getRawButtonReleased(7)) {
					RobotMap.BackClimbSolenoid.set(Value.kReverse);
				}

				if(RobotMap.MainJoystick.getRawButtonPressed(11)) {
					ClawExtended = !ClawExtended;
					RobotMap.ClawExtensionSolenoid.set(ClawExtended ? Value.kForward : Value.kReverse);
				}

				if(RobotMap.MainJoystick.getRawButton(10)) {
					RobotMap.BackClimbMotor.set(0.5);
				}
				else {
					RobotMap.BackClimbMotor.set(0);
				}
			//}
			
			
			RobotMap.CameraPitchServo.set(CameraPitch);
			RobotMap.CameraYawServo.set(CameraYaw);
		}
		else if(CurrentAutonomousTask != null) {
			PutOptionText(CurrentAutonomousTask.GetStatusText());
			if(CurrentAutonomousTask.Update()) {
				CurrentAutonomousTask.End();
				CurrentAutonomousTask = null;
			}
			else if(!BeginMatch.class.isInstance(CurrentAutonomousTask.getClass()) && !Jump.class.isInstance(CurrentAutonomousTask.getClass()) && RobotMap.MainJoystick.getRawButtonPressed(1)) {
				CurrentAutonomousTask.DriverInterrupted();
				CurrentAutonomousTask = null;
			}
		}
		
		if(CurrentAutonomousTask == null) {
			int x = 8;
			for(AutonomousTask task : AutonomousTasks) {
				if(task.IsViable()) {
					PutButtonOptionText(x, task.GetName());
					if(RobotMap.MainJoystick.getRawButtonPressed(x)) {
						CurrentAutonomousTask = task;
						task.Start();
					}
				}
				x++;
			}
		}
		SmartDashboard.putString("Robot Statgge", new Double(CameraPitch).toString() + " " + new Double(CameraYaw).toString());
		SmartDashboard.putString("Robot State", "Claw " + (ClawOpen ? "EXT" : "CTR") + "/" + (ClawExtended ? "OUT" : "IN"));
		OptionText = "";
	}
	
	public static void UpdateDisabled() {
		UpdateCamera();
		SmartDashboard.putString("Robot State", "[DISABLED] Claw " + (ClawOpen ? "EXT" : "CTR") + "/" + (ClawExtended ? "OUT" : "IN"));
	}
	
	public static void UpdateCamera() {
		/*try {
			//if(!RobotMap.MainJoystick.getRawButton(11)) {
				CameraManager.UpdateCameraFeed();
			//}
			//else {
			//	CameraManager.execute();
			//}
		}
		catch(Exception cept) {
			System.out.println("WARNING " + cept.toString());
		}*/
	}
	
	public static boolean IsDriverOperable() {
		return CurrentAutonomousTask == null && Robot.Mainbot.isEnabled();
	}
	
	public static void PutOptionText(String text) {
		OptionText += text + " ";
	}
	
	public static void PutButtonOptionText(Integer button, String text) {
		OptionText += "[" + button.toString() + "] " + text + " ";
	}
}
