package videoProcessorSting;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import sting.Cell;
import sting.Combinable;
import sting.Sting;
import videoProcessor.Frame;
import MotionDetectionUtility.MeanVarianceAccumulator;

public class FrameFactory {
	
	private Mat mat;
	private int numOfRows, numOfCols;
	private Sting sting;
	private Frame frame;
	private ArrayList<Point> ctrOfSigCells;
	
	public FrameFactory(Mat mat, MeanVarianceAccumulator[][] mvAccs){
		if(mat.rows() == mvAccs.length && mat.cols()==mvAccs[0].length){
			this.mat = mat;
			this.ctrOfSigCells = new ArrayList<Point>();
			initSizeConfig(mvAccs);
			constructFrame();
		}else{
			System.out.println("FrameFactory: mvAccs is not of the appropriate size");
		}
	}
	
	public Frame getFrame(){
		return frame;
	}
	
	private void initSizeConfig(MeanVarianceAccumulator[][] mvAccs){
		Cell[][] cells = initCellArray(mvAccs);
		sting = constructSting(cells);
		executeSting();
		initSizeInfo();
	}
	
	public Frame constructPrevFrame(Mat prevMat){
		return new Frame(prevMat,numOfRows,numOfCols,this.ctrOfSigCells);
	}
	
	private void constructFrame(){
		frame = new Frame(mat,numOfRows,numOfCols,this.ctrOfSigCells);
	}
	
	private Cell[][] initCellArray(MeanVarianceAccumulator[][] mvAccs){
		Cell[][] cells = new Cell[mvAccs.length][mvAccs[0].length];
		MeanVarianceAccumulator mvAcc;
		Significance sig;
		
		for(int i=0;i<mvAccs.length;i++){
			for(int j=0; j<mvAccs[0].length; j++){
				mvAcc = mvAccs[i][j];
				double value = mat.get(i, j)[0];
				sig = new Significance(!mvAcc.isPointWithinConfidenceInterval(value));
				cells[i][j] = new Cell(sig,i,j);
			}
		}
		
		return cells;
	}
	
	private Sting constructSting(Cell[][] cells){
		Sting sting = new Sting(cells,2);
		return sting;
	}
	
	private void executeSting(){
		Cell[] activeCells = null;
		int exeCount = 0;
		ArrayList<Cell> movingCells = null;
		
		while(movingCells == null || movingCells.size() > 30){
			
			if(movingCells == null){
				movingCells = new ArrayList<Cell>();
			}else{
				movingCells.clear();
			}
					
			sting.execute();

			exeCount++;
			
//			if(exeCount >= 4){
//				sting.printInfo();
//			}
			
			activeCells = sting.getActiveCells();
		
			for(Cell c : activeCells){
				Combinable prop = c.getProperty();
				if(prop instanceof Significance){
					Significance sig = (Significance) prop;
					if(sig.getSignificance()){
						movingCells.add(c);
					}
				}else{
					System.out.println("FrameFactory Line 102: Unexpected type "+prop.getClass());
				}
			}
		}
				
		initCtrOfSigCells(movingCells);
	}
	
	private void initCtrOfSigCells(ArrayList<Cell> sigCells){
		this.ctrOfSigCells.clear();
		for(int i=0; i<sigCells.size(); i++){
			Cell c = sigCells.get(i);
			this.ctrOfSigCells.add(c.getCenter());
		}
	}
	
	private void initSizeInfo(){
		Cell[] activeCells = sting.getActiveCells();
		int level = activeCells[0].getLevel();
		int gridSideLength = (int)Math.pow(2, level);
		numOfRows = mat.cols() / gridSideLength;
		numOfCols = mat.rows() / gridSideLength;
		System.out.println("Num of Rows: "+numOfRows+" Num of Cols: "+numOfCols);
	}
}