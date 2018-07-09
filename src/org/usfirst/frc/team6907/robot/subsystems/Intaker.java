package org.usfirst.frc.team6907.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team6907.robot.RobotMap;
import org.usfirst.frc.team6907.robot.commands.Command;
import org.usfirst.frc.team6907.robot.devices.Photogate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Spark;

public class Intaker {
	private static final double 
			MAX_PITCH_SPEED_MANUAL_UP=0.3,
			MAX_PITCH_SPEED_MANUAL_DOWN=0.75,
			PITCH_SPEED_GOTO_VERTICAL=0.77,
			PITCH_SPEED_GOTO_HORIZONTAL=0.55,
			SLOW_PITCH_SPEED=0.25,
			PITCH_SPEED_GOTO_SHOOT=0.2,
			INTAKER_PITCH_DIRECTION=-1,
			PITCH_SPEED_ADJUST_DOWNWARDS=0.16;
	private static final long 
			RUN_TIME_GOTO_BLOCKED=400,
			RUN_TIME_VERTICAL=600,
			RUN_TIME_VERTICAL_COMPENSATION=100,
			RUN_TIME_RESET_VERTICAL_COMPENSATION=600,
			RUN_TIME_HORIZONTAL_COMPENSATION=100,
			RUN_TIME_GOTO_SHOOT_PITCH=720,	
			RUN_TIME_ADJUST_DOWNWARDS=200,
			MAX_RUN_TIME_GOTO_HORIZONTAL=780,
			MAX_RUN_TIME_GOTO_VERTICAL=1900,
			MAX_RUN_TIME_RESET_VERTICAL=1600;
	
	private static final double 
			EXPIRATION=1;
	private static final int
			CMD_VERTICAL=0,
			CMD_SHOOT_PITCH=1,	
			CMD_HORIZONTAL=2,
			CMD_RESET_TO_VERTICAL=3,
			CMD_ADJUST_DOWNWARDS=4;	
	
	private static Intaker sIntaker;	
	
	private Photogate mPhotogate;
	private Spark mSpark;
	private WPI_TalonSRX mTalon;
	
	private Timer mTimer;
	private TimerTask mTimerTask;
	private volatile Command mCommand;
	private int mCmdMode;
	private GoToShootPitchCmd mGotoShootPitchCmd;
	private GotoHorizontalCmd mGotoHorizontalCmd;
	private GotoVerticalCmd mGotoVerticalCmd;
	private ResetToVerticalCmd mResetToVerticalCmd;
	private AdjustDownwardsCmd mAdjustDownwardsCmd;
	
	private volatile boolean isManual=false;
	
	public static Intaker get() {
		if(sIntaker==null) sIntaker=new Intaker();
		return sIntaker;
	}
	
	public Intaker() {
		mPhotogate=new Photogate(RobotMap.INTAKER_PHOTOGATE);
		mSpark=new Spark(RobotMap.INTAKER_SPARK);
		mTalon=new WPI_TalonSRX(RobotMap.INTAKER_TALON);
		mTalon.setSafetyEnabled(true);
		mTalon.setExpiration(EXPIRATION);
		mGotoShootPitchCmd=new GoToShootPitchCmd();
		mGotoHorizontalCmd=new GotoHorizontalCmd();
		mGotoVerticalCmd=new GotoVerticalCmd();
		mResetToVerticalCmd=new ResetToVerticalCmd();	
		mAdjustDownwardsCmd=new AdjustDownwardsCmd();
		mCommand=mGotoShootPitchCmd;	
		mTimer=new Timer();
		mTimerTask=new TimerTask() {
			@Override
			public void run() {
				if(mCommand!=null) {
					synchronized(mCommand) {
						if(mCommand.getStatus()==Command.RUNNING) {
							mCommand.run();
						}else {
							if(!isManual)mTalon.set(ControlMode.PercentOutput,0);
						}
					}	
				}else {
					if(!isManual) mTalon.set(ControlMode.PercentOutput,0);
				}
			}
		};
		mTimer.scheduleAtFixedRate(mTimerTask, 0, 20);
	}
	
	public void setIsManual(boolean manual) {
		isManual=manual;
	}
	
	public void stopMotor() {
		mCommand.stop();
		mSpark.stopMotor();
		mTalon.stopMotor();
	}	
	
	public void feedStop() {
		mCommand.stop();
		mSpark.set(0);
		mTalon.set(ControlMode.PercentOutput,0);
	}
	
	public void gotoVertical() {
		synchronized(mCommand) {
			if(!(mCmdMode == CMD_VERTICAL)) {
				mCmdMode = CMD_VERTICAL;
				mCommand=mGotoVerticalCmd;
				mCommand.init();
				mCommand.run();
			}
		}
	}
	
	public void gotoHorizontal() {
		synchronized(mCommand) {
			if(!(mCmdMode==CMD_HORIZONTAL)) {
				mCmdMode=CMD_HORIZONTAL;
				mCommand=mGotoHorizontalCmd;
				mCommand.init();
				mCommand.run();
			}
		}
	}
	
	public void gotoShootPitch() {
		synchronized(mCommand) {
			if(!(mCmdMode==CMD_SHOOT_PITCH)
					&& !(mCmdMode==CMD_HORIZONTAL)) {
				mCmdMode=CMD_SHOOT_PITCH;
				mCommand=mGotoShootPitchCmd;
				mCommand.init();
				mCommand.run();
			}
		}
	}
	
