package org.usfirst.frc.team6494.robot.controller;

import org.usfirst.frc.team6494.robot.subsystems.Climber;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;

public class ClimberController{	
	private static ClimberController sInstance;

	private OperateOI mOI;
	private Climber mClimber;
	
	public static ClimberController get() {
		if (sInstance==null) sInstance=new ClimberController();
		return sInstance;
	}
	
	public ClimberController() {
		mOI=OperateOI.get();
		mClimber=Climber.get();
	}
	
	public void runTeleOp() {
		if(mOI.getClimberElevate()) {
			mClimber.elevate();
		} else if(mOI.getClimberRelax()){
			mClimber.relax();
		} else if(mOI.getClimberClimb()) {
			mClimber.climb();
		} else if(mOI.getManualRelaxSpeed()!=0.0){
			mClimber.manualrelax(mOI.getManualRelaxSpeed());
		}
			else {
			mClimber.feedStop();
		}
	}
}
