package videoProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import majorDirectionAnalyzer.MajorDirectionAnalyzer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import MotionDetectionUtility.MeanVarianceAccumulator;
import MotionDetectionUtility.Utility;
import MotionDetectionUtility.Vector;

public class Frame {
	private final static double NEIGHBOUR_AREA_RADIUS = 3;
	private final static double INDICATOR_LINE_LENGTH = 30;
	private ArrayList<Grid> gridArray;
	private ArrayList<Grid> significantGridArray;
	private Mat frameAsMat;
	private BufferedImage frameAsBufferedImage;
	private int numOfRows;
	private int numOfCols;
	private ArrayList<MeanVarianceAccumulator> mvAccs;
	
	public Frame(Mat mat,int numOfRows,int numOfCols){
		this.frameAsMat = mat;
		this.numOfRows = numOfRows;
		this.numOfCols = numOfCols;
		this.significantGridArray = new ArrayList<Grid>();
		initGridArray();
//		initFrameGridBorder();
	}
	
	public Frame(Mat mat, int numOfRows, int numOfCols, ArrayList<Point> sigCenters){
		this(mat,numOfRows, numOfCols);
		initSigGrids(sigCenters);
	}
	
	private void initSigGrids(ArrayList<Point> centers){
		
		// Can be optimized by sorting
		for(int i=0; i<centers.size(); i++){
			Point center = new Point(centers.get(i).y, centers.get(i).x);
			for(int j=0; j<gridArray.size(); j++){
				Grid grid = gridArray.get(j);
				if(grid.isPointWithinGrid(center)){
					Core.circle(frameAsMat, grid.getCenter(), 5, new Scalar(0,0,100));
					significantGridArray.add(grid);
				}
			}
		}
	}
	
	private void initGridArray(){
		if(gridArray == null){
			gridArray = new ArrayList<Grid>();
		}
		if(frameAsMat != null){
			Grid grid;
			int gridHeight = frameAsMat.rows() / this.numOfRows;
			int gridWidth = frameAsMat.cols() / this.numOfCols;
			for(int y= 0; y < this.numOfRows;y++){
				for(int x = 0; x < this.numOfCols; x++){
					int topX = x * gridWidth;
					int topY = y * gridHeight;
					int lowerX = (x+1) * gridWidth;
					int lowerY = (y+1) * gridHeight;
					grid = new Grid(new Point(topX,topY), new Point(lowerX, lowerY));
					grid.setRowColIndices(y, x);
					this.gridArray.add(grid);
				}
			}
		}
	}
	
	public Grid getGridIndexedBy(int index){
		return gridArray.get(index);
	}
	
	public int getIndexForGrid(Grid grid){
		int rowIndex = grid.getRowIndex();
		int colIndex = grid.getColIndex();
		return rowIndex*this.numOfCols+colIndex;
	}
	
	private void initFrameGridBorder(){
		if(this.gridArray.size() > 0){
			Scalar borderColor = new Scalar(255,255,255);
			for(Grid grid : this.gridArray){
				Core.rectangle(frameAsMat, grid.getTopLeftCorner(), grid.getLowerRightCorner(), borderColor);
			}
		}
	}
	
	public void addMvAccs(ArrayList<MeanVarianceAccumulator> newMvAccs){
		if(this.mvAccs == null){
			this.mvAccs = new ArrayList<MeanVarianceAccumulator>();
		}
		int oldSize = this.mvAccs.size();
		this.mvAccs.addAll(newMvAccs);
	}
	
	public ArrayList<MeanVarianceAccumulator> getMvAccs(){
		return this.mvAccs;
	}
	
	public void updateGridMovingPosition(){
		for(Grid grid : this.gridArray){
			Vector dir = grid.getMovingDirection().getNormedVector().scale(INDICATOR_LINE_LENGTH);
			Point center = grid.getCenter();
			Point endPoint = new Point(center.x+dir.dx(), center.y+dir.dy());
			drawArrow(frameAsMat,center,endPoint,new Scalar(0,0,255));
		}
	}
	
	private void drawArrow(Mat mat, Point start, Point end, Scalar color){
		Core.line(mat,start,end,color);
		if(start.x == end.x && start.y == end.y){
			return ;
		}
		double dx = start.x < end.x? -5 : 5;
		double dy = start.y < end.y? -5 : 5;
		Core.line(mat, end, new Point(end.x,end.y+dy), color);
		Core.line(mat, end, new Point(end.x+dx,end.y), color);
	}
	
	public void updateAverageMovingDirection(){
		ArrayList<Vector> movingDirections = getListOfMovingDirections();
		MajorDirectionAnalyzer analyzer = new MajorDirectionAnalyzer(movingDirections);
		Vector movDir = analyzer.getMajorDirection().scale(100);
		Point center = new Point(this.frameAsMat.cols()/2,this.frameAsMat.rows()/2);
		Point end = new Point(center.x+movDir.dx(), center.y+movDir.dy());
		Core.line(frameAsMat, center, end, new Scalar(0,0,255));
	}
	
	private ArrayList<Vector> getListOfMovingDirections(){
		ArrayList<Vector> movDirs = new ArrayList<Vector>();
		for(Grid grid : this.gridArray){
			Vector dir = grid.getMovingDirection();
			movDirs.add(dir);
		}
		return movDirs;
	}
	
	public Mat getFrameAsMat(){
		return this.frameAsMat;
	}
	
	public BufferedImage getFrameAsBufferedImage(){
		this.frameAsBufferedImage = Utility.matToBufferedImage(getFrameAsMat());		
		return this.frameAsBufferedImage;
	}
	
	public ArrayList<Grid> getGridArray(){
		return this.gridArray;
	}
	
	public void setSignificantGridArray(ArrayList<Grid> sigGridArray){
		this.significantGridArray = sigGridArray;
	}
	
	public ArrayList<Grid> getSignificantGridArray(){
		return this.significantGridArray;
	}
	
	/**
	 * 
	 * @param grid1
	 * @param grid2
	 * @return Defines what it means by being 
	 */
	public static boolean areGridsNear(Grid grid1, Grid grid2){
		int row1 = grid1.getRowIndex();
		int row2 = grid2.getRowIndex();
		int col1 = grid1.getColIndex();
		int col2 = grid2.getColIndex();
		
		if(Math.abs(row1-row2)<=NEIGHBOUR_AREA_RADIUS && Math.abs(col1-col2)<=NEIGHBOUR_AREA_RADIUS){
			return true;
		}else{
			return false;
		}
	}
}