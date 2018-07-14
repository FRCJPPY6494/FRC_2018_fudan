package org.usfirst.frc.team6494.robot.controller;

import org.usfirst.frc.team6494.robot.Robot;
import org.usfirst.frc.team6494.robot.commands.AutoCmd;
import org.usfirst.frc.team6494.robot.commands.Command;
import org.usfirst.frc.team6494.robot.subsystems.Drive;
import org.usfirst.frc.team6494.robot.subsystems.DriveOI;
import org.usfirst.frc.team6494.robot.controller.BaseController;
import org.usfirst.frc.team6494.robot.controller.DriveController;
import org.usfirst.frc.team6494.robot.controller.DriveController.DriveAlignCmd;
import org.usfirst.frc.team6494.robot.controller.DriveController.DriveCmd;
import org.usfirst.frc.team6494.robot.controller.DriveController.TurnToCmd;

public class DriveController extends BaseController{
	private static final double EPS=0.00001;
	
	private static DriveController sInstance;
	
	private DriveOI mDriveOI;
	private Drive mDrive;
	
	private boolean mLastDriveStraightActivated;
	
	public static DriveController get() {
		if (sInstance==null) sInstance=new DriveController();
		return sInstance;
	}
	
	public DriveController() {
		super();
		mDriveOI=DriveOI.get();
		mDrive=Drive.get();
		mLastDriveStraightActivated=false;
	}
	
	public void initAuto(int pos, boolean left) {
		mDrive.resetGyro();
		mCmds.clear();
		//mCmds.add(new DriveCmd(0, 0.8, 2000));
		
		//Formal
		if(pos==Robot.MIDDLE) {
			if(left) {
				mCmds.add(new DriveCmd(0, 0.6, 400));
				mCmds.add(new TurnToCmd(400, -70));
				mCmds.add(new DriveCmd(1900, 0.74,1100));
				mCmds.add(new TurnToCmd(3000, 0));
				mCmds.add(new DriveCmd(4500, 0.7, 500));
				//mCmds.add(new DriveCmd(9000, 0.45, 3000));//5000
				mCmds.add(new DriveCmd(11000, -0.5, 2000));//9000
			}else {
				mCmds.add(new DriveCmd(0, 0.6, 400));
				mCmds.add(new TurnToCmd(400, 70));
				mCmds.add(new DriveCmd(2000, 0.7,1100));
				mCmds.add(new TurnToCmd(3000, 0));
				mCmds.add(new DriveCmd(4500, 0.7, 500));
				//mCmds.add(new DriveCmd(9000, 0.45, 3000));//5000
				mCmds.add(new DriveCmd(11000, -0.5, 2000));//9000
			}
		}else if(pos==Robot.LEFT) {
			if(left) {
				mCmds.add(new DriveAlignCmd(0, 0.8, 0, 2000));
				mCmds.add(new TurnToCmd(2100, 90));
			}else {
				mCmds.add(new DriveAlignCmd(0, 0.8, 0, 1000));
				mCmds.add(new TurnToCmd(1050, 90));
				mCmds.add(new DriveAlignCmd(2600, 0.8, 90, 2500));
				mCmds.add(new TurnToCmd(5200, 0));
			}
		}else if(pos==Robot.RIGHT){
			if(left) {
				mCmds.add(new DriveAlignCmd(0, 0.8, 0, 2000));
				mCmds.add(new TurnToCmd(2100, -90));
			}else {
				mCmds.add(new DriveAlignCmd(0, 0.8, 0, 1000));
				mCmds.add(new TurnToCmd(1050, -90));
				mCmds.add(new DriveAlignCmd(2600, 0.8, -90, 2500));
				mCmds.add(new TurnToCmd(5200, 0));
			}
		}
		/*
		//Switch && Shoot from side
		if(!left) {
			mCmds.add(new DriveCmd(0, 0.6, 400));
			mCmds.add(new TurnToCmd(500, 45));
			mCmds.add(new DriveCmd(2000, 0.69, 2000));
			mCmds.add(new TurnToCmd(4000, 0));
			mCmds.add(new DriveCmd(6000, 0.63, 1000));
			mCmds.add(new TurnToCmd(7000, -90));
			mCmds.add(new DriveCmd(9000, 0.33, 2500));
		}else {	
			mCmds.add(new DriveCmd(0, 0.6, 400));
			mCmds.add(new TurnToCmd(500, -59));
			mCmds.add(new DriveCmd(2000, 0.65, 3000));
			mCmds.add(new TurnToCmd(5000, 0));
			mCmds.add(new DriveCmd(7000, 0.72, 980));
			mCmds.add(new TurnToCmd(8000, 90));
			mCmds.add(new DriveCmd(10000, 0.36, 2500));
		}
		*/
	}
	
