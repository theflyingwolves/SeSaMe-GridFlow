package videoProcessorSting;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import bipartiteMatching.Edge;
import bipartiteMatching.HungarianMatch;
import videoProcessor.Frame;
import videoProcessor.Grid;
import videoProcessor.VideoProcessor;
import DataStructure.BufferQueue;
import MotionDetectionUtility.DisplayWindow;
import MotionDetectionUtility.ImageSequenceLoader;
import MotionDetectionUtility.MeanVarianceAccumulator;
import MotionDetectionUtility.Utility;
import MotionDetectionUtility.Vector;

public class VideoProcessorSting {
	private final int BUFFER_SIZE = 2;
	private final int PROCESSOR_FREQUENCY = 50;
	private final int NUM_OF_FRAMES_INIT = 50;
	
	private int globalFrameNumberCounter;
	
	private BufferQueue<Mat> buffer;
	private Mat prevMat,currMat;
	private MeanVarianceAccumulator[][] mvAccs;
	private DisplayWindow window;
	private Timer timer;
	private FrameFactory factory;
	
	public VideoProcessorSting(){
		globalFrameNumberCounter = 0;
		buffer = new BufferQueue<Mat>(BUFFER_SIZE);
		window = new DisplayWindow();
		timer = new Timer();
		timer.schedule(new Processor(),0,PROCESSOR_FREQUENCY);
	}
	
	public void processFrame(Mat newFrameAsMat){
		boolean success = buffer.add(newFrameAsMat);
		while(!success){
			success =  buffer.add(newFrameAsMat);
		}
	}
	
	class Processor extends TimerTask {
		
		@Override
		public void run() {
			if(buffer.size() > 0){
				Mat matToProcess = buffer.getLatest();
				prevMat = currMat;
				currMat = matToProcess;
				
				buffer.removeOldest();
				
//				if(globalFrameNumberCounter < NUM_OF_FRAMES_INIT){
//					accumulateMvAccs(matToProcess);
//				}else{
//					processMat(matToProcess);
//				}
				
				if(matToProcess != null){
					accumulateMvAccs(matToProcess);
					
					BufferedImage img;
					
					if(globalFrameNumberCounter > NUM_OF_FRAMES_INIT){
						processMat(matToProcess);
						img = Utility.matToBufferedImage(factory.getFrame().getFrameAsMat());
					}else{
						img = Utility.matToBufferedImage(currMat);
					}
					
					window.setSize(img.getWidth(), img.getHeight());
					window.showFrame(img);
					
					globalFrameNumberCounter ++;
				
				}

			}
		}
		
		private void accumulateMvAccs(Mat mat){
			if(globalFrameNumberCounter == 0){
				initMvAccs(mat.rows(),mat.cols());
			}
			
			feedMatToMvAccs(mat);
		}
		
		private void initMvAccs(int numOfRows, int numOfCols){
			mvAccs = new MeanVarianceAccumulator[numOfRows][numOfCols];
			for(int i=0; i<numOfRows; i++){
				for(int j=0; j<numOfCols; j++){
					mvAccs[i][j] = new MeanVarianceAccumulator();
				}
			}
		}
		
		private void feedMatToMvAccs(Mat mat){
			for(int i=0;i<mat.rows();i++){
				for(int j=0;j<mat.cols();j++){
					mvAccs[i][j].accumulate(mat.get(i, j)[0]);
				}
			}
		}
		
		private void processMat(Mat matToProcess){
//			if(globalFrameNumberCounter == (NUM_OF_FRAMES_INIT+1)){
//				printMvAccInfo();
//			}
			
			System.out.println("Start constructing factory");
			factory = new FrameFactory(matToProcess,mvAccs);
			System.out.println("Factory constructed");
			Frame currFrame = factory.getFrame();
			Frame prevFrame = factory.constructPrevFrame(prevMat);
			System.out.println("prevFrame and currFrame constructed");
			prepareFrame(currFrame); //Convert frame to b/w frame and assign pixels to each grid
			prepareFrame(prevFrame);
			System.out.println("prevFrame and currFrame prepared");
			Map<Grid,Grid> minCostMatch = BipartiteMatch(currFrame,prevFrame);
			System.out.println("minCostMatch Obtained");
//			updateMovingDirections(minCostMatch);
//			clusterUsingSting(currFrame);
		}
		
		private Map<Grid,Grid> BipartiteMatch(Frame currFrame,Frame prevFrame){
			ArrayList<Grid> currSignificantGridArray = currFrame.getSignificantGridArray();
			ArrayList<Grid> prevSignificantGridArray = prevFrame.getSignificantGridArray();
			System.out.println("SigArray: "+currSignificantGridArray.size());
			Map<Edge<Grid>,Double> graph = buildGraphUsingSignificantGridArrays(currSignificantGridArray,
					prevSignificantGridArray);
			System.out.println("Graph Successfully Built");
			HungarianMatch<Grid> hMatch = new HungarianMatch<Grid>(graph);
			System.out.println("Min Cost Match Obtained");
			Map<Grid,Grid> minCostMatch = hMatch.getMinCostMatch();
			return minCostMatch;
//			updateMovingDirections(minCostMatch);
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
		
		private void assignPixelsToGrids(Frame frame){
			ArrayList<Grid> gridArray = frame.getGridArray();
			BufferedImage frameAsImage = frame.getFrameAsBufferedImage();
			int[][] frameAsIntArray = Utility.bufferedImageTo2DArray(frameAsImage);
			for(Grid grid : gridArray){
				grid.assignPixelsInFrameArray(frameAsIntArray);
			}
		}
		
		private void clusterUsingSting(Frame frame){
			StingClusterer c = new StingClusterer();
			Frame clusteredframe = c.clusterFrame(frame);
			//TODO Need to add position information to frame so that it is easier to draw results
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
		
		private void printMvAccInfo(){
//			System.out.println("Printing MvAcc Info");
			for(int i=0;i<mvAccs.length;i++){
				for(int j=0;j<mvAccs[0].length;j++){
					System.out.println("Variance: "+mvAccs[i][j].getVariance());
				}
			}
//			System.out.println("Mean: "+mvAccs[100][100].getMean());
		}
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
	
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String basename = "/Users/theflyingwolves/Downloads/Crowd_PETS09/S2/L3/Time_14-41/View_001/frame_";
		String typeString = ".jpg";
		
		int count = 240;
		
	    ImageSequenceLoader loader = new ImageSequenceLoader(basename,typeString,count);
	    VideoProcessorSting sProcessor = new VideoProcessorSting();
	    while(!loader.isEndOfSequence()){
	    	Mat mat = loader.getFrameAsMat();			
	    	sProcessor.processFrame(mat);
	    }
	}
}