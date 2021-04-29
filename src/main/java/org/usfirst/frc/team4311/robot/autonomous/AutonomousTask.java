package org.usfirst.frc.team4311.robot.autonomous;

public class AutonomousTask {
	
	public boolean IsViable() {
		return false;
	}
	
	public void Start() {
	
	}
	
	public boolean Update() {
		return true;
	}
	
	public void End() {
		
	}
	
	public void DriverInterrupted() {
		End();
	}

	public String GetName() {
		return "Autonomous Task";
	}
	
	public String GetStatusText() {
		return "Executing " + GetName() + "...\n[1] - End Task";
	}
}
