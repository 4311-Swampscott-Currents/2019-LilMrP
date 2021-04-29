package org.usfirst.frc.team4311.robot.autonomous;

import org.usfirst.frc.team4311.robot.IO;
import org.usfirst.frc.team4311.robot.RobotMap;

public class BeginMatch extends AutonomousTask {

	public boolean IsViable() { return false; }
	
	public boolean Update() {
		IO.PutButtonOptionText(1, "Leap of Faith");
		IO.PutButtonOptionText(8, "Manual");
		
		if(RobotMap.MainJoystick.getRawButtonPressed(1)) {
			IO.CurrentAutonomousTask = new Jump();
			IO.CurrentAutonomousTask.Start();
		}
		else if(RobotMap.MainJoystick.getRawButtonPressed(8)) {
			return true;
		}
		
		return false;
	}

	public String GetStatusText() {
		return "";
	}
}
