package sting;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import sting.StingTestFake.Significance;
import MotionDetectionUtility.DisplayWindow;
import MotionDetectionUtility.ImageSequenceLoader;
import MotionDetectionUtility.Utility;

public class StingTest {
	private static DisplayWindow window;
	
//	public static class Significance extends Combinable {
//		private float significance;
//		
//		public Significance(float sig){
//			this.significance = sig;
//		}
//		
//		public float getSignificance(){
//			return this.significance;
//		}
//		
//		public Combinable combineWith(Combinable obj) {
//			if(obj instanceof Significance){
//				Significance s = (Significance)obj;
//				Significance newS= new Significance(this.significance + s.getSignificance());
//				return newS;
//			}else{
//				return null;
//			}
//		}
//		
//		public String toString(){
//			return significance+"";
//		}
//
//		@Override
//		public boolean shouldPropagate(Combinable[] objs) {
////			boolean flag = true;
////			int sum = 0;
////			Significance sig;
////			
////			for(Combinable c : objs){
////				if(!(c instanceof Significance)){
////					flag = false;
////					break;
////				}else{				
////					sig = (Significance)c;
////					sum += sig.getSignificance();
////				}
////			}
////			
////			if(flag){
////				return sum < 50;
////			}else{
////				return false;
////			}
//			return true;
//		}
//	}
//	
	
	public static class Significance extends Combinable{
		private boolean significance;
		
		public Significance(boolean isSignificant){
			this.significance = isSignificant;
		}
		
		public boolean getSignificance(){
			return this.significance;
		}
		
		@Override
		public Combinable combineWith(Combinable obj) {
			if(obj instanceof Significance){
				Significance s = (Significance)obj;
				Significance newS= new Significance(this.significance && s.getSignificance());
				return newS;
			}else{
				return null;
			}
		}
		
		@Override
		public Combinable combineWith(Combinable[] obj){
			int count = 0;
			for(Combinable c : obj){
				if(c instanceof Significance){
					Significance s = (Significance)c;
					if(s.getSignificance()){
						count++;
					}
				}
			}
			
			if(count >= 0.5*obj.length){
				return new Significance(true);
			}else{
				return new Significance(false);
			}
		}

		@Override
		public boolean shouldPropagate(Combinable[] objs) {
			return true;
		}
		
		public String toString(){
			if(significance){
				return "true";
			}else{
				return "false";
			}
		}
	}
	
	public static void main(String[] args){
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    window = new DisplayWindow();
	    
//		Cell[][] cells = new Cell[8][8];
//		int counter = 0;
//		for(int i=0; i<cells.length;i++){
//			for(int j=0; j<cells[0].length; j++){
//				Significance sig = new Significance(counter);
//				cells[i][j] = new Cell(sig);
//				counter++;
//			}
//		}
//		System.out.println("Counter: "+counter);
//		
//		Sting sting = new Sting(cells,2);
	    long start, end;
	    long totalStart, totalEnd;
	    
	    totalStart = System.nanoTime();
	    start = System.nanoTime();
		Sting sting = constructSting();
		end = System.nanoTime();
		System.out.println("Time for Construction: "+(end-start)+"\n");
		
		printStingInfo(sting);
		
		start = System.nanoTime();
		sting.execute();
		end = System.nanoTime();
		System.out.println("Execution time: "+(end-start));
		
		printStingInfo(sting);
		
		start = System.nanoTime();
		sting.execute();
		end = System.nanoTime();
		System.out.println("Execution time: "+(end-start));
		
		printStingInfo(sting);
		
		start = System.nanoTime();
		sting.execute();
		end = System.nanoTime();
		System.out.println("Execution time: "+(end-start));
		
		printStingInfo(sting);
		
		start = System.nanoTime();
		sting.execute();
		end = System.nanoTime();
		System.out.println("Execution time: "+(end-start));
		printStingInfo(sting);
		
//		start = System.nanoTime();
//		sting.execute();
//		end = System.nanoTime();
//		System.out.println("Execution time: "+(end-start));
//		printStingInfo(sting);
//		
//		start = System.nanoTime();
//		sting.execute();
//		end = System.nanoTime();
//		System.out.println("Execution time: "+(end-start));
//		printStingInfo(sting);
//		
//		start = System.nanoTime();
//		sting.execute();
//		end = System.nanoTime();
//		System.out.println("Execution time: "+(end-start));
//		printStingInfo(sting);
//		
//		start = System.nanoTime();
//		sting.execute();
//		end = System.nanoTime();
//		System.out.println("Execution time: "+(end-start));
//		printStingInfo(sting);
//		
//		start = System.nanoTime();
//		sting.execute();
//		end = System.nanoTime();
//		System.out.println("Execution time: "+(end-start));
//		
//		printStingInfo(sting);
		
//		sting.execute();
//		printStingInfo(sting);
		
		totalEnd = System.nanoTime();
		System.out.println("\nTotal Time: "+(totalEnd - totalStart));
	}
	
	private static void printStingInfo(Sting sting){
//		Cell[] activeCells = sting.getActiveCells();
//		System.out.println("Number of Cells Left: "+activeCells.length);
//		for(Cell c : activeCells){
//			System.out.println("Cell Content: "+c);
//		}
	}
	
	private static Sting constructSting(){
		String baseName = "/Users/theflyingwolves/Desktop/photo/snow_";
		String typeName = ".JPG";
		int count = 1;
		ImageSequenceLoader loader = new ImageSequenceLoader(baseName, typeName, count);
		Mat frame = loader.getFrameAsMat();
		Mat grayscaleFrame = convertToGrayScale(frame);
		BufferedImage image = Utility.matToBufferedImage(grayscaleFrame);
//		showMat(image);

		int[][] frameAsIntArray = Utility.bufferedImageTo2DArray(image);
		Cell[][] cells = constructCellArray(frameAsIntArray);
		Sting sting = new Sting(cells,4);
		return sting;
	}
	
	private static void showMat(BufferedImage image){
		System.out.println("Width: "+image.getWidth()+" Height: "+image.getHeight());
		window.setSize(image.getWidth(), image.getHeight());
		window.showFrame(image);
	}
	
	private static Mat convertToGrayScale(Mat mat){
		Mat result = new Mat();
		Imgproc.cvtColor(mat, result,Imgproc.COLOR_BGR2GRAY);
		return result;
	}
	
	private static Cell[][] constructCellArray(int[][] frame){
		Cell[][] cells = new Cell[frame.length][frame[0].length];
		Significance sig;
		int threshold = 120;
		for(int i=0;i<frame.length;i++){
			for(int j=0;j<frame[0].length;j++){
				if(frame[i][j] > threshold){
					sig = new Significance(true);
				}else{
					sig = new Significance(false);
				}
				
				cells[i][j] = new Cell(sig);
			}
		}
		
		return cells;
	}
}