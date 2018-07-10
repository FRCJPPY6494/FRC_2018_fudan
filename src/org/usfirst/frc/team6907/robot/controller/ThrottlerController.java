package org.usfirst.frc.team6907.robot.controller;

import org.usfirst.frc.team6907.robot.commands.AutoCmd;
import org.usfirst.frc.team6907.robot.subsystems.Elevator;
import org.usfirst.frc.team6907.robot.subsystems.OperateOI;
import org.usfirst.frc.team6907.robot.subsystems.Throttler;

public class ThrottlerController extends BaseController{
	private static final double EPS = 0.001;
	
	public static final double
			HEIGHT_ZERO = 0.0,	
			HEIGHT_LAUNCH = 0.013,
			HEIGHT_HORIZONTAL = 0.03;
	
	public static ThrottlerController mInstance;
	
	private OperateOI mOI;
	
	private Throttler mThrottler;
	
	private boolean mLastManual;
	private boolean mLastManualAdjust;
	
	public static ThrottlerController get(){
		if(mInstance == null) mInstance = new ThrottlerController();
		return mInstance;
	}
	
	public ThrottlerController(){
		super();
		mOI = OperateOI.get();
		mThrottler = Throttler.get();
		mLastManual = false;
		mLastManualAdjust = false;
	}
	
	public void initAuto(int pos){
		mCmds.add(new GotoPosCmd(1000, HEIGHT_HORIZONTAL));
	}
	
	@Override
	public void stopAuto() {
		super.stopAuto();
		mThrottler.feedStop();
	}
	
	public void runTeleOp(){
		if(!mOI.isIntakerManualActivated()) {
			if(mOI.getIntakerResetToVertical()) {
				mThrottler.resetToVertical();
			}else if(mOI.getIntakerProtect()) {
				mThrottler.gotoVertical();
			}else if(mOI.getIntakerShootPrep()) {
				mThrottler.gotoShootPitch();
			}else if(mOI.getIntakerTakeIn()) {
				mThrottler.gotoHorizontal();
			}else if(mOI.getIntakerAdjustDownwards()) {
				mThrotter.adjustDownwards();
			}
		}else {
			mThrottler.setRawIntakerPitch(mOI.getIntakerPitchSpeed());
		}		
		mThrottler.setRawIntakerSpeed(mOI.getIntakerSpeed());
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