	@Override
	public void stopAuto() {
		super.stopAuto();
		mDrive.stopMotor();
	}
	
	public void runTeleOp() {
		mDrive.log();
		double speed=mDriveOI.getSpeed();
		double turn=mDriveOI.getTurn();
		int direction=mDriveOI.getTurnAlignToCourt();
		if(!mDriveOI.getAbort()
				&& Math.abs(speed)<EPS 
				&& Math.abs(turn)<EPS) {
			switch (direction) {
				case DriveOI.DIRECTION_NOT_AVAILABLE:
					break;
				case DriveOI.DIRECTION_FORWARDS:
					mDrive.turnToAngle(0);
					break;
				case DriveOI.DIRECTION_RIGHT:
					mDrive.turnToAngle(90);		
					break;
				case DriveOI.DIRECTION_BACKWARDS:
					mDrive.turnToAngle(180);
						break;
				case DriveOI.DIRECTION_LEFT:
					mDrive.turnToAngle(270);
			}
			if(!mDrive.getPIDRunning())
				mDrive.drive(mDriveOI.getSpeed(),mDriveOI.getTurn());
		}else {
			mDrive.stopPID();
			if(mDriveOI.getDriveStraight()) {
				if(!mLastDriveStraightActivated) {
					mDrive.startDriveStraight();
				}
			}else {
				mDrive.endDriveStraight();
			}
			mDrive.drive(mDriveOI.getSpeed(),mDriveOI.getTurn());	
		}
	}
	
	/**
	 * Auto Commands
	 * */
	static class DriveCmd extends AutoCmd{
		private long mDuration;
		private double mSpeed;
		public DriveCmd(long startTimeStamp,double speed,long duration) {
			mStartTimestamp=startTimeStamp;
			mDuration=duration;
			mSpeed=speed;
		}
		@Override
		public void run(long time) {
			if(time<mDuration) {
				Drive.get().driveStraight(mSpeed);
			}else{
				Drive.get().stopMotor();
			}	
			super.run();

		}
		@Override
		public void stop() {
			super.stop();
			Drive.get().stopMotor();
		}
	}

	static class DriveAlignCmd extends AutoCmd{
		private long mDuration;
		private double mSpeed,mAngle;
		public DriveAlignCmd(long startTimeStamp,double speed,double angle,long duration) {
			mStartTimestamp=startTimeStamp;
			mDuration=duration;
			mSpeed=speed;
			mAngle=angle;
		}
		@Override
		public void run(long time) {
			if(time<mDuration) {
				Drive.get().driveAlignStraight(mSpeed, mAngle);
			}else{
				Drive.get().stopMotor();
			}	
			super.run();
		}
		@Override
		public void stop() {
			super.stop();
			Drive.get().stopMotor();
		}
	}
	
	static class TurnCmd extends AutoCmd{
		private double mAngle;
		public TurnCmd(long startTimeStamp,double angle) {
			super();
			mStartTimestamp=startTimeStamp;
			mAngle=angle;
		}
		@Override
		public void run(long time) {
			if(mStatus==Command.NOT_STARTED) {
				Drive.get().turnAngle(mAngle);
				super.run();
			}
		}
		@Override
		public void stop() {
			super.stop();
			Drive.get().stopMotor();
		}
	}
	
	static class TurnToCmd extends AutoCmd{
		private double mAngle;
		public TurnToCmd(long startTimeStamp,double angle) {
			super();
			mStartTimestamp=startTimeStamp;
			mAngle=angle;
		}
		@Override
		public void run(long time) {
			if(mStatus==Command.NOT_STARTED) {
				Drive.get().turnToAngle(mAngle);
				super.run();
			}
		}
		@Override
		public void stop() {
			super.stop();
			Drive.get().stopMotor();
		}
	}
}
