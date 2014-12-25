package MotionDetectionUtility;

import java.awt.image.BufferedImage;

import org.opencv.highgui.VideoCapture;

public class VideoPlayer {
	private VideoCapture capture;
	private VideoLoader loader;
	private VideoDisplayer window;
	
	public VideoPlayer(String videoName){
		capture = new VideoCapture(videoName);
		loader = new VideoLoader(capture);
		window = new VideoDisplayer();
	}
	
	public VideoPlayer(VideoCapture capt){
		capture = capt;
		loader = new VideoLoader(capture);
		window = new VideoDisplayer();
	}
	
	public void play(){
		BufferedImage frame = Utility.matToBufferedImage(loader.getNextFrameAsMat());
		do{
			window.setSize(frame.getWidth(), frame.getHeight());
			window.showFrame(frame);
			frame = Utility.matToBufferedImage(loader.getNextFrameAsMat());
		}while(frame != null);
	}
}
