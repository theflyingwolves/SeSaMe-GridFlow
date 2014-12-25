package MotionDetectionUtility;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class VideoLoader {
	private final int MAX_FRAMES_ALLOWED = 1;
	private VideoCapture capture;
	private ArrayList<Mat> buffer;
	private boolean isVideoFinished;
	
	public VideoLoader(VideoCapture capture){
		if(this.capture == null || !this.capture.isOpened()){
			System.out.println("Fail Loading Video");
		}
		this.capture = capture;
		this.isVideoFinished = false;
		buffer = new ArrayList<Mat>();
	}
	
	public VideoLoader(String videoName){
		capture = new VideoCapture(videoName);
		this.isVideoFinished = false;
		buffer = new ArrayList<Mat>();
	}
	
	public Mat getNextFrameAsMat(){
		if(buffer.size() <= 0){
			if(isVideoFinished){
				return null;
			}else{
				loadFramesToBuffer();
			}
		}
		
		if(buffer.size() > 0){
			Mat imageAsMat = buffer.get(0);
			buffer.remove(0);
			return imageAsMat;
		}else{
			isVideoFinished = true;
			return null;
		}
	}
	
	public boolean isEndOfVideo(){
		return buffer.size() == 0 && isVideoFinished;
	}
	
	private void loadFramesToBuffer(){
		Mat frameAsMat = new Mat();
		while(buffer.size() < MAX_FRAMES_ALLOWED){
			capture.read(frameAsMat);
			if(!frameAsMat.empty()){
				Imgproc.pyrDown(frameAsMat, frameAsMat, new Size(frameAsMat.size().width/2,frameAsMat.size().height/2));
				Imgproc.pyrDown(frameAsMat, frameAsMat, new Size(frameAsMat.size().width/2,frameAsMat.size().height/2));
				buffer.add(frameAsMat);
			}else{
				this.isVideoFinished = true;
				break;
			}
		}
	}
}