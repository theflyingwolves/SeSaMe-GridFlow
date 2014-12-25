package MotionDetectionUtility;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class VideoDisplayer {
	private JFrame frame;
	private JLabel label;
	
	public VideoDisplayer(){
		 frame = new JFrame("Video Analysis");
		 label = new JLabel();
		 frame.getContentPane().add(label);
		 frame.setVisible(true); 
	}
	
	public void setSize(int width, int height){
		frame.setSize(width,height);
	}
	
	public void showFrame(BufferedImage img){
		label.setIcon(new ImageIcon(img));
	}
}
