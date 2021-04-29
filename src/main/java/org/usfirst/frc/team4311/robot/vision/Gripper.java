package org.usfirst.frc.team4311.robot.vision;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Gripper {

    private static int IMG_WIDTH = 320;
	private static int IMG_HEIGHT = 240;

    private VisionThread visionThread;
	private double centerX, centerY = 0.0;
	
	private final Object imgLock = new Object();

    public void Start() {
        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
    
        visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
            if (pipeline.findBlobsOutput().toArray().length != 0) {
                /*Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    centerX = r.x + (r.width / 2);
                }*/
                System.out.println("woodies pp");
            }
        });
        visionThread.start();
    }

    public void Update() {

    }
}