package majorDirectionAnalyzer;

import java.util.Comparator;

import MotionDetectionUtility.Vector;

public class VectorComparator implements Comparator<Vector>{
	private Vector referenceVector;
	
	public VectorComparator(Vector referenceVector){
		this.referenceVector = referenceVector;
	}
	
	@Override
	public int compare(Vector o1, Vector o2) {		
		double angle1 = angleBetweenVectors(o1,this.referenceVector);
		double angle2 = angleBetweenVectors(o2,this.referenceVector);
		
		if(angle1 == angle2){
			return 0;
		}else if(angle1 < angle2){
			return 1;
		}else{
			return -1;
		}
	}
	
	private double angleBetweenVectors(Vector v1, Vector v2){
		double dotProduct = v1.dx()*v2.dx() + v1.dy()*v2.dy();
		double modV1 = Math.sqrt(v1.dx()*v1.dx()+v1.dy()*v1.dy());
		double modV2 = Math.sqrt(v2.dx()*v2.dx()+v2.dy()*v2.dy());
		double cosineAngle = dotProduct / (modV1*modV2);
		return Math.acos(cosineAngle);
	}
}
