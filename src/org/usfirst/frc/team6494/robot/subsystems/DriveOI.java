package org.usfirst.frc.team6494.robot.subsystems;

import org.usfirst.frc.team6494.robot.RobotMap;
import org.usfirst.frc.team6494.robot.utils.Calc;
import org.usfirst.frc.team6494.robot.subsystems.DriveOI;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;

public class DriveOI {
	private static final double 
			DEADBAND=0.065, 
			SLOW_GEAR=0.45,
			NORMAL_GEAR=0.85,
			TURN_SENSITIVITY=0.35,
			TURN_SENSITIVITY_SLOW=0.1,
			TRIGGER_BOUND=0.1;
			
	public static final int
			DIRECTION_FORWARDS=0,
			DIRECTION_RIGHT=1,
			DIRECTION_LEFT=3,
			DIRECTION_BACKWARDS=2,
			DIRECTION_NOT_AVAILABLE=-1;
	
	private static DriveOI sInstance;
	
	private XboxController mController;
	
	public static DriveOI get() {
		if (sInstance==null || !sInstance.isTeleOpInputAvailable()) {
			sInstance=new DriveOI();
		}
		return sInstance;
	}
	
	public DriveOI() {
		mController=new XboxController(RobotMap.OI_DRIVE);
	}
	
	public double getSpeed() {
		double speed=-Calc.eliminateDeadband(mController.getY(Hand.kLeft),DEADBAND);
		return speed*getGear();
	}
	
	public double getTurn() {
		double turn=Calc.eliminateDeadband(mController.getX(Hand.kRight),DEADBAND)*0.9;
		if(getGear()==SLOW_GEAR)return (scaleInput(turn))*TURN_SENSITIVITY_SLOW;
		return turn*TURN_SENSITIVITY;
	}
	
	public int getTurnAlignToCourt() {
		if(mController.getYButton()) return DIRECTION_FORWARDS;
		if(mController.getBButton()) return DIRECTION_RIGHT;
		if(mController.getAButton()) return DIRECTION_BACKWARDS;
		if(mController.getXButton()) return DIRECTION_LEFT;
		return DIRECTION_NOT_AVAILABLE;
	}
	
	public boolean getDriveStraight() {
		return mController.getBumper(Hand.kRight);
	}
	
	public boolean getAbort() {
		return mController.getBackButton();
	}	
	
	private double getGear() {
		if(mController.getBumper(Hand.kLeft))
			return SLOW_GEAR;
		if(mController.getTriggerAxis(Hand.kLeft)>TRIGGER_BOUND)
			return 1;
		return NORMAL_GEAR;
	}
	
	public boolean isTeleOpInputAvailable() {
		return mController!=null;
	}
	
	/**
	 * Write OI status to SmartDashboard
	 * */
	public void log() {
		SmartDashboard.putNumber("OI.Drive.Speed",getSpeed());
		SmartDashboard.putNumber("OI.Drive.Turn",getTurn());
	}
	
	public double scaleInput(double dVal) {
		double dScale = 0.0;
		double scaleArray[] = {
				0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18,
				0.22, 0.28, 0.34, 0.4, 0.45, 0.50, 0.55,
				0.6, 0.62, 0.68, 0.75
		};
		int index = (int)(dVal * 18);
		if(index < 0) index = -index;
		if(index > 18) index = 18;
		if(dVal < 0) dScale = -scaleArray[index];
		else dScale = scaleArray[index];
		return dScale;
	}
}