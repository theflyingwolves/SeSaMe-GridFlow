package MotionDetectionUtility;

import org.opencv.core.Point;

public class Vector {
	private Point vector;
	public Vector(double dx, double dy){
		vector = new Point(dx,dy);
	}
	
	public double dx(){
		return vector.x;
	}
	
	public double dy(){
		return vector.y;
	}
	
	public Vector getNormedVector(){
		double length = Math.sqrt(vector.x*vector.x+vector.y*vector.y);
		if(length == 0){
			return new Vector(0,0);
		}else{
			return new Vector(vector.x/length, vector.y / length);
		}
	}
	
	public Vector scale(double scaleFactor){
		return new Vector(vector.x*scaleFactor,vector.y*scaleFactor);
	}
	
	public Vector add(Vector v){
		return new Vector(this.dx()+v.dx(),this.dy()+v.dy());
	}
	
	public String toString(){
		return "("+this.dx()+", "+this.dy()+")";
	}
}