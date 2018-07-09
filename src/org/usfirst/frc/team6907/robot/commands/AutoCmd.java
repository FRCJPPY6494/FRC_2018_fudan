package org.usfirst.frc.team6907.robot.commands;

public class AutoCmd extends Command{
	protected long mStartTimestamp;
	
	public AutoCmd() {
		mStatus=NOT_STARTED;
	}
	
	public long getStartTimestamp() {
		return mStartTimestamp;
	}
	
	public void run(long cmdElapsedTime) {
		super.run();
	}
}
