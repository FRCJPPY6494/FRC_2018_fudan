package org.usfirst.frc.team6907.robot.devices;

import edu.wpi.first.wpilibj.DigitalOutput;

public class ArduinoLEDStrip {
	public static final int
			MODE_NORMAL=0,
			MODE_ELEVATE_UP=1,
			MODE_ELEVATE_DOWN=2;
	
	private static final long[] DURATION= {20,40,60};
	private static final long DURATION_RESET=20;
	
	private static final boolean[] mSignal= {false,true,false};

	private DigitalOutput mOutput;
	
	private Thread mThread;
	
	private volatile int mTargetMode;
	private int mMode,mCurrentTask;
	private long[] mTimeStamp= {0,0,0};
	
	public ArduinoLEDStrip(int channel) {
		mOutput=new DigitalOutput(channel);
	}
	
	public void start() {
		mCurrentTask=0;
		mOutput.set(false);
		mMode=0;
		mTargetMode=0;
		mThread = new Thread(() -> {
			while (!Thread.interrupted()) {
				if(mMode!=mTargetMode) {
					mCurrentTask=0;
					mTimeStamp[0]=System.currentTimeMillis();
					mTimeStamp[1]=mTimeStamp[0]+DURATION_RESET;
					mTimeStamp[2]=mTimeStamp[1]+DURATION[mTargetMode];
					mMode=mTargetMode;
				}
				if(mCurrentTask<3) {
					if(mTimeStamp[mCurrentTask]<System.currentTimeMillis()) {
						mOutput.set(mSignal[mCurrentTask]);
						mCurrentTask++;
					}
				}		
			}
		});
		mThread.setDaemon(true);
		mThread.start();
	}
	
	public void setMode(int mode) {
		mTargetMode=mode;
	}
}
