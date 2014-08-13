package MotionDetectionUtility;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class ImageSequenceLoader {
	private final int BUFFER_SIZE = 30;
	private int count;
	private String baseName;
	private String fileTypeString;
	private int currImageCount;
	private ArrayList<Mat> buffer;
	private boolean isSequenceFinished;
	
	/**
	 * 
	 * @param baseName baseName is the common part in the path of the sequence of images
	 * @param typeString file type of the image files, such as ".jpg", ".png", etc.
	 * @param count Total number of image files in the sequence. Must be less than 10000
	 */
	public ImageSequenceLoader(String baseName,String typeString, int count){
		if(count >= 10000){
			System.out.println("ImageSequenceLoader Construction Failed: Too many images in the sequence");
		}else{
			this.baseName = baseName;
			this.fileTypeString = typeString;
			this.count = count;
			this.currImageCount = 0;
			this.isSequenceFinished = false;
			this.buffer = new ArrayList<Mat>();
			preload();
		}
	}
	
	public Mat getFrameAsMat(){
		if(this.buffer.size() == 0){
			if(this.isSequenceFinished){
				return null;
			}
		}
		
		System.out.println("Getting Image Indexed by: "+this.currImageCount);
		Mat mat = this.buffer.get(0);
		this.buffer.remove(0);
		loadImageToBuffer();
		return mat;
	}
	
	public boolean isEndOfSequence(){
		return this.isSequenceFinished;
	}
	
	private void preload(){
		for(int i=0;i<this.BUFFER_SIZE;i++){
			loadImageToBuffer();
		}
	}
	
	private void loadImageToBuffer(){
		if(this.buffer.size() < this.BUFFER_SIZE){
			if(this.currImageCount < this.count){
				String path = constructPathForImageIndexed(this.currImageCount);
				Mat mat = loadImageAsMat(path);
				this.buffer.add(mat);
				this.currImageCount++;
			}else{
				this.isSequenceFinished = true;
			}
		}
	}
	
	private String constructPathForImageIndexed(int count){
		String fourDigitCount = convertToFourDigitString(count);
		String path = this.baseName+fourDigitCount+this.fileTypeString;
		return path;
	}
	
	private Mat loadImageAsMat(String path){
		Mat mat = Highgui.imread(path);
		if(mat.empty()){
			System.out.println("Image Not Found");
		}
		return mat;
	}
	
	private String convertToFourDigitString(int n){
		String auxString = "";
		if(n>=0 && n<10){
			auxString += "000";
		}else if(n >=10 && n < 100){
			auxString += "00";
		}else if(n >= 100 && n < 1000){
			auxString += "0";
		}
		
		return auxString+n;
	}
}