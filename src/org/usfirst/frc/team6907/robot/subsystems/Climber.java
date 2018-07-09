package org.usfirst.frc.team6907.robot.subsystems;

import org.usfirst.frc.team6907.robot.RobotMap;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;

public class Climber {
	
	private static final double
		SPEED_ELEVATE_SPARK=0.3,
		SPEED_ELEVATE_VICTOR=0,
		SPEED_CLIMB_SPARK=-0.15,
		SPEED_CLIMB_VICTOR=0.7,
		SPEED_RELAX_SPARK=-0.15,
		SPEED_RELAX_VICTOR=0.35;
	
	private static final double EXPIRATION=0.8;
	
	private static Climber sInstance;
	
	private Spark mSpark;	
	private VictorSP mVictor;	
	
	public static Climber get() {
		if(sInstance==null)sInstance=new Climber();
		return sInstance;
	}
	
	public Climber() {
		mSpark=new Spark(RobotMap.CLIMBER_SPARK);
		mVictor=new VictorSP(RobotMap.CLIMBER_VICTOR);
		mSpark.setExpiration(EXPIRATION);
		mVictor.setExpiration(EXPIRATION);
		mSpark.setSafetyEnabled(true);
		mVictor.setSafetyEnabled(true);
	}
	
	public void elevate() {
		mSpark.set(SPEED_ELEVATE_SPARK);
		mVictor.set(SPEED_ELEVATE_VICTOR);
	}
	
	public void climb() {
		mSpark.set(SPEED_CLIMB_SPARK);
		mVictor.set(SPEED_CLIMB_VICTOR);
	}

	public void relax() {
		mSpark.set(SPEED_RELAX_SPARK);
		mVictor.set(SPEED_RELAX_VICTOR);
	}
	
	public void feedStop() {
		mSpark.set(0);
		mVictor.set(0);
	}	
}
