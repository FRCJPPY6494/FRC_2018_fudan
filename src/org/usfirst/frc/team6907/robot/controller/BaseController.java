package org.usfirst.frc.team6907.robot.controller;

import java.util.ArrayList;

import org.usfirst.frc.team6907.robot.commands.AutoCmd;
import org.usfirst.frc.team6907.robot.commands.Command;

public class BaseController {
	protected ArrayList<AutoCmd> mCmds;
	private int mCurrentCmd;
	
	public BaseController() {
		mCmds=new ArrayList<AutoCmd>();
		mCurrentCmd=0;
	}
	
	public void runAuto(long autoElapsedTime) {
		if(mCurrentCmd>mCmds.size()-1) {
			stopAuto();
			return;
		}
		while(mCurrentCmd<mCmds.size()-1
				&& mCmds.get(mCurrentCmd+1).getStartTimestamp()<=autoElapsedTime) {
			mCmds.get(mCurrentCmd).stop();
			mCurrentCmd++;
		}
		if(mCmds.get(mCurrentCmd).getStatus()==Command.NOT_STARTED) {
			if(autoElapsedTime>=mCmds.get(mCurrentCmd).getStartTimestamp()) {
				mCmds.get(mCurrentCmd).init();
				mCmds.get(mCurrentCmd)
						.run(autoElapsedTime-mCmds.get(mCurrentCmd).getStartTimestamp());
			}
		}else if(mCmds.get(mCurrentCmd).getStatus()==Command.RUNNING) {
			if(autoElapsedTime>=mCmds.get(mCurrentCmd).getStartTimestamp()) {
				mCmds.get(mCurrentCmd)
						.run(autoElapsedTime-mCmds.get(mCurrentCmd).getStartTimestamp());
			}
		}else if(mCmds.get(mCurrentCmd).getStatus()==Command.STOPPED) {
			mCmds.get(mCurrentCmd).stop();
			mCurrentCmd++;
		}
	}
	
	public void stopAuto() {
		mCurrentCmd=0;
		mCmds.clear();
	}
}
