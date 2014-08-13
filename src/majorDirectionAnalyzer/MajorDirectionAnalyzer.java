package majorDirectionAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import MotionDetectionUtility.Vector;

public class MajorDirectionAnalyzer {
	private static final double THRESHOLD = 0.8;
	private List<Vector> dirFeeds;
	private Vector majorDir;
	
	public MajorDirectionAnalyzer(ArrayList<Vector> feed){
		dirFeeds = feed;
		majorDir = null;
	}
	
	public Vector getMajorDirection(){
		if(this.majorDir == null){
			analyze();
		}
		
		return this.majorDir;
	}
	
	private void analyze(){
		Vector sumDir = getSumDirection(this.dirFeeds);
		VectorComparator comparator = new VectorComparator(sumDir);
		Collections.sort(this.dirFeeds,comparator);
		ArrayList<Vector> significantVectors = filterDirections();
		this.majorDir = getSumDirection(significantVectors);
	}
	
	private ArrayList<Vector> filterDirections(){
		int length = (int) (this.dirFeeds.size()*THRESHOLD);
		return new ArrayList<Vector>(this.dirFeeds.subList(0, length));
	}
	
	private Vector getSumDirection(List<Vector> directions){
		Vector sum = new Vector(0,0);
		for(Vector v : directions){
			Vector normedV = v.getNormedVector();
			sum = sum.add(normedV);
		}
		return sum.getNormedVector();
	}
}
