package sting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CellGroup implements CellType{
	private boolean isActive;
	private CellType[] cells;
	private CellType[] frontiers;
	
	public CellGroup(CellType[] cells){
		this.cells = cells;
		isActive = true;
		initFrontiers();
	}
	
	private void initFrontiers(){
		ArrayList<CellType> frontier = new ArrayList<CellType>();
		for(CellType cellT : this.cells){
			if(cellT != null && cellT.isActive()){
				switch(cellT.getType()){
				case Cell:
					frontier.add(cellT);
					break;
				case CellGroup:
					List<CellType> groupFrontiers = Arrays.asList(cellT.getFrontiers());
					frontier.addAll(groupFrontiers);
					break;
				}
			}
		}
		
		frontiers = new CellType[frontier.size()];
		int counter = 0;
		for(CellType c : frontier){
			frontiers[counter] = c;
			counter++;
		}
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public CellType[] getFrontiers() {
		return frontiers;
	}

	@Override
	public CellT getType() {
		return CellT.CellGroup;
	}

	@Override
	public void deactivate() {
		isActive = false;
	}
	
}
