package videoProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kMean.KMean;
import kMean.TwoMeanThresholdSelector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import bipartiteMatching.Edge;
import bipartiteMatching.HungarianMatch;
import scatterPlotter.ScatterPlot;
import thresholdAnalyzer.DynamicThresholdSelector;
import DataStructure.BufferQueue;
import MotionDetectionUtility.DisplayWindow;
import MotionDetectionUtility.MeanVarianceAccumulator;
import MotionDetectionUtility.Utility;
import MotionDetectionUtility.Vector;

public class VideoProcessor {
	private final int BUFFER_SIZE = 2;
	private final int PROCESSOR_FREQUENCY = 50;
	private final int NUMBER_OF_GRIDS_PER_ROW = 20;
	private final int NUMBER_OF_GRIDS_PER_COL = 20;
	private final boolean isSumOfAbs = true;
	
	private float[][] dataOfVariance;
	private ScatterPlot variancePlot;
	private int counter = 0;
	
	private float[] standardDeviationArray;
	private float threshold;
	
	private BufferQueue<Frame> buffer;
	private Timer timer;
	private DisplayWindow window;
	
	private MeanVarianceAccumulator globalVarianceMvAcc;
		
	public VideoProcessor(){
		buffer = new BufferQueue<Frame>(this.BUFFER_SIZE);
		timer = new Timer();
		window = new DisplayWindow();
		globalVarianceMvAcc = new MeanVarianceAccumulator();
		threshold = 0;
//		this.dataOfVariance = new float[2][COUNT];
//		this.variancePlot = new ScatterPlot(this.dataOfVariance);
//		this.variancePlot.plot();
		timer.schedule(new Processor(), 0, PROCESSOR_FREQUENCY);
	}
	
	public void processFrame(Mat newFrameAsMat){
		boolean success = buffer.add(new Frame(newFrameAsMat,NUMBER_OF_GRIDS_PER_ROW,NUMBER_OF_GRIDS_PER_COL));
		while(!success){
			success =  buffer.add(new Frame(newFrameAsMat,NUMBER_OF_GRIDS_PER_ROW,NUMBER_OF_GRIDS_PER_COL));
		}
	}
	
	class Processor extends TimerTask {
		public void run() {
			if(buffer.size() > 0){
				Frame frame = buffer.getLatest();
				Frame prevFrame = buffer.getSecondLatest();
				buffer.removeOldest();
				
				if(frame != null){
					prepareFrame(frame);
				}
				
				if(prevFrame == null){
					// If there is no previous frame, then this is the first frame and 
					// hence no processing is required
					return ;
				}
				
				if(frame != null){
					processFrame(frame,prevFrame);
					frame.updateGridMovingPosition();
					frame.updateAverageMovingDirection();
					window.setSize(frame.getFrameAsMat().width(), frame.getFrameAsMat().height());
					window.showFrame(frame.getFrameAsBufferedImage());
				}
			}
		}
		
		private void prepareFrame(Frame frame){
			Mat mat = frame.getFrameAsMat();
			if(mat!=null){
				if(frame.getFrameAsMat().channels() > 1){
					Imgproc.cvtColor(frame.getFrameAsMat(), frame.getFrameAsMat(),Imgproc.COLOR_BGR2GRAY);
				}
				assignPixelsToGrids(frame);
			}
		}
		
		private void processFrame(Frame currFrame,Frame prevFrame){
			ArrayList<Grid> currSignificantGridArray = getSignificantGridArray(currFrame,prevFrame);
			ArrayList<Grid> prevSignificantGridArray = prevFrame.getSignificantGridArray();
			Map<Edge<Grid>,Double> graph = buildGraphUsingSignificantGridArrays(currSignificantGridArray,
																				prevSignificantGridArray);
			HungarianMatch<Grid> hMatch = new HungarianMatch<Grid>(graph);
			Map<Grid,Grid> minCostMatch = hMatch.getMinCostMatch();
			updateMovingDirections(minCostMatch);
		}
		
		private void updateMovingDirections(Map<Grid,Grid> match){
			for(Grid grid : match.keySet()){
				Grid mappedGrid = match.get(grid);
				double dx = grid.getTopLeftCorner().x - mappedGrid.getTopLeftCorner().x;
				double dy = grid.getTopLeftCorner().y - mappedGrid.getTopLeftCorner().y;
				grid.setMovingDirection(new Vector(dx,dy));
			}
		}
		
