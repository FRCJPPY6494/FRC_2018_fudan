package org.usfirst.frc.team6494.robot.utils;

public class Calc {
	
	public static double eliminateDeadband(double i,double deadband) {
		if (Math.abs(i)<deadband) {
			return 0;
		}else {
			return (i-deadband*Math.signum(i))/(1-deadband);
		}
	}
}
