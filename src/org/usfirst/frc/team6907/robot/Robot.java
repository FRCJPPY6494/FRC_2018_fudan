/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6907.robot;

import org.usfirst.frc.team6907.robot.controller.ClimberController;
import org.usfirst.frc.team6907.robot.controller.DriveController;
import org.usfirst.frc.team6907.robot.controller.ElevatorController;
import org.usfirst.frc.team6907.robot.controller.IntakerController;
import org.usfirst.frc.team6907.robot.controller.ThrottlerController;
import org.usfirst.frc.team6907.robot.devices.Camera;
import org.usfirst.frc.team6907.robot.subsystems.Climber;
import org.usfirst.frc.team6907.robot.subsystems.Drive;
import org.usfirst.frc.team6907.robot.subsystems.DriveOI;
import org.usfirst.frc.team6907.robot.subsystems.Elevator;
import org.usfirst.frc.team6907.robot.subsystems.Intaker;
import org.usfirst.frc.team6907.robot.subsystems.OperateOI;
import org.usfirst.frc.team6907.robot.subsystems.Throttler;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {
	public static final int LEFT=0, MIDDLE=1, RIGHT=2;
	private static int POS=MIDDLE; 
	
	private long mAutoStartTime;
	
	
	private static String mPosDat;
	
	@Override
	public void robotInit() {
		/**Initialize camera*/
		Camera.start(160,120,20);
		Camera.start(160,120,20);
		DriveController.get();
		ElevatorController.get();
		IntakerController.get();
		ClimberController.get();
		ThrottlerController.get();
	}

	@Override
	public void autonomousInit() {
		mPosDat = DriverStation.getInstance().getGameSpecificMessage();
		if(mPosDat.length() > 0){
			boolean isLeft;
			if(POS==MIDDLE) {
				isLeft=(mPosDat.charAt(0) == 'L');
			}else {
				isLeft=(mPosDat.charAt(1) == 'L');
			}
			DriveController.get().initAuto(POS,isLeft);
			IntakerController.get().initAuto(POS,isLeft);
			ElevatorController.get().initAuto(POS,isLeft);
			ThrottlerController.get().initAuto();
		}
		mAutoStartTime=System.currentTimeMillis();
	}

	@Override
	public void autonomousPeriodic() {
		long autoElapsedTime=System.currentTimeMillis()-mAutoStartTime;
		DriveController.get().runAuto(autoElapsedTime);
		ElevatorController.get().runAuto(autoElapsedTime);
		IntakerController.get().runAuto(autoElapsedTime);
		Climber.get().feedStop();
		ThrottlerController.get().runAuto(autoElapsedTime);
	}

	@Override
	public void teleopInit() {
		DriveController.get().stopAuto();
		ElevatorController.get().stopAuto();
		IntakerController.get().stopAuto();
		ThrottlerController.get().stopAuto();
		DriveOI.get();
		OperateOI.get();
	}

	@Override
	public void teleopPeriodic() {
		DriveController.get().runTeleOp();
		if(OperateOI.get().isResetActivated()) {
			Elevator.get().reset();
			Climber.get().feedStop();
			Elevator.get().feedStop();
			Intaker.get().feedStop();
			Throttler.get().feedStop();
		}else if(OperateOI.get().isModeClimb()) {
			ClimberController.get().runTeleOp();
			Elevator.get().feedStop();
			Intaker.get().feedStop();
			Throttler.get().feedStop();
		}else {
			ElevatorController.get().runTeleOp();
			IntakerController.get().runTeleOp();
			Climber.get().feedStop();
			ThrottlerController.get().runTeleOp();
		}	
	}
	

	@Override
	public void disabledInit() {
		DriveController.get().stopAuto();
		ElevatorController.get().stopAuto();
		IntakerController.get().stopAuto();
		ThrottlerController.get().stopAuto();
	}
	
	@Override
	public void disabledPeriodic() {
		Drive.get().feedStop();
		Elevator.get().feedStop();
		Intaker.get().stopMotor();
		Climber.get().feedStop();
		Throttler.get().feedStop();
	}
}
