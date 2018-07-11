package org.usfirst.frc.team6494.robot.controller;

import org.usfirst.frc.team6494.robot.Robot;
import org.usfirst.frc.team6494.robot.commands.AutoCmd;
import org.usfirst.frc.team6494.robot.subsystems.Elevator;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;
import org.usfirst.frc.team6494.robot.subsystems.Throttler;

public class ThrottlerController extends BaseController{
	private static final double EPS=0.001;
	
	private final static double _zeroPosition = 0;
	private final static double _delta = 0.045;
	public static final double 
			THROTTLER_ZERO = _zeroPosition + 0.09,       //0.09
			THROTTLER_LAUNCH = _zeroPosition + 0.055,      //0.055
			THROTTLER_HORIZONTAL = _zeroPosition + 0.00;
	
	public static final double
			HEIGHT_MANUAL_ADJUST=0.1;
	
	private static ThrottlerController sInstance;
	
	private OperateOI mOI;
	private Throttler mThrottler;
	
	private boolean mLastManual;
	private boolean mLastManualAdjust;
	
	public static ThrottlerController get() {
		if(sInstance==null) sInstance=new ThrottlerController();
		return sInstance;
	}
	
	public ThrottlerController() {
		super();
		mOI=OperateOI.get();
		mThrottler=Throttler.get();
		mLastManual=false;
		mLastManualAdjust=false;
	}
	
	public void initAuto() {
		mCmds.add(new GotoPosCmd(1000, THROTTLER_ZERO));
	}
	
	@Override
	public void stopAuto() {
		super.stopAuto();
		mThrottler.feedStop();
	}
	
	public void runTeleOp() {
		System.out.println(mThrottler.pidGet());
		if(!mOI.isThrottlerManualActivated()) {
			if(mLastManual) {
				mThrottler.setStatic();
			}else {
				mThrottler.startPID();
				switch (mOI.getThrottlerSetPoint()) {
					case OperateOI.THROTTLER_ZERO:
						mThrottler.gotoPos(THROTTLER_ZERO);
						break;
					case OperateOI.THROTTLER_LAUNCH:
						mThrottler.gotoPos(THROTTLER_LAUNCH);
						break;			
					case OperateOI.THROTTLER_HORIZONTAL:
						mThrottler.gotoPos(THROTTLER_HORIZONTAL);
						break;
					default:
						break;
				}
				if(Math.abs(mOI.getThrottlerSpeed())>EPS 
						&& !mLastManualAdjust) {
					mThrottler.gotoRelativePos(
							(mOI.getThrottlerSpeed()>0?1:-0.5)*HEIGHT_MANUAL_ADJUST);
				}
				if(!mThrottler.getPIDEnabled()) mThrottler.setStatic();
			}
			mLastManual=false;
		}else {
			double input=mOI.getThrottlerSpeed();
			if(Math.abs(input)<EPS) {
				if(!mThrottler.getPIDEnabled()) mThrottler.setStatic();
			} else {
				mThrottler.stopPID();
				mThrottler.manualControl(input);
			}
			mLastManual=true;
		}
		mLastManualAdjust=Math.abs(mOI.getThrottlerSpeed())>EPS;
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
				Throttler.get().gotoPos(mPosition);
			}
		}
		@Override
		public void stop() {
			super.stop();
			Throttler.get().feedStop();
		}
	}
}
