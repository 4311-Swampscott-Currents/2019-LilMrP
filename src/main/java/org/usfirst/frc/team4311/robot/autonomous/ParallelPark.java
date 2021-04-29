package org.usfirst.frc.team4311.robot.autonomous;

import org.usfirst.frc.team4311.robot.*;

public class ParallelPark extends AutonomousTask {
	
	private double DriveDegrees = 0;
	private double TotalTurn = 0;
	private boolean isRight = false;
	private int state = 0;
	private double EndTime = 0;

	public boolean IsViable() { return CameraManager.LastIdentifiedLine != null && Math.abs(CameraManager.LastIdentifiedLine.ParallelA.GetSlope()) != Double.POSITIVE_INFINITY && Math.abs(CameraManager.LastIdentifiedLine.ParallelB.GetSlope()) != Double.POSITIVE_INFINITY; }

	@Override
	public void Start() {
		DriveDegrees = RobotMap.Gyroscope.getAngle() - (GetTurnAngle() * 0.8);
		TotalTurn = GetTurnAngle();
		double slope = (CameraManager.LastIdentifiedLine.ParallelA.GetSlope() + CameraManager.LastIdentifiedLine.ParallelB.GetSlope()) / 2;
		double i = (CameraManager.LastIdentifiedLine.EndPoint.A.y + CameraManager.LastIdentifiedLine.EndPoint.B.y - (slope * (CameraManager.LastIdentifiedLine.EndPoint.A.x + CameraManager.LastIdentifiedLine.EndPoint.B.x))) / 2;
		double x = -(i + 8.5) * Math.sin(TotalTurn);
		state = 0;
	}

	public boolean Update() {
		if(state == 0) {
			double ang = Math.abs(DriveDegrees - RobotMap.Gyroscope.getAngle());
			double drv = 4 / (ang - 91) + 1;
			drv *= 0.4 * Math.signum(TotalTurn);
			RobotMap.BackLeftMotor.set(drv);
			RobotMap.FrontLeftMotor.set(drv);
			RobotMap.BackRightMotor.set(drv);
			RobotMap.FrontRightMotor.set(drv);
			if(ang < 4) {
				EndTime = Robot.TotalTime + 1;
				state = 1;
			}
		}
		if(state == 1) {
			double drv = -0.7;
			RobotMap.BackLeftMotor.set(drv);
			RobotMap.FrontLeftMotor.set(drv);
			RobotMap.BackRightMotor.set(-drv);
			RobotMap.FrontRightMotor.set(-drv);
			if(EndTime < Robot.TotalTime) {
				IO.ClawOpen = !IO.ClawOpen;
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
		if(IsViable()) {
			return "Parallel Park (" + new Double(GetTurnAngle()).toString() + ")";
		}
		else {
			return "Parallel Park";
		}
    }
    
    private double GetTurnAngle() {
		double avgSlope = (CameraManager.LastIdentifiedLine.ParallelA.GetSlope() + CameraManager.LastIdentifiedLine.ParallelB.GetSlope()) / 2;
		double dgr = Math.toDegrees(Math.atan((int)-avgSlope));
		if(dgr > 0) {
			return 90 - dgr;
		}
		else {
			return -90 - dgr;
		}
    }
}
