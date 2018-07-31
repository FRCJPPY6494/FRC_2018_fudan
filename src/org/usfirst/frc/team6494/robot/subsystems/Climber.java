package org.usfirst.frc.team6494.robot.subsystems;

import org.usfirst.frc.team6494.robot.RobotMap;
import org.usfirst.frc.team6494.robot.subsystems.Climber;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;

public class Climber {
	
	private static final double
		SPEED_ELEVATE_SPARK=0,
		SPEED_ELEVATE_VICTOR=0.3,//0.3
		SPEED_CLIMB_SPARK=0.7,
		SPEED_CLIMB_VICTOR=-0.15,
		SPEED_RELAX_SPARK=-0.35,
		SPEED_RELAX_VICTOR=0;
	
//	SPEED_ELEVATE_SPARK=0.3,
//	SPEED_ELEVATE_VICTOR=0,
//	SPEED_CLIMB_SPARK=-0.15,
//	SPEED_CLIMB_VICTOR=0.7,
//	SPEED_RELAX_SPARK=-0.15,
//	SPEED_RELAX_VICTOR=0.35;
	
	private static final double EXPIRATION=0.8;
	
	private static Climber sInstance;
	
	private WPI_VictorSPX mSpark;	
	private VictorSP mVictor;	
	
	public static Climber get() {
		if(sInstance==null)sInstance=new Climber();
		return sInstance;
	}
	
	public Climber() {
		mSpark=new WPI_VictorSPX(RobotMap.CLIMBER_SPARK);
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
	
	public void manualrelax(double speed) {
		System.out.println("Power out: "+ speed);
		mVictor.set(speed);
	}
	
	public void feedStop() {
		mSpark.set(0);
		mVictor.set(0);
	}	
}
