package sting;

import java.util.ArrayList;

public class Sting {
	private ArrayList<Layer> layers;
	private final int unitLength;
	
	public Sting(Cell[][] dataCells, int unitLength){	
		this.unitLength = unitLength;
		initWithDataCells(dataCells);
	}
	
	private void initWithDataCells(Cell[][] dataCells){
		layers = new ArrayList<Layer>();
		Layer layerZero = new Layer(this.unitLength,dataCells);
		layers.add(layerZero);
	}
	
	public void execute(){
		Layer topLayer = getTopLayer();
		Layer upperLayer = topLayer.layerWisePropagate();
		layers.add(upperLayer);
	}
	
	public void printInfo(){
		Layer layer = getTopLayer();
		layer.printInfo();
	}
	
	private Layer getTopLayer(){
		int index = layers.size()-1;
		return layers.get(index);
	}
	
	public Cell[] getActiveCells(){
		Layer topLayer = getTopLayer();
		return topLayer.getActiveCells();
	}
}
