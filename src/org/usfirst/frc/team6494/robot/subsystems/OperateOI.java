package org.usfirst.frc.team6494.robot.subsystems;

import org.usfirst.frc.team6494.robot.RobotMap;
import org.usfirst.frc.team6494.robot.utils.Calc;
import org.usfirst.frc.team6494.robot.subsystems.OperateOI;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;

/**
 * This class handles all input given by the operator.
 * 
 * In FRC 2018 Team 6907, the Operator handles the Intaker, Elevator,
 * and the Climber.
 * */
public class OperateOI {
	private static final double 
			DEADBAND=0.065,			
			TRIGGER_BOUND=0.1;		
	
	public static final int
			ELEVATOR_ZERO=0,
			ELEVATOR_SWITCH=1,
			ELEVATOR_SCALE_LOW=2,
			ELEVATOR_SCALE_MIDDLE=3,
			ELEVATOR_SCALE_HIGH=4,
			THROTTLER_ZERO = 5,
			THROTTLER_LAUNCH = 6,
			THROTTLER_HORIZONTAL = 7;
			
			
	
	private static OperateOI sInstance;
	
	private XboxController mXboxController;
	
	public static OperateOI get() {
		if (sInstance==null || !sInstance.isTeleOpInputAvailable()) {
			sInstance=new OperateOI();
		}
		return sInstance;
	}
	
	public OperateOI() {
		mXboxController=new XboxController(RobotMap.OI_OPERATE);
	}
	
	//Over all
	
	public boolean isModeClimb() {
		return (mXboxController.getTriggerAxis(GenericHID.Hand.kLeft)>0.1
				&& mXboxController.getBumper(Hand.kLeft));
	}

	public boolean isResetActivated() {
		return mXboxController.getBumper(Hand.kLeft)
				&& mXboxController.getBumper(Hand.kRight)
				&& mXboxController.getTriggerAxis(GenericHID.Hand.kLeft)>0.1
				&& mXboxController.getTriggerAxis(GenericHID.Hand.kRight)>0.1;
	}	

	public boolean isElevatorManualActivated() {
		return mXboxController.getStartButton();
	}
	
	public int getElevatorSetPoint() {
		if(mXboxController.getBumper(Hand.kLeft)) {
			switch (mXboxController.getPOV()) {
			case 180:
				return ELEVATOR_SCALE_LOW;
			case 90:
				return ELEVATOR_SCALE_MIDDLE;
			case 270:
				return ELEVATOR_SCALE_MIDDLE;
			case 0:
				return ELEVATOR_SCALE_HIGH;
		}
		}else {
			switch (mXboxController.getPOV()) {
				case 180:
					return ELEVATOR_ZERO;
				case 0:
					return ELEVATOR_SWITCH;
			}
		}
		return -1;
	}
	
	public int getThrottlerSetPoint() {
			if(mXboxController.getYButton()) return THROTTLER_ZERO;
			else if(mXboxController.getBButton()) return THROTTLER_LAUNCH;
			else if(mXboxController.getAButton()) return THROTTLER_HORIZONTAL;
			return -1;
	}
	
	public double getElevatorSpeed() {
		return -Calc.eliminateDeadband(mXboxController.getY(Hand.kLeft),DEADBAND);
	}
	
	public double getThrottlerSpeed() {
		return -Calc.eliminateDeadband(mXboxController.getY(Hand.kRight),DEADBAND);
	}
	
	//Intaker
	public boolean isIntakerManualActivated() {
		return true;
	}
	
	public boolean isThrottlerManualActivated() {
		return mXboxController.getBackButton();
	}
	
	
	public boolean getIntakerAdjustDownwards() {
		return mXboxController.getStickButton(Hand.kRight);
	}
	

	public double getIntakerPitchSpeed() {
		return -Calc.eliminateDeadband(mXboxController.getY(Hand.kRight),DEADBAND);
	}
	
	public double getIntakerSpeed() {
		if(mXboxController.getBumper(Hand.kRight)) {
			return 1;
		}
		if(mXboxController.getTriggerAxis(Hand.kRight)>TRIGGER_BOUND) {
			if(mXboxController.getTriggerAxis(Hand.kLeft)<=TRIGGER_BOUND) {
				return -1;	
			}else {
				return -0.9;	
			}
		}
		return 0;
	}
	
	
	public boolean getClimberElevate() {
		return isModeClimb() && mXboxController.getYButton();
	}
	
	public boolean getClimberClimb() {
		return isModeClimb() && mXboxController.getAButton();
	}
	
	public boolean getClimberRelax() {
		return isModeClimb() && mXboxController.getBButton();
	}
	
	public double getManualRelaxSpeed() {
		if(isModeClimb()) {
			return mXboxController.getY(Hand.kRight)*0.4;
		}
		return 0;
	}

	public boolean isTeleOpInputAvailable() {
		return mXboxController!=null;
	}
}
