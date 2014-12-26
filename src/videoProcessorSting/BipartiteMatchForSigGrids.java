package videoProcessorSting;

import hungarianMatch.Edge;
import hungarianMatch.HungarianMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import videoProcessor.Frame;
import videoProcessor.Grid;

public class BipartiteMatchForSigGrids {
	private ArrayList<Grid> currSignificantGridArray;
	private ArrayList<Grid> prevSignificantGridArray;
	
	public BipartiteMatchForSigGrids(ArrayList<Grid> currSignificantGridArray, 
										ArrayList<Grid> prevSignificantGridArray){
		this.currSignificantGridArray = currSignificantGridArray;
		this.prevSignificantGridArray = prevSignificantGridArray;
	}
	
	public Map<Grid,Grid> getMinCostMatch(){
		return BipartiteMatch();
	}
	
	private Map<Grid,Grid> BipartiteMatch(){
		Map<Edge<Grid>,Double> graph = buildBipartiteGraph();
		HungarianMatch<Grid> hMatch = new HungarianMatch<Grid>(graph);
		Map<Grid,Grid> minCostMatch = hMatch.getMinCostMatch();
		return minCostMatch;
//		updateMovingDirections(minCostMatch);
	}
	
	private Map<Edge<Grid>,Double> buildBipartiteGraph(){
		Map<Edge<Grid>,Double> graph = new HashMap<Edge<Grid>,Double>();
		
		if(currSignificantGridArray != null && currSignificantGridArray.size() > 0){
			if(prevSignificantGridArray != null && prevSignificantGridArray.size() > 0){
				for(Grid currGrid : currSignificantGridArray){
					for(Grid prevGrid : prevSignificantGridArray){
						if(Frame.areGridsNear(currGrid, prevGrid)){
							graph.put(new Edge<Grid>(currGrid,prevGrid), (double)getEdgeWeightBetweenGrids(currGrid,prevGrid));
						}else{
							graph.put(new Edge<Grid>(currGrid,prevGrid), (double)255);
						}
					}
				}
			}
		}
		
		return graph;
	}
	
	private int getEdgeWeightBetweenGrids(Grid grid1, Grid grid2){
			ArrayList<Integer> grid1Pixels = grid1.getGridPixels();
			ArrayList<Integer> grid2Pixels = grid2.getGridPixels();
			int sum = 0;
			
			if(grid1Pixels.size() != grid2Pixels.size()){
				System.out.println("Negative Weight");
				return -1;
			}
			
			for(int i=0;i<grid1Pixels.size();i++){
				int pixel1 = grid1Pixels.get(i);
				int pixel2 = grid2Pixels.get(i);
				sum += (Math.abs(pixel1-pixel2));
			}
			return sum/grid1Pixels.size();
	}
}
