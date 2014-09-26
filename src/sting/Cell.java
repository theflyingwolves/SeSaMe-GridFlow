package sting;

public class Cell implements CellType{
	private boolean isActive;
	private Combinable property;
	private int level;
	
	public Cell(Combinable prop){
		property = prop;
		isActive = true;
		level = 0;
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public void setLevel(int newLevel){
		this.level = newLevel;
	}
	
	public Cell combineWith(Cell c){
		Combinable combinedProperty = property.combineWith(c.getProperty());
		return new Cell(combinedProperty);
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