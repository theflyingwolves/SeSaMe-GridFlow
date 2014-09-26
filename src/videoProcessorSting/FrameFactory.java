package videoProcessorSting;

import org.opencv.core.Mat;

import sting.Cell;
import sting.Sting;
import videoProcessor.Frame;
import MotionDetectionUtility.MeanVarianceAccumulator;

public class FrameFactory {
	
	private Mat mat;
	private int numOfRows, numOfCols;
	private Sting sting;
	private Frame frame;
	
	public FrameFactory(Mat mat, MeanVarianceAccumulator[][] mvAccs){
		if(mat.rows() == mvAccs.length && mat.cols()==mvAccs[0].length){
			this.mat = mat;
			initSizeConfig(mvAccs);
			constructFrame();
		}else{
			System.out.println("FrameWithSting: mvAccs is not of the appropriate size");
		}
	}
	
	public Frame getFrame(){
		return frame;
	}
	
	public Frame constructPrevFrame(Mat prevMat){
		return new Frame(prevMat,numOfRows,numOfCols);
	}
	
	private void initSizeConfig(MeanVarianceAccumulator[][] mvAccs){
		Cell[][] cells = initCellArray(mvAccs);
		sting = constructSting(cells);
		executeSting();
		initSizeInfo();
	}
	
	private void constructFrame(){
		frame = new Frame(mat,numOfRows,numOfCols);
	}
	
	private Cell[][] initCellArray(MeanVarianceAccumulator[][] mvAccs){
		Cell[][] cells = new Cell[mvAccs.length][mvAccs[0].length];
		MeanVarianceAccumulator mvAcc;
		Significance sig;
		for(int i=0;i<mvAccs.length;i++){
			for(int j=0; j<mvAccs[0].length; j++){
				mvAcc = mvAccs[i][j];
				double value = mat.get(i, j)[0];
				mvAcc.addCandidate(value);
				sig = new Significance(mvAcc.isCandidateWithinConfidenceInterval());
				cells[i][j] = new Cell(sig);
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
		while(activeCells == null || activeCells.length > 30){
			sting.execute();
			
			if(activeCells == null){
			 	System.out.println("Null Active Cells");
			}else{
				System.out.println("length Exe Count "+exeCount+": "+activeCells.length);	
			}
			
			exeCount++;
			activeCells = sting.getActiveCells();
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