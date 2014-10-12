package MotionDetectionUtility;

public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Point add(Point pt){
		return new Point(this.x+pt.x, this.y+pt.y);
	}
	
	public String toString(){
		return "{"+x+", "+y+"}";
	}
}
