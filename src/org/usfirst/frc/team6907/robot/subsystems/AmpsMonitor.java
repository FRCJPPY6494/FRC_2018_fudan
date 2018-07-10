package org.usfirst.frc.team6907.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class AmpsMonitor extends Thread{
	private final double MAX_AMPS = 5.0;
	private final long SLEEP_TIME = 10;
	private final int ERROR_TIME = 10;
	
	private WPI_TalonSRX mtalon;
	
	private double amps;
	private double speed;
	private int times;
	
	public AmpsMonitor() {
		amps = 0;
		speed = 0;
		times = 0;
		mtalon = new WPI_TalonSRX( 6 );
	}
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				speed +=0.002;
				mtalon.set(speed);
				amps = mtalon.getOutputCurrent();
				if( amps > MAX_AMPS ) times ++ ; else times = 0 ;
				if( times > ERROR_TIME ) {
					mtalon.set(0);
					break;
				}
				System.out.print( amps + " " + times + "\n" );
				sleep( SLEEP_TIME );
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

}
