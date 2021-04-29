package org.usfirst.frc.team4311.robot;

import org.opencv.core.Point;

public class Line {
	public Point A, B;
	
	public Line() { }
	
	public Line(Point a, Point b) {
		A = a;
		B = b;
	}
	
	public double GetSlope() {
		return (A.y - B.y)/(A.x - B.x);
	}
	
	public String toString() {
		return "Slope " + new Double(GetSlope()).toString() + ", (" + new Double(A.x).toString() + "," + new Double(A.y).toString() + ") (" + new Double(B.x).toString() + "," + new Double(B.y).toString();
	}
	
	public boolean EqualsLine(Line other) {
		return (A.equals(other.A) && B.equals(other.B)) || (A.equals(other.B) && B.equals(other.A));
	}
}
