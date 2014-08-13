package sting;

public interface CellType {
	public static enum CellT {
		Cell, CellGroup
	};
	public void deactivate();
	public boolean isActive();
	public CellType[] getFrontiers();
	public CellT getType();
}