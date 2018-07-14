package org.usfirst.frc.team6494.robot.controller;

import org.usfirst.frc.team6494.robot.Robot;
import org.usfirst.frc.team6494.robot.commands.AutoCmd;
import org.usfirst.frc.team6494.robot.commands.Command;
import org.usfirst.frc.team6494.robot.subsystems.Intaker;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;
import org.usfirst.frc.team6494.robot.controller.BaseController;
import org.usfirst.frc.team6494.robot.controller.IntakerController;
import org.usfirst.frc.team6494.robot.controller.IntakerController.PitchCmd;
import org.usfirst.frc.team6494.robot.controller.IntakerController.ShootCmd;

public class IntakerController extends BaseController{
	private static IntakerController sInstance;
	
	private OperateOI mOI;
	private Intaker mIntaker;

	public static IntakerController get() {
		if(sInstance==null)sInstance=new IntakerController();
		return sInstance;
	}
	
	public IntakerController() {
		super();
		mOI=OperateOI.get();
		mIntaker=Intaker.get();
	}
	
	public void initAuto(int pos, boolean left) {
		if(pos==Robot.MIDDLE) {
			mCmds.add(new ShootCmd(9800, 2000));
		}else if (pos==Robot.LEFT) {
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
		mIntaker.feedStop();
	}
	
	public void runTeleOp() {
		mIntaker.setIsManual(mOI.isIntakerManualActivated());
		if(!mOI.isIntakerManualActivated()) {
			
		}if(mOI.getIntakerAdjustDownwards()) {
				mIntaker.adjustDownwards();
			
		}else {
			mIntaker.setRawIntakerPitch(mOI.getIntakerPitchSpeed());
		}		
		mIntaker.setRawIntakerSpeed(mOI.getIntakerSpeed());
	}
	
	static class PitchCmd extends AutoCmd{
		private static final int
				CMD_GOTO_VERTICAL=0,
				CMD_RESET_TO_VERTICAL=1,
				CMD_GOTO_HORIZONTAL=2,
				CMD_GOTO_SHOOT_PITCH=3;
		private int mCmd;
		public PitchCmd(long startTimeStamp,int cmd) {
			super();
			mStartTimestamp=startTimeStamp;
			mCmd=cmd;
		}
		@Override
		public void run(long time) {
			if(mStatus!=Command.RUNNING) {
				switch (mCmd) {
					case CMD_GOTO_VERTICAL:
						Intaker.get().gotoVertical();
						break;
					case CMD_GOTO_HORIZONTAL:
						Intaker.get().gotoHorizontal();
						break;
					case CMD_RESET_TO_VERTICAL:
						Intaker.get().resetToVertical();
						break;
					case CMD_GOTO_SHOOT_PITCH:
						Intaker.get().gotoShootPitch();
						break;
				}
				super.run();
			}		
		}
		@Override
		public void stop() {
			super.stop();
			Intaker.get().feedStop();
		}
	}
	
	static class ShootCmd extends AutoCmd{
		private long mDuration;
		public ShootCmd(long startTimeStamp,long duration) {
			super();
			mStartTimestamp=startTimeStamp;
			mDuration=duration;
		}
		@Override
		public void run(long time) {
			super.run();
			Intaker.get().setRawIntakerSpeed(1);
		}
		@Override
		public void stop() {
			super.stop();
			Intaker.get().feedStop();
		}
	}
}