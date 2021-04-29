package org.usfirst.frc.team4311.robot;

import java.awt.List;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class CameraManager {
	
	public static UsbCamera ViewingCamera;
	public static UsbCamera LineCamera;
	
	public static QuadLine LastIdentifiedLine;
	
	protected static CvSource LineProcessorOutput;
	protected static CvSink LineProcessorSink;
	protected static CvSink LineOutputSink;
	public static CvSource CameraProcessSource;
	
	public static int ColorThreshold = 130, DeviationThreshold = 102;
	//Auditorium = (150, 100)
	
	public static CvSource cameraOutputSource;
    
    public static void initialize() {
		//7.1inx6.45in, c = 8.5

    	ViewingCamera = CameraServer.getInstance().startAutomaticCapture(0);
		LineCamera = CameraServer.getInstance().startAutomaticCapture(1);
		LineCamera.setResolution(160, 120);
    	LineCamera.setExposureManual(40);
		LineProcessorSink = CameraServer.getInstance().getVideo();
		LineProcessorSink.setSource(LineCamera);
		VideoMode currentVideoMode = LineCamera.getVideoMode();
		CameraProcessSource = CameraServer.getInstance().putVideo("Annotated Image Processor", currentVideoMode.width, currentVideoMode.height);
		LineOutputSink = new CvSink("Annotated Image");
		LineOutputSink.setSource(CameraProcessSource);
		SmartDashboard.putNumber("Average", ColorThreshold);
		SmartDashboard.putNumber("Deviation", DeviationThreshold);
		System.out.println("Camera initialized.");
    }
    
    public static void UpdateCameraFeed() {
    	Mat lastImage = new Mat();
    	LineProcessorSink.grabFrame(lastImage);
    	if(lastImage.empty()) { return; }
    	LastIdentifiedLine = IdentifyLine(lastImage);
    	CameraProcessSource.putFrame(lastImage);
    }

    public static void execute() {
    	Mat lastImage = new Mat();
    	Mat currentImage = new Mat();
    	lastImage.copyTo(currentImage);
    	LineProcessorSink.grabFrame(lastImage);
    	if(lastImage.empty()) { return; }
    	/*byte[] bs = new byte[] { (byte)0, (byte)255, 0 };
    	for(int x = -4; x < 5; x++) {
    		lastImage.put(lastImage.height() / 2 + x, lastImage.width() / 2, bs);
    	}
    	for(int x = -4; x < 5; x++) {
    		lastImage.put(lastImage.height() / 2, lastImage.width() / 2 + x, bs);
    	}*/
    	
    	for(int x = 0; x < lastImage.width(); x++) {
    		for(int y = 0; y < lastImage.height(); y++) {
        		double[] ret = lastImage.get(y, x);
        		double avg = 0;
        		int size = 0;
        		for(;size < ret.length; size++) {
        			avg += ret[size];
        		}
        		avg /= size;
        		double stdDev = standardDeviation(ret);
        		if(avg < SmartDashboard.getNumber("Average", 120) || stdDev > SmartDashboard.getNumber("Deviation", 20)) {
        			lastImage.put(y, x, new byte[] { 0, 0, 0 });
        		}
        		else {
        			lastImage.put(y, x, new byte[] { (byte)255, (byte)255, (byte)255 });
        		}
        	}
    	}
    	
    	Imgproc.putText(lastImage, "[1 - Place Hatch]", new Point(10, lastImage.height() - 50), 0, 0.25D, new Scalar(0, 255, 255));
    	CameraProcessSource.putFrame(lastImage);
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
    
    public static double standardDeviation(double[] array) {
    	double sum = 0.0, standardDev = 0.0;
    	int length = array.length;
    	
    	for(double num : array) {
    		sum += num;
    	}
    	
    	double mean = sum / length;
    	
    	for(double num : array) {
    		standardDev += Math.pow(num - mean, 2);
    	}
    	
    	return Math.sqrt(standardDev / length);
    }
	
	public static QuadLine IdentifyLine(Mat image) {
		VideoMode currentVideoMode = LineCamera.getVideoMode();
		boolean[][] blackWhite = new boolean[currentVideoMode.width][currentVideoMode.height];
		int foundAmount = 0;
		for(int x = 0; x < image.width(); x++) {
    		for(int y = 0; y < image.height(); y++) {
        		double[] ret = image.get(y, x);
        		double avg = 0;
        		int size = 0;
        		for(;size < ret.length; size++) {
        			avg += ret[size];
        		}
        		avg /= size;
				double stdDev = StandardDeviation(ret);
				if(!(avg < ColorThreshold || stdDev > DeviationThreshold)) {
					blackWhite[x][y] = true;
					foundAmount++;
				}
        	}
		}
		
		if(foundAmount < 40) {
			return null;
		}

		ArrayList<Point> corners = new ArrayList<Point>();
		for(int x = 0; x < image.width(); x++) {
			if(blackWhite[x][0]) {
				boolean blackFound = false, whiteFound = false;
				if(x > 0) {
					if(!blackFound && !blackWhite[x - 1][0]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x - 1][0]) {
						whiteFound = true;
					}
				}
				if(x < image.width() - 1) {
					if(!blackFound && !blackWhite[x + 1][0]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x + 1][0]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[x][1]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[x][1]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(x, 0))) {
						corners.add(new Point(x, 0));
					}
					break;
				}
			}
		}
		for(int x = 0; x < image.width(); x++) {
			if(blackWhite[x][image.height() - 1]) {
				boolean blackFound = false, whiteFound = false;
				if(x > 0) {
					if(!blackFound && !blackWhite[x - 1][image.height() - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x - 1][image.height() - 1]) {
						whiteFound = true;
					}
				}
				if(x < image.width() - 1) {
					if(!blackFound && !blackWhite[x + 1][image.height() - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x + 1][image.height() - 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[x][image.height() - 2]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[x][image.height() - 2]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(x, image.height() - 1))) {
						corners.add(new Point(x, image.height() - 1));
					}
					break;
				}
			}
		}
		for(int y = 0; y < image.height(); y++) {
			if(blackWhite[0][y]) {
				boolean blackFound = false, whiteFound = false;
				if(y > 0) {
					if(!blackFound && !blackWhite[0][y - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[0][y - 1]) {
						whiteFound = true;
					}
				}
				if(y < image.height() - 1) {
					if(!blackFound && !blackWhite[0][y + 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[0][y + 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[1][y]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[1][y]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(0, y))) {
						corners.add(new Point(0, y));
					}
					break;
				}
			}
		}
		for(int y = 0; y < image.height(); y++) {
			if(blackWhite[image.width() - 1][y]) {
				boolean blackFound = false, whiteFound = false;
				if(y > 0) {
					if(!blackFound && !blackWhite[image.width() - 1][y - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[image.width() - 1][y - 1]) {
						whiteFound = true;
					}
				}
				if(y < image.height() - 1) {
					if(!blackFound && !blackWhite[image.width() - 1][y + 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[image.width() - 1][y + 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[image.width() - 2][y]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[image.width() - 2][y]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(image.width() - 1, y))) {
						corners.add(new Point(image.width() - 1, y));
					}
					break;
				}
			}
		}
		for(int x = image.width() - 1; x > -1; x--) {
			if(blackWhite[x][0]) {
				boolean blackFound = false, whiteFound = false;
				if(x > 0) {
					if(!blackFound && !blackWhite[x - 1][0]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x - 1][0]) {
						whiteFound = true;
					}
				}
				if(x < image.width() - 1) {
					if(!blackFound && !blackWhite[x + 1][0]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x + 1][0]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[x][1]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[x][1]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(x, 0))) {
						corners.add(new Point(x, 0));
					}
					break;
				}
			}
		}
		for(int x = image.width() - 1; x > -1; x--) {
			if(blackWhite[x][image.height() - 1]) {
				boolean blackFound = false, whiteFound = false;
				if(x > 0) {
					if(!blackFound && !blackWhite[x - 1][image.height() - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x - 1][image.height() - 1]) {
						whiteFound = true;
					}
				}
				if(x < image.width() - 1) {
					if(!blackFound && !blackWhite[x + 1][image.height() - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[x + 1][image.height() - 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[x][image.height() - 2]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[x][image.height() - 2]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(x, image.height() - 1))) {
						corners.add(new Point(x, image.height() - 1));
					}
					break;
				}
			}
		}
		for(int y = image.height() - 1; y > -1; y--) {
			if(blackWhite[0][y]) {
				boolean blackFound = false, whiteFound = false;
				if(y > 0) {
					if(!blackFound && !blackWhite[0][y - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[0][y - 1]) {
						whiteFound = true;
					}
				}
				if(y < image.height() - 1) {
					if(!blackFound && !blackWhite[0][y + 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[0][y + 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[1][y]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[1][y]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(0, y))) {
						corners.add(new Point(0, y));
					}
					break;
				}
			}
		}
		for(int y = image.height() - 1; y > -1; y--) {
			if(blackWhite[image.width() - 1][y]) {
				boolean blackFound = false, whiteFound = false;
				if(y > 0) {
					if(!blackFound && !blackWhite[image.width() - 1][y - 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[image.width() - 1][y - 1]) {
						whiteFound = true;
					}
				}
				if(y < image.height() - 1) {
					if(!blackFound && !blackWhite[image.width() - 1][y + 1]) {
						blackFound = true;
					}
					if(!whiteFound && blackWhite[image.width() - 1][y + 1]) {
						whiteFound = true;
					}
				}
				if(!blackFound && !blackWhite[image.width() - 2][y]) {
					blackFound = true;
				}
				if(!whiteFound && blackWhite[image.width() - 2][y]) {
					whiteFound = true;
				}
				if(blackFound && whiteFound) {
					if(!corners.contains(new Point(image.width() - 1, y))) {
						corners.add(new Point(image.width() - 1, y));
					}
					break;
				}
			}
		}
		QuadLine quadLine = null;
		if(corners.size() == 2) {
			CalculateAuxilaryCorners(corners, corners.get(0), corners.get(1), image, blackWhite);
			if(corners.size() == 4) {
				quadLine = new QuadLine();
				quadLine.ParallelA = new Line(corners.get(0), corners.get(3));
				quadLine.ParallelB = new Line(corners.get(1), corners.get(2));
				quadLine.EndPoint = new Line(corners.get(2), corners.get(3));
			}
		}
		Imgproc.putText(image, new Integer(corners.size()).toString(), new Point(10, 10), 0, 0.5, new Scalar(0, 0, 0));
		for(Point p : corners) {
			p.x = (p.x / image.width()) * 7.1;
			p.y = (p.y / image.height()) * 6.45;
			p.x -= 3.55;
			p.y =-p.y;
			p.y += 3.225;
		}
		return quadLine;
	}
	
	public static void DrawLine(Mat image, byte[] c, int x1, int y1, int x2, int y2) {
        byte[] color = c;

        int deltax = Math.abs(x2 - x1);
        int deltay = Math.abs(y2 - y1);
        int error = 0;
        int y = y1;
        for( int x=x1; x<x2; x++) {
            image.put(y, x, color);
            error = error + deltay;
            if( 2*error >= deltax ) {
                y = y + 1;
                error=error - deltax;
            }
        }
    }
	
	public static void CalculateAuxilaryCorners(ArrayList<Point> corners, Point ca, Point cb, Mat image, boolean[][] blackWhite) {
		double da = 0;
		double db = 0;
		
		Point ma = null;
		Point mb = null;
		
		for(int y = 0; y < image.height(); y++) {
			for(int x = 0; x < image.width(); x++) {
				if(blackWhite[x][y]) {
					if(da < DistanceSquared(ca, new Point(x, 0))) {
						da = DistanceSquared(ca, new Point(x, 0));
						ma = new Point(x, y);
					}
					if(db < DistanceSquared(cb, new Point(x, y))) {
						db = DistanceSquared(cb, new Point(x, y));
						mb = new Point(x, y);
					}
					break;
				}
			}
		}
		
		for(int y = 0; y < image.height(); y++) {
			for(int x = image.width() - 1; x > -1; x--) {
				if(blackWhite[x][y]) {
					if(da < DistanceSquared(ca, new Point(x, y))) {
						da = DistanceSquared(ca, new Point(x, y));
						ma = new Point(x, y);
					}
					if(db < DistanceSquared(cb, new Point(x, y))) {
						db = DistanceSquared(cb, new Point(x, y));
						mb = new Point(x, y);
					}
					break;
				}
			}
		}
		if(!ma.equals(null) ) {
			corners.add(ma);
		}
		if(!mb.equals(null) ) {
			corners.add(mb);
		}
	}
	
	public static double DistanceSquared(Point a, Point b) {
		return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
	}
	
	public static double StandardDeviation(double[] array) {
    	double sum = 0.0, standardDev = 0.0;
    	int length = array.length;
    	
    	for(double num : array) {
    		sum += num;
    	}
    	
    	double mean = sum / length;
    	
    	for(double num : array) {
    		standardDev += Math.pow(num - mean, 2);
    	}
    	
    	return Math.sqrt(standardDev / length);
    }
}
