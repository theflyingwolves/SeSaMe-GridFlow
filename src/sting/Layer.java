package sting;

import java.util.ArrayList;

import sting.CellType.CellT;

public class Layer {
	private CellType[][] cells;
	private int unitLength;
	
	public Layer(int unitLength, CellType[][] dataCells){
		this.unitLength = unitLength;
		this.cells = dataCells;
	}
	
	public Cell[] getActiveCells(){
		ArrayList<Cell> activeCellsInArray = new ArrayList<Cell>();
		for(int i=0;i<cells.length;i++){
			for(int j=0;j<cells[0].length;j++){
				CellType ct = cells[i][j];
				if(ct.getType() == CellT.Cell){
					activeCellsInArray.add((Cell)ct);
				}else{
					activeCellsInArray.addAll(getActiveCellsInCellGroup((CellGroup)ct));
				}
			}
		}
		
		Cell[] activeCells = new Cell[activeCellsInArray.size()];
		for(int i = 0; i < activeCellsInArray.size(); i++){
			activeCells[i] = activeCellsInArray.get(i);
		}
		
		return activeCells;
	}
	
	public void printInfo(){
		for(int i=0; i<cells.length; i++){
			for(int j=0; j<cells[0].length; j++){
				System.out.print(cells[i][j]+" ");
			}
			System.out.println(" ");
		}
	}
	
	private ArrayList<Cell> getActiveCellsInCellGroup(CellGroup cg){
		ArrayList<Cell> activeCells = new ArrayList<Cell>();
		CellType[] frontiers = cg.getFrontiers();
		for(CellType ct : frontiers){
			if(ct.getType() == CellT.Cell){
				activeCells.add((Cell)ct);
			}else{
				ArrayList<Cell> subActiveCells = getActiveCellsInCellGroup((CellGroup)ct);
				activeCells.addAll(subActiveCells);
			}
		}
		
		return activeCells;
	}
	
	public CellType[][][] groupAdjacentCells(){
		int numOfGroupsPerRow = (int) Math.ceil((double)cells.length / this.unitLength);
		int numOfGroupsPerCol = (int) Math.ceil((double)cells[0].length / this.unitLength);
		int numOfCellsPerGroup = this.unitLength * this.unitLength;
		CellType[][][] cellsInGroups = new CellType[numOfGroupsPerRow][numOfGroupsPerCol][numOfCellsPerGroup];
		for(int x = 0; x < cells.length; x+=this.unitLength){
			for(int y = 0; y < cells[0].length; y+=this.unitLength){
				for(int offsetX = 0; offsetX < this.unitLength; offsetX ++){
					for(int offsetY = 0; offsetY < this.unitLength; offsetY ++){
						if(x+offsetX < cells.length && y+offsetY < cells[0].length){
							cellsInGroups[x/this.unitLength][y/this.unitLength][offsetX*this.unitLength+offsetY] = 
								cells[x+offsetX][y+offsetY];
						}
					}
				}
			}
		}
		return cellsInGroups;
	}
	
	public Layer layerWisePropagate(){
		CellType[][][] cellGroups = groupAdjacentCells();
		CellType[][] updatedCells = new CellType[cellGroups.length][cellGroups[0].length];
		CellType combinedCell = null;
		CellType[] cellsInGroup;
		
		for(int i=0; i< cellGroups.length;i++){
			for(int j=0; j<cellGroups[0].length;j++){
				cellsInGroup = cellGroups[i][j];
				combinedCell = combineAll(cellsInGroup);
				updatedCells[i][j] = combinedCell;
			}
		}
		
		Layer updatedLayer = new Layer(unitLength, updatedCells);
		return updatedLayer;
	}
	
	private CellType combineAll(CellType[] cellsToCombine){
		boolean isCombinable = testCombinability(cellsToCombine);
		boolean shouldCombine = shouldCombineCells(cellsToCombine);
		int oldLevelNumber = -1;
		if(isCombinable && shouldCombine){
			Cell combinedCell = null;
			for(CellType c : cellsToCombine){
				if(c!=null){
					Cell cell = (Cell)c;
					
					if(oldLevelNumber == -1){
						oldLevelNumber = cell.getLevel();
					}
					
					if(combinedCell == null){
						combinedCell = cell;
					}else{
						combinedCell = combinedCell.combineWith(cell);
					}
				}
			}
			combinedCell.setLevel(oldLevelNumber+1);
			return combinedCell;
		}else{
			CellGroup cellsInGroup = new CellGroup(cellsToCombine);
			return cellsInGroup;
		}
	}
	
	private boolean shouldCombineCells(CellType[] cells){
		Combinable[] props = getCombinablePropertiesFromCells(cells);
		if(props != null){
			return props[0].shouldPropagate(props);
		}else{
			return false;
		}
	}
	
	private boolean testCombinability(CellType[] cells){
		boolean flag = true;
		
		for(CellType c : cells){
			if(c != null && c.getType() == CellT.CellGroup){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	private Combinable[] getCombinablePropertiesFromCells(CellType[] cells){
		Combinable[] props = new Combinable[cells.length];
		boolean flag = true;
		
		for(int i=0;i<cells.length;i++){
			if(cells[i]!=null &&cells[i].getType()!=CellT.CellGroup){
				Cell c = (Cell)cells[i];
				props[i] = c.getProperty();
			}else{
				flag = false;
				break;
			}
		}
		
		if(flag){
			return props;
		}else{
			return null;
		}
	}
}
