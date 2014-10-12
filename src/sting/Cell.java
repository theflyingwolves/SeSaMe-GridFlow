package sting;

import org.opencv.core.Point;

import MotionDetectionUtility.Vector;

public class Cell implements CellType{
	private boolean isActive;
	private Combinable property;
	private int level;
	private Point center;
	
	public Cell(Combinable prop, double row, double col){
		property = prop;
		isActive = true;
		level = 0;
		center = new Point(row,col);
	}
	
	public Cell(Combinable prop, Point center){
		this(prop, center.x, center.y);
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public void setLevel(int newLevel){
		this.level = newLevel;
	}
	
	public Point getCenter(){
		return this.center;
	}
	
	public Cell combineWith(Cell c){
		Combinable combinedProperty = property.combineWith(c.getProperty());
		Point centerC = c.getCenter();
		Point combinedCenter = new Point((centerC.x+center.x) / 2, (centerC.y + center.y)/2);
		return new Cell(combinedProperty, combinedCenter);
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
		Point avgCenter = new Point(sumCenter.x / len, sumCenter.y / len);
		return new Cell(combinedProperty, avgCenter);
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