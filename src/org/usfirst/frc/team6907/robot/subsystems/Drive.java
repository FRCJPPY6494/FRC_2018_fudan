package org.usfirst.frc.team6907.robot.subsystems;

import org.usfirst.frc.team6907.robot.RobotMap;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * In FRC 2018, our team uses DifferentialDrive with Spark as 
 * motor controller, so changes shall be made were other teams
 * to utilize this class under other circumstances.
 * 
 * Copyright (c) 2018 Team 6907 (The G.O.A.T.)
 * */
public class Drive implements PIDSource,PIDOutput{
	private static final double 
			FRICTION=0.15,
			EPS=0.00001,
			MAX_ACCELERATION_PER_MS=0.004,	
			MAX_ACCELERATION_DISCRETE=0.1,
			DIRECTION=1,
			MANUAL_ANGLE_ADJUSTION=0.04,
			MAX_PID_OUTPUT=0.7,
			MIN_PID_OUTPUT=0.01;
	
	private static final double EPS_PID=1;

	private static Drive sInstance;
	
	private DifferentialDrive mDifferentialDrive;
	private Spark mSparkL,mSparkR;
	private ADXRS450_Gyro mGyro;
	
	private double mLastSpeed;
	private long mLastTime;
	
	private boolean mAdjustActivated;
	private double mAngle;

	private PIDController mPidController;
	private volatile boolean mPIDRunning;
	
	public static Drive get() {
		if(sInstance==null) sInstance=new Drive();
		return sInstance;
	}
	
	public Drive() {
		mGyro=new ADXRS450_Gyro();
		mGyro.setPIDSourceType(PIDSourceType.kDisplacement);
		mSparkL=new Spark(RobotMap.DRIVE_MOTOR_LEFT);
		mSparkR=new Spark(RobotMap.DRIVE_MOTOR_RIGHT);
		mDifferentialDrive=new DifferentialDrive(mSparkL,mSparkR);
	
		mPidController=new PIDController(0.06,0,0.002,mGyro,this);
		mPidController.setOutputRange(-MAX_PID_OUTPUT,MAX_PID_OUTPUT);
		mPIDRunning=false;
		mLastSpeed=0;
		mLastTime=System.currentTimeMillis();
		mAdjustActivated=false;
	}
	
	public void drive(double speed,double turn) {
		speed=Math.signum(speed)*speed*speed;
		speed=boundAcceleration(speed);
		mLastSpeed=speed;
		mLastTime=System.currentTimeMillis();
		if(mAdjustActivated) {
			turn=(mAngle-mGyro.getAngle())*MANUAL_ANGLE_ADJUSTION;
		}	
		speed*=DIRECTION;
		double speedL=speed+turn;
		double speedR=speed-turn;
		mDifferentialDrive.tankDrive(clearFriction(speedL),clearFriction(speedR),false);
	}
	
	public void resetGyro() {
		mGyro.reset();
	}
	
	public void turnAngle(double angle) {
		mPidController.reset();
		mPidController.setSetpoint(mGyro.getAngle()+angle);
		mPIDRunning=true;
		mPidController.enable();
	}
	
	public void turnToAngle(double angle) {
		double rawAngle=mGyro.getAngle();
		double currentAngle=regulateAngle(rawAngle);
		angle=regulateAngle(angle);
		double targetAngle=angle;
		if(Math.abs(targetAngle-currentAngle)>Math.abs(angle+360-currentAngle)) {
			targetAngle=angle+360;
		}
		if(Math.abs(targetAngle-currentAngle)>Math.abs(angle-360-currentAngle)) {
			targetAngle=angle-360;
		}
		if(Math.abs((rawAngle+targetAngle-currentAngle)-mPidController.getSetpoint())>EPS_PID
				|| !mPidController.isEnabled()) {
			mPidController.reset();
			mPidController.setSetpoint(rawAngle+targetAngle-currentAngle);
		}
		mPidController.enable();
		mPIDRunning=true;
	}
	
	private double regulateAngle(double angle) {
		return angle-360.0*((int)angle/360);
	}
	
	public void driveStraight(double speed) {
		stopPID();
		if(!mAdjustActivated)startDriveStraight();
		drive(speed, 0);
	}
	
	public void driveAlignStraight(double speed,double angle) {
		stopPID();
		if(!mAdjustActivated)startDriveStraight(angle);
		drive(speed, 0);
	}
	
	public void startDriveStraight() {
		mAngle=mGyro.getAngle();
		mAdjustActivated=true;
	}
	
	public void startDriveStraight(double angle) {
		double rawAngle=mGyro.getAngle();
		double currentAngle=regulateAngle(rawAngle);
		angle=regulateAngle(angle);
		double targetAngle=angle;
		if(Math.abs(targetAngle-currentAngle)>Math.abs(angle+360-currentAngle)) {
			targetAngle=angle+360;
		}
		if(Math.abs(targetAngle-currentAngle)>Math.abs(angle-360-currentAngle)) {
			targetAngle=angle-360;
		} 
		mAngle=rawAngle+targetAngle-currentAngle;
		mAdjustActivated=true;
	}
	
	public void endDriveStraight() {
		mAdjustActivated=false;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {}

	@Override
	public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

	@Override
	public double pidGet() {
		return mGyro.getAngle();
	}
	
	@Override
	public void pidWrite(double output) {
		SmartDashboard.putNumber("DrivePIDOut",output);
		if(Math.abs(output)<MIN_PID_OUTPUT) output=0;
		output=DIRECTION*clearFriction(MAX_PID_OUTPUT*output);
		mDifferentialDrive.tankDrive(output,-output);
		mLastSpeed=0;
		mLastTime=System.currentTimeMillis();
	}	
	
	public void stopPID() {
		if(mPIDRunning)mPidController.disable();
		mPIDRunning=false;
	}
	
	public boolean getPIDRunning() {
		return mPIDRunning;
	}
	
	private double boundAcceleration(double speed) {
		double bound=(System.currentTimeMillis()-mLastTime)*MAX_ACCELERATION_PER_MS;
		bound=Math.min(bound,MAX_ACCELERATION_DISCRETE);
		if(Math.abs(speed-mLastSpeed)>bound) {
			speed=mLastSpeed+Math.signum(speed-mLastSpeed)*bound;
		}
		return speed;
	}
	
	private double clearFriction(double speed) {
		if(Math.abs(speed)<EPS)return 0;
		return FRICTION*Math.signum(speed)+speed*(1-FRICTION);
	}
	
	public void feedStop() {
		stopPID();
		endDriveStraight();
		mDifferentialDrive.tankDrive(0,0);
	}
	
	public void stopMotor() {
		stopPID();
		endDriveStraight();
		mDifferentialDrive.stopMotor();
	}
	
	public void log() {
		SmartDashboard.putData(mGyro);	
		SmartDashboard.putNumber("Gyro", mGyro.getAngle());
	}
}