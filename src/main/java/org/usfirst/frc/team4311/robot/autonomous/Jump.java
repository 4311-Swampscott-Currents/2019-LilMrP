package org.usfirst.frc.team4311.robot.autonomous;

import org.usfirst.frc.team4311.robot.*;

public class Jump extends AutonomousTask {
	
	private double EndTime = 0;
	
	public boolean IsViable() { return false; }

	@Override
	public void Start() {
		EndTime = Robot.TotalTime + 1;
		RobotMap.BackLeftMotor.set(-1);
		RobotMap.FrontLeftMotor.set(-1);
		RobotMap.BackRightMotor.set(1);
		RobotMap.FrontRightMotor.set(1);
	}

	public boolean Update() {
		if(EndTime < Robot.TotalTime) {
			return true;
		}
		return false;
	}

	public void End() {
		RobotMap.BackLeftMotor.set(0);
		RobotMap.FrontLeftMotor.set(0);
		RobotMap.BackRightMotor.set(0);
		RobotMap.FrontRightMotor.set(0);
	}

	public String GetName() {
		return "Leap of Faith";
	}
}
