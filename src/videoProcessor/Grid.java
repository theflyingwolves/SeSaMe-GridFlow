package videoProcessor;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import MotionDetectionUtility.MeanVarianceAccumulator;
import MotionDetectionUtility.Vector;

public class Grid {
	private Point topLeftCorner;
	private Point lowerRightCorner;
	private Vector movingDirection;
	private int row,col;
	private int sum;
	private int pixelCount;
	private ArrayList<MeanVarianceAccumulator> gridMvAccs;
	private ArrayList<Integer> gridPixels;
	
	public Grid(Point topLeftCorner, Point lowerRightCorner){
		this.movingDirection = new Vector(0,0);
		this.topLeftCorner = topLeftCorner;
		this.lowerRightCorner = lowerRightCorner;
		this.sum = 0;
		this.pixelCount = 0;
		this.gridPixels = new ArrayList<Integer>();
		this.gridMvAccs = new ArrayList<MeanVarianceAccumulator>();
	}
	
	public void assignPixelsInFrameArray(int[][] frameAsArr){
		int sum = 0;
		int pixel;
		this.pixelCount = 0;
		this.gridPixels = new ArrayList<Integer>();
		
		for(int row = (int)topLeftCorner.y; row<lowerRightCorner.y; row++){
			for(int col = (int)topLeftCorner.x; col<lowerRightCorner.x;col++){
				pixel = frameAsArr[row][col];
				this.gridPixels.add(pixel);
				this.pixelCount++;
				sum += pixel;
			}
		}
		
		this.sum = sum;
	}
	
	public boolean isGridSignificant(float threshold){
		int count = 0;
		if(this.gridMvAccs.size() == 0){
			for(int index=0; index<this.gridPixels.size();index++){
				MeanVarianceAccumulator mvAcc = new MeanVarianceAccumulator();
				this.gridMvAccs.add(mvAcc);
			}
		}
		
		for(int pIndex = 0; pIndex < this.gridPixels.size(); pIndex++){
			int pixel = this.gridPixels.get(pIndex);
			MeanVarianceAccumulator mvAcc = this.gridMvAccs.get(pIndex);
//			mvAcc.addCandidate(pixel);
//			mvAcc.accumulateCandidate();
			mvAcc.accumulate(pixel);
			if(mvAcc.getVariance() > threshold){
				count++;
			}
		}
		
		if(count > 0.5*this.gridMvAccs.size()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isGridSignificant(){
		int count = 0;
		
		if(this.gridMvAccs.size() == 0){
			for(int index=0; index<this.gridPixels.size();index++){
				MeanVarianceAccumulator mvAcc = new MeanVarianceAccumulator();
				this.gridMvAccs.add(mvAcc);
			}
		}
		
		for(int pIndex = 0; pIndex < this.gridPixels.size(); pIndex++){
			int pixel = this.gridPixels.get(pIndex);
			MeanVarianceAccumulator mvAcc = this.gridMvAccs.get(pIndex);
//			mvAcc.addCandidate(pixel);
//			
//			if(!mvAcc.isCandidateWithinConfidenceInterval()){
//				count++;
//			}else{
//				mvAcc.accumulateCandidate();
//			}
			
			if(!mvAcc.isPointWithinConfidenceInterval(pixel)){
				count++;
			}else{
				mvAcc.accumulate(pixel);
			}
			
//			if(pIndex == 200 && row == 10 && col == 10){
//				System.out.println("Candidate: "+pixel);
//				System.out.println("Mean: "+mvAcc.getMean()+" Variance: "+mvAcc.getVariance());
//				if(!mvAcc.isCandidateWithinConfidenceInterval()){
//					System.out.println("Not Within Confidence Interval");
//				}
//			}
		}
				
		if(count > 0.5*this.gridMvAccs.size()){
			return true;
		}else{
			return false;
		}
	}
	
	public ArrayList<MeanVarianceAccumulator> getGridMvAccumulators(){
		return this.gridMvAccs;
	}
	
	public void setGridMvAccumulators(ArrayList<MeanVarianceAccumulator> mvAccs){
		this.gridMvAccs = mvAccs;
	}
	
	public int getAverageGrayScaleValue(){
		if(this.pixelCount == 0){
			return 0;
		}
		return this.sum / this.pixelCount;
	}
	
	public void setMovingDirection(Vector dir){
		this.movingDirection = dir;
	}
	
	public Vector getMovingDirection(){
		return this.movingDirection;
	}
	
	public Point getCenter(){
		double midX = (this.topLeftCorner.x + this.lowerRightCorner.x ) / 2;
		double midY = (this.topLeftCorner.y + this.lowerRightCorner.y ) / 2;
		return new Point(midX, midY);
	}
	
	public Point getTopLeftCorner(){
		return this.topLeftCorner;
	}
	
	public Point getLowerRightCorner(){
		return this.lowerRightCorner;
	}
	
	public boolean isPointWithinGrid(Point pt){
		if(pt.x >= this.topLeftCorner.x && pt.x <= this.lowerRightCorner.x){
			if(pt.y >= this.topLeftCorner.y && pt.y <= this.lowerRightCorner.y){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<MeanVarianceAccumulator> getMVAccumulators(){
		return this.gridMvAccs;
	}
	
	public ArrayList<Integer> getGridPixels(){
		return this.gridPixels;
	}
	
	public void setRowColIndices(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	public int getRowIndex(){
		return this.row;
	}
	
	public int getColIndex(){
		return this.col;
	}
	
	public String toString(){
		return "(("+topLeftCorner.x + ", "+topLeftCorner.y+"), ("+lowerRightCorner.x+", "+lowerRightCorner.y+"))";
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Grid)){
			return false;
		}
		
		if(obj == this){
			return true;
		}
		
		Grid objGrid = (Grid)obj;
		return objGrid.toString().equalsIgnoreCase(this.toString());
	}
}