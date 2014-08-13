package DataStructure;

import java.util.ArrayList;

import videoProcessor.Grid;

public class StingLayers {
	private ArrayList<Grid> sting;
	private final int dim = 2;
	private StingState state;
	
	private enum StingState {
		PROPAGATE,
		ACCUMULATE
	};
	
	public StingLayers(){
		this.sting = new ArrayList<Grid>();
	}
	
	public void propagateUp(){
		switch(state){
		case PROPAGATE:
			break;
		case ACCUMULATE:
			break;
		default:
			break;
		}
	}
	
	
}
