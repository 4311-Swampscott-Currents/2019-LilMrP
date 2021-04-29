package org.usfirst.frc.team4311.robot.autonomous;

import org.usfirst.frc.team4311.robot.*;
import org.usfirst.frc.team4311.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climb extends AutonomousTask {
	
	public boolean IsViable() { return true; }
	
	private double Time = 0;
	private int state = 0;

	public void Start() {
		Time = Robot.TotalTime + 2;
		state = 0;
		RobotMap.FrontClimbSolenoid.set(Value.kForward);
		RobotMap.BackClimbSolenoid.set(Value.kForward);
	}

	public boolean Update() {
		/*
		if(state == 0) {
			if(Time < Robot.TotalTime) {
				Time = Robot.TotalTime + 0.75;
				state = 1;
				RobotMap.BackClimbMotor.set(0.5);
				return false;
			}
		}
		if(state == 1) {
			if(Time < Robot.TotalTime) {
				RobotMap.FrontClimbSolenoid.set(Value.kReverse);
				Time = Robot.TotalTime + 1;
				state = 2;
				return false;
			}
		}
		if(state == 2) {
			if(Time < Robot.TotalTime) {
				RobotMap.BackClimbSolenoid.set(Value.kReverse);
				RobotMap.BackLeftMotor.set(-0.5);
				RobotMap.FrontLeftMotor.set(-0.5);
				RobotMap.BackRightMotor.set(-0.5);
				RobotMap.FrontRightMotor.set(-0.5);
				Time = Robot.TotalTime + 0.5;
				state = 3;
				return false;
			}
		}
		if(state == 3) {
			if(Time < Robot.TotalTime) {
				return true;
			}
		}*/
		return true;
	}

	public void End() {
		RobotMap.BackClimbMotor.set(0);
		RobotMap.BackLeftMotor.set(0);
		RobotMap.FrontLeftMotor.set(0);
		RobotMap.BackRightMotor.set(0);
		RobotMap.FrontRightMotor.set(0);
	}

	public String GetName() {
		return "Climb";
	}
}
