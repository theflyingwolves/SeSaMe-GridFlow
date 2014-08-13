package MotionDetectionUtility;

import java.awt.image.BufferedImage;

import org.opencv.highgui.VideoCapture;

public class VideoDisplayer {
	private VideoCapture capture;
	private VideoLoader loader;
	private DisplayWindow window;
	
	public VideoDisplayer(String videoName){
		capture = new VideoCapture(videoName);
		loader = new VideoLoader(capture);
		window = new DisplayWindow();
	}
	
	public VideoDisplayer(VideoCapture capt){
		capture = capt;
		loader = new VideoLoader(capture);
		window = new DisplayWindow();
	}
	
	public void play(){
		BufferedImage frame = Utility.matToBufferedImage(loader.getFrameAsMat());
		do{
			window.setSize(frame.getWidth(), frame.getHeight());
			window.showFrame(frame);
			frame = Utility.matToBufferedImage(loader.getFrameAsMat());
		}while(frame != null);
	}
}