		private Map<Edge<Grid>,Double> buildGraphUsingSignificantGridArrays(ArrayList<Grid> currSignificantGridArray,
																				ArrayList<Grid> prevSignificantGridArray){
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
		
		private ArrayList<Grid> getSignificantGridArray(Frame frame, Frame prevFrame){
			counter++;

			ArrayList<Grid> significantGrids = filterGrids(frame,prevFrame);
			frame.setSignificantGridArray(significantGrids);
			propagateMVAccsToFrame(frame);
			if(counter < 10){
				initStandardDeviationArray(frame);

				if(standardDeviationArray != null && standardDeviationArray.length > 0){
					TwoMeanThresholdSelector selector = new TwoMeanThresholdSelector(standardDeviationArray);
					threshold = (threshold*(counter-1) + selector.getThreshold())/(float)counter;
					System.out.println("Threshold Becomes "+threshold);
				}else{
					System.out.println("Null Varaince Array");
				}
			}
			if(counter%5 == 0){
//				plotVarianceScatter(frame);
			}
			return significantGrids;
		}
		
		private void initStandardDeviationArray(Frame frame){
			ArrayList<MeanVarianceAccumulator> mvAccs = frame.getMvAccs();
			standardDeviationArray = new float[mvAccs.size()];
			double variance;
			
			for(int i=0; i<mvAccs.size(); i++){
				variance = mvAccs.get(i).getVariance();
				standardDeviationArray[i] = (float) Math.sqrt(variance);
			}
		}
		
		private void propagateMVAccsToFrame(Frame frame){
			for(Grid grid : frame.getGridArray()){
				frame.addMvAccs(grid.getGridMvAccumulators());
			}
		}
		
		private void plotVarianceScatter(Frame frame){
			ArrayList<MeanVarianceAccumulator> mvAccs = frame.getMvAccs();
			
			dataOfVariance = new float[2][mvAccs.size()];
			float[] variance = new float[mvAccs.size()];
			
			for(int i=0;i<mvAccs.size();i++){
				variance[i] = (float) Math.sqrt(mvAccs.get(i).getVariance());
				globalVarianceMvAcc.addCandidate(variance[i]);
				globalVarianceMvAcc.accumulateCandidate();
			}
			
			Arrays.sort(variance);
						
			for(int i = 0; i < mvAccs.size(); i++){
				dataOfVariance[0][i] = i;
				dataOfVariance[1][i] = variance[i];
			}
			
			variancePlot = new ScatterPlot(dataOfVariance);
			variancePlot.plot();
		}
		
		private ArrayList<Grid> filterGrids(Frame currFrame, Frame prevFrame){
			ArrayList<Grid> currGrids = currFrame.getGridArray();
			ArrayList<Grid> prevGrids = prevFrame.getGridArray();
			ArrayList<Grid> significantGridArrayForCurrFrame = new ArrayList<Grid>();

			for(int index = 0; index < currGrids.size(); index++){
				Grid grid1 = currGrids.get(index);
				Grid grid2 = prevGrids.get(index);
				
				grid1.setGridMvAccumulators(grid2.getGridMvAccumulators());
				
				if(grid1.isGridSignificant(threshold)){
					significantGridArrayForCurrFrame.add(grid1);
				}
				
//				MeanVarianceAccumulator mvAcc = grid2.getMVAccumulator();
//				grid1.setMVAccumulator(grid2.getMVAccumulator());
				
//				int w = getEdgeWeightBetweenGrids(grid1, grid2);
				
//				if(w > 0){
//					thresholdSelector.feed(w);
//					
//					System.out.println("Threshold: "+thresholdSelector.getThreshold());
//					if(w >= thresholdSelector.getThreshold()){
//						significantGridArrayForCurrFrame.add(grid1);
//					}
//				}
				
//				if(w >= GRID_FILTER_THRESHOLD){
//					significantGridArrayForCurrFrame.add(grid1);
//				}
				
//				if(mvAcc.getCount() < 10){
//					grid1.accumulate(w);
//				}else{
//					System.out.println("Mean: "+mvAcc.getMean()+" Variance: "+mvAcc.getVariance());
//					boolean isGridSignificant = grid1.isWeightSignificant(w);
//					
//					if(isGridSignificant){
//						significantGridArrayForCurrFrame.add(grid1);
//					}else{
//						grid1.accumulate(w);
//					}
//				}
			}
			
			System.out.println("Number of significant grids are "+significantGridArrayForCurrFrame.size());
			return significantGridArrayForCurrFrame;
		}
				
		private void assignPixelsToGrids(Frame frame){
			ArrayList<Grid> gridArray = frame.getGridArray();
			BufferedImage frameAsImage = frame.getFrameAsBufferedImage();
			int[][] frameAsIntArray = Utility.bufferedImageTo2DArray(frameAsImage);
			for(Grid grid : gridArray){
				grid.assignPixelsInFrameArray(frameAsIntArray);
			}
		}

		/**
		 * 
		 * @param grid1
		 * @param grid2
		 * @return The edge between the two grids determined by max(minMatch Weights), where minMatch Weight
		 * 			represents the individual edge weight in a minimum match
		 */
		private int getEdgeWeightBetweenGrids(Grid grid1, Grid grid2){
			if(isSumOfAbs){
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
			}else{
				return Math.abs(grid1.getAverageGrayScaleValue() - grid2.getAverageGrayScaleValue());
			}
		}
	}
}