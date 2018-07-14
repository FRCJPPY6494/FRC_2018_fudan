package org.usfirst.frc.team6494.robot.controller;

import org.usfirst.frc.team6494.robot.Robot;
import org.usfirst.frc.team6494.robot.commands.AutoCmd;
import org.usfirst.frc.team6494.robot.subsystems.Elevator;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;
import org.usfirst.frc.team6494.robot.controller.BaseController;
import org.usfirst.frc.team6494.robot.controller.ElevatorController;
import org.usfirst.frc.team6494.robot.controller.ElevatorController.GotoPosCmd;

public class ElevatorController extends BaseController{
	private static final double EPS=0.001;
	
	public static final double 
			HEIGHT_ZERO=0.01,
			HEIGHT_SWITCH=0.21,
			HEIGHT_SCALE_LOW=0.65,
			HEIGHT_SCALE_MIDDLE=0.75,
			HEIGHT_SCALE_HIGH=0.8;
	
	public static final double
			HEIGHT_MANUAL_ADJUST=0.1;
	
	private static ElevatorController sInstance;
	
	private OperateOI mOI;
	private Elevator mElevator;
	
	private boolean mLastManual;
	private boolean mLastManualAdjust;
	
	public static ElevatorController get() {
		if(sInstance==null) sInstance=new ElevatorController();
		return sInstance;
	}
	
	public ElevatorController() {
		super();
		mOI=OperateOI.get();
		mElevator=Elevator.get();
		mLastManual=false;
		mLastManualAdjust=false;
	}
	
	public void initAuto(int pos, boolean left) {
		if(pos==Robot.MIDDLE) {
			mCmds.add(new GotoPosCmd(4000, HEIGHT_SWITCH));
			mCmds.add(new GotoPosCmd(14000, HEIGHT_ZERO));
		}else if(pos==Robot.LEFT){
			if(left) {
				
			}else {
				
			}
		}else {
			if(left) {
				
			}else {
				
			}
		}
	}
	
	@Override
	public void stopAuto() {
		super.stopAuto();
		mElevator.feedStop();
	}
	
	public void runTeleOp() {
		mElevator.log();
		if(!mOI.isElevatorManualActivated()) {
			if(mLastManual) {
				mElevator.setStatic();
			}else {
				mElevator.startPID();
				switch (mOI.getElevatorSetPoint()) {
					case OperateOI.ELEVATOR_ZERO:
						mElevator.gotoPos(HEIGHT_ZERO);
						break;
					case OperateOI.ELEVATOR_SWITCH:
						mElevator.gotoPos(HEIGHT_SWITCH);
						break;			
					case OperateOI.ELEVATOR_SCALE_LOW:
						mElevator.gotoPos(HEIGHT_SCALE_LOW);
						break;
					case OperateOI.ELEVATOR_SCALE_MIDDLE:
						mElevator.gotoPos(HEIGHT_SCALE_MIDDLE);
						break;	
					case OperateOI.ELEVATOR_SCALE_HIGH:
						mElevator.gotoPos(HEIGHT_SCALE_HIGH);
						break;
				}
				if(Math.abs(mOI.getElevatorSpeed())>EPS 
						&& !mLastManualAdjust) {
					mElevator.gotoRelativePos(
							(mOI.getElevatorSpeed()>0?1:-0.5)*HEIGHT_MANUAL_ADJUST);
				}
				if(!mElevator.getPIDEnabled()) mElevator.setStatic();
			}
			mLastManual=false;
		}else {
			double input=mOI.getElevatorSpeed();
			if(Math.abs(input)<EPS) {
				if(!mElevator.getPIDEnabled()) mElevator.setStatic();
			} else {
				mElevator.stopPID();
				mElevator.manualControl(input);
			}
			mLastManual=true;
		}
		mLastManualAdjust=Math.abs(mOI.getElevatorSpeed())>EPS;
	}
	
	static class GotoPosCmd extends AutoCmd{
		private double mPosition;
		public GotoPosCmd(long startTimestamp,double pos) {
			super();
			mStartTimestamp=startTimestamp;
			mPosition=pos;
		}
		@Override
		public void run(long time) {
			if(mStatus==NOT_STARTED) {
				super.run();
				Elevator.get().gotoPos(mPosition);
			}
		}
		@Override
		public void stop() {
			super.stop();
			Elevator.get().feedStop();
		}
	}
}
