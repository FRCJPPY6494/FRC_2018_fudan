package org.usfirst.frc.team6907.robot.devices;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class Camera {
	
	public static void start() {
		start(320,240,20);
	}
	
	public static void start(int width,int height,int fps) {
		start(width, height, fps, 50);	
	}
	
	public static void start(int width,int height,int fps,int brightness) {
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(width, height);
		camera.setFPS(fps);
		camera.setBrightness(brightness);
	}
}