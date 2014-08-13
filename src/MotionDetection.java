import org.opencv.core.Core;
import org.opencv.core.Mat;

import videoProcessor.Frame;
import videoProcessor.VideoProcessor;
import MotionDetectionUtility.ImageSequenceLoader;
import MotionDetectionUtility.ImageSequenceLoaderJavaBased;
import MotionDetectionUtility.VideoDisplayer;
import MotionDetectionUtility.VideoLoader;

 public class MotionDetection{
   public static void main(String arg[]){  
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//	    String fileName = "/Users/theflyingwolves/Documents/Android/OpenCV/MotionDetection/video/movingBall.mp4";
//	    String fileName = "/Users/theflyingwolves/Desktop/raiseHand.mp4";
//	    VideoDisplayer player= new VideoDisplayer(fileName);
//	    player.play();
	    
//	    String fileName = "/Users/theflyingwolves/Documents/Android/OpenCV/MotionDetection/video/movingBall.mp4";
//	    String fileName = "/Users/theflyingwolves/Desktop/raiseHand.mp4";
//	    VideoLoader loader = new VideoLoader(fileName);
//	    VideoProcessor processor = new VideoProcessor();
//	    while(!loader.isEndOfVideo()){
//	    	Mat frame = loader.getFrameAsMat();
//	    	processor.processFrame(frame);
//	    }

	    String basename = "/Users/theflyingwolves/Downloads/Crowd_PETS09/S2/L3/Time_14-41/View_001/frame_";
	    String typeString = ".jpg";
	    int count = 240;
	    ImageSequenceLoader loader = new ImageSequenceLoader(basename,typeString,count);
	    VideoProcessor sProcessor = new VideoProcessor();
	    while(!loader.isEndOfSequence()){
	    	Mat sFrame = loader.getFrameAsMat();
	    	sProcessor.processFrame(sFrame);
	    }
	    
	    return;
   }
 }