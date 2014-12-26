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
import DataStructure.BufferQueue;
import MotionDetectionUtility.VideoDisplayer;
import MotionDetectionUtility.ImageSequenceLoader;
import MotionDetectionUtility.MeanVarianceAccumulator;
import MotionDetectionUtility.Utility;
import MotionDetectionUtility.Vector;
import MotionDetectionUtility.VideoLoader;

public class VideoProcessorSting {
	private final int BUFFER_SIZE = 2;
	private final int PROCESSOR_FREQUENCY = 50;
	private final int NUM_OF_FRAMES_INIT = 30;
	
	private int globalFrameNumberCounter;
	
	private BufferQueue<Mat> buffer;
	private Mat prevMat,currMat;
	private MeanVarianceAccumulator[][] mvAccs;
	private VideoDisplayer window;
	private Timer timer;
	
	private FrameFactory factory;
	private BipartiteMatchForSigGrids bipartiteMatch;
	
	private ArrayList<Grid> currSignificantGridArray;
	private ArrayList<Grid> prevSignificantGridArray;
	
	public VideoProcessorSting(){
		globalFrameNumberCounter = 0;
		buffer = new BufferQueue<Mat>(BUFFER_SIZE);
		window = new VideoDisplayer();
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
			System.out.println("Buffer Size: "+buffer.size());
			if(buffer.size() > 0){
				nextFrame();
				if(currMat != null){
					accumulateMvAccs(currMat);
					
					BufferedImage img;
					
					if(globalFrameNumberCounter > NUM_OF_FRAMES_INIT){
						processMat(currMat);
						img = Utility.matToBufferedImage(factory.constructFrame(currMat).getFrameAsMat());
					}else{
						img = Utility.matToBufferedImage(currMat);
					}
					
					window.setSize(img.getWidth(), img.getHeight());
					window.showFrame(img);
					
					globalFrameNumberCounter ++;
				}
			}
		}
		
		private void nextFrame(){
			prevMat = currMat;
			currMat = buffer.getLatest();
			buffer.removeOldest();
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
			if(factory == null){
				factory = new FrameFactory(matToProcess,mvAccs);
			}else{
				factory.resetFrameFactory(matToProcess,mvAccs);
			}
			
			Frame currFrame = factory.constructFrame(currMat);
			Frame prevFrame = factory.constructFrame(prevMat);
			
			initSignificantGridArrays(currFrame, prevFrame);
			Map<Grid,Grid> minCostMatch = bipartiteMatch();
			updateMovingDirections(minCostMatch);
			
			currFrame.updateGridMovingPosition();
			currFrame.updateAverageMovingDirection();
//			clusterUsingSting(currFrame);
		}
		
		private void initSignificantGridArrays(Frame currFrame, Frame prevFrame){
			prepareMat(currFrame);
			prepareMat(prevFrame);
			currSignificantGridArray = currFrame.getSignificantGridArray();
			prevSignificantGridArray = prevFrame.getSignificantGridArray();
		}
		
		private Map<Grid,Grid> bipartiteMatch(){
			bipartiteMatch = new BipartiteMatchForSigGrids(currSignificantGridArray, prevSignificantGridArray);
			return bipartiteMatch.getMinCostMatch();
		}
		
		private void prepareMat(Frame frame){
			if(frame.getFrameAsMat()!=null){
				if(isFrameInColor(frame)){
					frame = convertFrameToGray(frame);
				}
				dividedFrameIntoGrids(frame);
			}
		}
		
		private boolean isFrameInColor(Frame frame){
			return frame.getFrameAsMat().channels() > 1;
		}
		
		private Frame convertFrameToGray(Frame frame){
			Imgproc.cvtColor(frame.getFrameAsMat(), frame.getFrameAsMat(),Imgproc.COLOR_BGR2GRAY);
			return frame;
		}
		
		private void dividedFrameIntoGrids(Frame frame){
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
	}
	
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String basename = "/Users/theflyingwolves/Downloads/Crowd_PETS09/S2/L3/Time_14-41/View_001/frame_";
		String typeString = ".jpg";
		
		int count = 240;
		
	    ImageSequenceLoader loader = new ImageSequenceLoader(basename,typeString,count);
	    VideoProcessorSting sProcessor = new VideoProcessorSting();
	    while(!loader.isEndOfSequence()){
	    	Mat mat = loader.getNextFrameAsMat();			
	    	sProcessor.processFrame(mat);
	    }

//	    String fileName = "/Users/theflyingwolves/Desktop/raiseHand.mp4";
//	    VideoLoader loader = new VideoLoader(fileName);
//	    VideoProcessorSting processor = new VideoProcessorSting();
//	    int counter = 0;
//	    while(!loader.isEndOfVideo()){
//	    	counter ++;
//	    	System.out.println("Frame "+counter);
//	    	Mat frame = loader.getFrameAsMat();
//	    	processor.processFrame(frame);
//	    }
	}
}