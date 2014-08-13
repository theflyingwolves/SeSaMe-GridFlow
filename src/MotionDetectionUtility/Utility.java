package MotionDetectionUtility;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

public class Utility {
   /**  
    * Converts/writes a Mat into a BufferedImage.  
    *  
    * @param matrix Mat of type CV_8UC3 or CV_8UC1  
    * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
    */  
	public static BufferedImage matToBufferedImage(Mat matrix) {  
	     int cols = matrix.cols();  
	     int rows = matrix.rows();  
	     int elemSize = (int)matrix.elemSize();
	     byte[] data = new byte[cols * rows * elemSize];  
	     int type;
	     matrix.get(0, 0, data);
	     switch (matrix.channels()) {  
	       case 1:  
	         type = BufferedImage.TYPE_BYTE_GRAY;  
	         break;  
	       case 3:  
	         type = BufferedImage.TYPE_3BYTE_BGR;  
	         byte b;  
	         for(int i=0; i<data.length; i=i+3) {  
	           b = data[i];  
	           data[i] = data[i+2];  
	           data[i+2] = b;  
	         }
	         break;
	       default:  
	         return null;  
	     }  
	     BufferedImage image2 = new BufferedImage(cols, rows, type);  
	     image2.getRaster().setDataElements(0, 0, cols, rows, data);  
	     return image2;
	}
	
	public static double[] matToDoubleArr(Mat mat){
//		Mat temp = mat.clone();
//		temp.convertTo(temp, CvType.CV_64FC3);
//		int size2 = (int)temp.total()*temp.channels();
//		double[] arr2 = new double[size2];
//		temp.get(0, 0, arr2);
		
		int size = (int)mat.total()*mat.channels();
		int index = 0;
		int totalRows = mat.rows();
		int totalCols = mat.cols();
		double[] arr = new double[size];
		for(int row = 0; row < totalRows; row++){
			for(int col = 0; col < totalCols; col++){
				index = row*totalCols+col;
				arr[index] = mat.get(row, col)[0];
			}
		}
		
		return arr;
	}
	
	public static int[][] bufferedImageTo2DArray(BufferedImage image){
		 final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	     final int width = image.getWidth();
	     final int height = image.getHeight();

	     int[][] result = new int[height][width];
	      
         final int pixelLength = 1;
         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            int grayscale = pixels[pixel] & 0xff;
            result[row][col] = grayscale;
            col++;
            if (col == width) {
               col = 0;
               row++;
            }
         }

	     return result;
	}
}