	public void resetToVertical() {
		synchronized(mCommand) {
			if(!(mCmdMode == CMD_RESET_TO_VERTICAL)) {
				mCmdMode = CMD_RESET_TO_VERTICAL;
				mCommand=mResetToVerticalCmd;		
				mCommand.init();
				mCommand.run();
			}
		}
	}
	
	public void adjustDownwards() {
		synchronized(mCommand) {
			if(!(mCmdMode == CMD_ADJUST_DOWNWARDS)) {
				mCmdMode = CMD_ADJUST_DOWNWARDS;
				mCommand=mAdjustDownwardsCmd;		
				mCommand.init();
				mCommand.run();
			}
		}
	}
	
	public void setRawIntakerPitch(double speed) {
		speed*=(speed>0)?MAX_PITCH_SPEED_MANUAL_UP:MAX_PITCH_SPEED_MANUAL_DOWN;
		mTalon.set(ControlMode.PercentOutput, INTAKER_PITCH_DIRECTION*speed);
	}
	
	public void setRawIntakerSpeed(double speed) {
		mSpark.set(speed);
	}
	
	private class GotoVerticalCmd extends Command{	
		boolean mHasVerticalReached;
		@Override
		public void init() {
			super.init();
			mHasVerticalReached=false;
		}
		@Override
		public void run() {
			super.run();
			if(getTime()>MAX_RUN_TIME_GOTO_VERTICAL) {
				stop();
				mTalon.set(ControlMode.PercentOutput,0);
				return;
			}
			if(!mHasVerticalReached) {
				if (getTime()<RUN_TIME_VERTICAL || mPhotogate.isBlocked()) {
					mTalon.set(PITCH_SPEED_GOTO_VERTICAL*INTAKER_PITCH_DIRECTION);
				}else if(!mPhotogate.isBlocked()) {
					mHasVerticalReached=true;
					mTimeStart=System.currentTimeMillis();
				}
			}else {
				if (getTime()<RUN_TIME_VERTICAL_COMPENSATION) {
					mTalon.set(PITCH_SPEED_GOTO_VERTICAL*INTAKER_PITCH_DIRECTION);
				}else {
					stop();
					mTalon.set(ControlMode.PercentOutput,0);
				}
			}
		}
	}
	
	private class ResetToVerticalCmd extends Command{
		boolean mHasVerticalReached;
		@Override
		public void init() {
			super.init();
			mHasVerticalReached=false;
		}
		@Override
		public void run() {
			super.run();
			if(getTime()>MAX_RUN_TIME_RESET_VERTICAL) {
				stop();
				mTalon.set(ControlMode.PercentOutput,0);
				return;
			}
			if(!mHasVerticalReached) {
				if (getTime()<RUN_TIME_VERTICAL || mPhotogate.isBlocked()) {
					mTalon.set(SLOW_PITCH_SPEED*INTAKER_PITCH_DIRECTION);
				}else if(!mPhotogate.isBlocked()) {
					mHasVerticalReached=true;
					mTimeStart=System.currentTimeMillis();
				}
			}else {
				if (getTime()<RUN_TIME_RESET_VERTICAL_COMPENSATION) {
					mTalon.set(SLOW_PITCH_SPEED*INTAKER_PITCH_DIRECTION);
				}else {
					stop();
					mTalon.set(ControlMode.PercentOutput,0);
				}
			}
		}
	}
	
	private class GotoHorizontalCmd extends Command{
		boolean mHasHorizontalReached;
		@Override
		public void init() {
			super.init();
			mHasHorizontalReached=false;
		}
		@Override
		public void run() {
			super.run();
			if (getTime()<MAX_RUN_TIME_GOTO_HORIZONTAL) { 
				if(!mHasHorizontalReached) {
					if (getTime()<RUN_TIME_GOTO_BLOCKED || mPhotogate.isBlocked()) {
						mTalon.set(-PITCH_SPEED_GOTO_HORIZONTAL*INTAKER_PITCH_DIRECTION);
					}else if(!mPhotogate.isBlocked()) {
						mHasHorizontalReached=true;
						mTimeStart=System.currentTimeMillis();
					}
				}else {
					if (getTime()<RUN_TIME_HORIZONTAL_COMPENSATION) {
						mTalon.set(-PITCH_SPEED_GOTO_HORIZONTAL*INTAKER_PITCH_DIRECTION);
					}else {
						stop();
						mTalon.set(ControlMode.PercentOutput,0);
					}
				}
			}else {
				stop();
				mTalon.set(ControlMode.PercentOutput,0);
			}
		}
	}
	
	private class GoToShootPitchCmd extends Command{
		@Override
		public void run() {
			super.run();
			if (getTime()<RUN_TIME_GOTO_SHOOT_PITCH) {
				mTalon.set(-PITCH_SPEED_GOTO_SHOOT*INTAKER_PITCH_DIRECTION);
			}else {
				stop();
				mTalon.set(ControlMode.PercentOutput,0);
			}
		}
	}
	
	private class AdjustDownwardsCmd extends Command{
		@Override
		public void run() {
			super.run();
			if (getTime()<RUN_TIME_ADJUST_DOWNWARDS) {
				mTalon.set(-PITCH_SPEED_ADJUST_DOWNWARDS*INTAKER_PITCH_DIRECTION);
			}else {
				stop();
				mTalon.set(ControlMode.PercentOutput,0);
			}
		}
	}
	
	public void log() {
		mPhotogate.log();
	}
}
