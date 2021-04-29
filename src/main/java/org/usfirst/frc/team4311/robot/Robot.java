/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4311.robot;

import org.usfirst.frc.team4311.robot.autonomous.*;
import org.usfirst.frc.team4311.robot.vision.*;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {

	public static Robot Mainbot;
	
	//public static SendableChooser<AutonomousTask> AutoChooser = new SendableChooser<AutonomousTask>();

	public static double TimeDelta;
	public static double TotalTime;
	public static Gripper VisionSystem;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		Mainbot = this;
		IO.Start();
		TotalTime = 0;
		//VisionSystem = new Gripper();
		//VisionSystem.Start();
		//AutoChooser.addOption("No Autonomous", null);
		//AutoChooser.addOption("Leap of Faith", new Jump());
		//AutoChooser.addOption("Leap of Faith + Place Left Hatch", new JumpAndPlaceLeft());
	}
	
	@Override
	public void disabledPeriodic() {
		IO.UpdateDisabled();
		TimeDelta = (Timer.getFPGATimestamp() - TotalTime);
		TotalTime = Timer.getFPGATimestamp();
	}
	 
	@Override
	public void autonomousInit() {
		/*AutonomousTask task = AutoChooser.getSelected();
		if(task != null) {
			IO.CurrentAutonomousTask = task;
			IO.CurrentAutonomousTask.Start();
		}*/
		IO.CurrentAutonomousTask = new Jump();
		IO.CurrentAutonomousTask.Start();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		IO.Update();
		TimeDelta = (Timer.getFPGATimestamp() - TotalTime);
		TotalTime = Timer.getFPGATimestamp();
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		IO.Update();
		TimeDelta = (Timer.getFPGATimestamp() - TotalTime);
		TotalTime = Timer.getFPGATimestamp();		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
