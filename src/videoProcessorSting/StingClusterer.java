package videoProcessorSting;

import java.util.ArrayList;

import MotionDetectionUtility.Vector;
import videoProcessor.Frame;
import videoProcessor.Grid;

public class StingClusterer {
	
	public StingClusterer(){
		
	}
	
	public Frame clusterFrame(Frame frame){
		ArrayList<Grid> gridArray = frame.getGridArray();
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		
		for(Grid grid : gridArray){
			Vector movingDir = grid.getMovingDirection();
			Direction dir = new Direction(movingDir);
			dirs.add(dir);
		}
		
		//TODO To Be Continued
		return null;
	}
}
