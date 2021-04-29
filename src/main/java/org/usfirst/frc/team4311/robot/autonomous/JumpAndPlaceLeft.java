package org.usfirst.frc.team4311.robot.autonomous;

import org.usfirst.frc.team4311.robot.*;

public class JumpAndPlaceLeft extends AutonomousTask {
	
	private double EndTime = 0;
	private double State = 0;
    private double SpeedControl = 0.5;
    
	public boolean IsViable() { return false; }

	@Override
	public void Start() {
        State = 0;
		EndTime = Robot.TotalTime + 1;
		RobotMap.BackLeftMotor.set(-1);
		RobotMap.FrontLeftMotor.set(-1);
		RobotMap.BackRightMotor.set(1);
		RobotMap.FrontRightMotor.set(1);
	}

	public boolean Update() {
        if(State == 0) {
		    if(EndTime < Robot.TotalTime) {
                RobotMap.BackLeftMotor.set(-SpeedControl);
		        RobotMap.FrontLeftMotor.set(-SpeedControl);
		        RobotMap.BackRightMotor.set(SpeedControl);
	        	RobotMap.FrontRightMotor.set(SpeedControl);
			    State = 1;
	        	EndTime = Robot.TotalTime + 0.5;
                return false;
		    }
        }
        if(State == 1) {
		    if(EndTime < Robot.TotalTime) {
                RobotMap.BackLeftMotor.set(-SpeedControl);
		        RobotMap.FrontLeftMotor.set(-SpeedControl);
		        RobotMap.BackRightMotor.set(0.5 * SpeedControl);
	        	RobotMap.FrontRightMotor.set(0.5 * SpeedControl);
			    State = 2;
	        	EndTime = Robot.TotalTime + 0.4;
                return false;
		    }
        }
        if(State == 2) {
		    if(EndTime < Robot.TotalTime) {
                RobotMap.BackLeftMotor.set(0.5 * -SpeedControl);
		        RobotMap.FrontLeftMotor.set(0.5 * -SpeedControl);
		        RobotMap.BackRightMotor.set(SpeedControl);
	        	RobotMap.FrontRightMotor.set(SpeedControl);
			    State = 3;
	        	EndTime = Robot.TotalTime + 0.4;
                return false;
		    }
        }
        if(State == 3) {
		    if(EndTime < Robot.TotalTime) {
                RobotMap.BackLeftMotor.set(-SpeedControl);
		        RobotMap.FrontLeftMotor.set(-SpeedControl);
		        RobotMap.BackRightMotor.set(SpeedControl);
	        	RobotMap.FrontRightMotor.set(SpeedControl);
			    State = 4;
	        	EndTime = Robot.TotalTime + 1;
                return false;
		    }
        }
        if(State == 4) {
		    if(EndTime < Robot.TotalTime) {
                RobotMap.BackLeftMotor.set(0);
		        RobotMap.FrontLeftMotor.set(0);
		        RobotMap.BackRightMotor.set(0);
	        	RobotMap.FrontRightMotor.set(0);
                return true;
		    }
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
		return "Leap of Faith + Place Left";
	}
}
