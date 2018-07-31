/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6494.robot;

import org.usfirst.frc.team6494.robot.controller.ClimberController;
import org.usfirst.frc.team6494.robot.controller.DriveController;
import org.usfirst.frc.team6494.robot.controller.ElevatorController;
import org.usfirst.frc.team6494.robot.controller.IntakerController;
import org.usfirst.frc.team6494.robot.controller.ThrottlerController;
import org.usfirst.frc.team6494.robot.subsystems.AmpsMonitor;
import org.usfirst.frc.team6494.robot.subsystems.Climber;
import org.usfirst.frc.team6494.robot.subsystems.Drive;
import org.usfirst.frc.team6494.robot.subsystems.DriveOI;
import org.usfirst.frc.team6494.robot.subsystems.Elevator;
import org.usfirst.frc.team6494.robot.subsystems.Intaker;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;
import org.usfirst.frc.team6494.robot.subsystems.Throttler;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {
	public static final int NONE=-1, LEFT=0, MIDDLE=1, RIGHT=2;
	
	
	private static int POS=MIDDLE;
	//**************   ^^^^^^^^^^   *****************//
	//************SET THIS BEFORE MATCH**************//
	
	private long mAutoStartTime;
	private AmpsMonitor ampsMonitor;
	
	
	private static String mPosDat;
	
	@Override
	public void robotInit() {
		/**Initialize camera*/

		UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture();
		UsbCamera camera2 = CameraServer.getInstance().startAutomaticCapture();
		camera1.setResolution(160, 120);
		camera1.setFPS(30);
		camera2.setResolution(160, 120);
		camera2.setFPS(30);
		DriveController.get();
		ElevatorController.get();
		IntakerController.get();
		ClimberController.get();
		ThrottlerController.get();
		
	}

	@Override
	public void autonomousInit() {
		mPosDat = DriverStation.getInstance().getGameSpecificMessage();
		int sPosition=NONE;
		if(mPosDat.length() > 0){
			if(mPosDat.charAt(0)=='L') sPosition=LEFT;
			else if(mPosDat.charAt(0)=='R') sPosition=RIGHT;
			else sPosition=NONE;
		}
			DriveController.get().initAuto(POS,sPosition);
			IntakerController.get().initAuto(POS,sPosition);
			ElevatorController.get().initAuto(POS,sPosition);
			ThrottlerController.get().initAuto(POS,sPosition);
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
		//ampsMonitor = new AmpsMonitor();
		//ampsMonitor.run();
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
