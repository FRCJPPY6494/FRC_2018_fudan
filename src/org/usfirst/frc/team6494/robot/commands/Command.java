package org.usfirst.frc.team6494.robot.commands;

public class Command {
	public static final int
			NOT_STARTED=0,
			RUNNING=1,
			STOPPED=2;
	
	protected int mStatus;
	
	protected long mTimeStart;
	
	public Command() {
		mStatus=NOT_STARTED;
	}
	
	public void init() {
		mStatus=NOT_STARTED;
		mTimeStart=System.currentTimeMillis();
	}
	
	public void run() {
		mStatus=RUNNING;
	}
	
	public void stop() {
		mStatus=STOPPED;
	}
	
	public int getStatus() {
		return mStatus;
	}
	
	public long getTime() {
		return System.currentTimeMillis()-mTimeStart;
	}
}
