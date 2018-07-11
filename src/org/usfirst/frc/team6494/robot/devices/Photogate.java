package org.usfirst.frc.team6494.robot.devices;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Photogate {

	private static final int BOUND=10;
	
	private AnalogInput mAnalogInput;
	
	public Photogate(int channel) {
		mAnalogInput=new AnalogInput(channel);
	}
	
	public boolean isBlocked() {
		return mAnalogInput.getValue()>BOUND;		
	}
	
	public void log() {
		SmartDashboard.putNumber("Photogate", mAnalogInput.getValue());
	}
}
