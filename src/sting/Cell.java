package sting;

import org.opencv.core.Point;

import MotionDetectionUtility.Vector;

public class Cell implements CellType{
	private boolean isActive;
	private Combinable property;
	private int level;
//	private Point center;
	private double centerX, centerY;
	
	public Cell(Combinable prop, double row, double col){
		property = prop;
		isActive = true;
		level = 0;
		centerX = row;
		centerY = col;
	}
	
//	public Cell(Combinable prop, Point center){
//		this(prop, center.x, center.y);
//	}
	
	public int getLevel(){
		return this.level;
	}
	
	public void setLevel(int newLevel){
		this.level = newLevel;
	}
	
	public Point getCenter(){
		return new Point(centerX, centerY);
	}
	
	public Cell combineWith(Cell c){
		Combinable combinedProperty = property.combineWith(c.getProperty());
		Point centerC = c.getCenter();
		double newX = (centerC.x+centerX) / 2;
		double newY = (centerC.y+centerY) / 2;
		
		return new Cell(combinedProperty, newX, newY);
	}
	
	public static Cell combineArray(Cell[] cells){
		int len = cells.length;
		Combinable[] properties = new Combinable[len];
		Point sumCenter = new Point(0,0);
		
		for(int i=0; i<len; i++){
			Cell c = cells[i];
			Point center = c.getCenter();
			properties[i] = c.getProperty();
			sumCenter = new Point(sumCenter.x+center.x, sumCenter.y+center.y);
		}
		
		Combinable combinedProperty = properties[0].combineWith(properties);
		return new Cell(combinedProperty, sumCenter.x / len, sumCenter.y / len);
	}
	
	public Combinable getProperty(){
		return property;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public CellType[] getFrontiers() {
		CellType[] frontiers = new CellType[1];
		frontiers[0] = this;
		return frontiers;
	}

	@Override
	public CellT getType() {
		return CellT.Cell;
	}

	@Override
	public void deactivate() {
		isActive = false;
	}
	
	public String toString(){
		return property.toString();
	}
}