package kMean;

import java.util.ArrayList;

public class Cluster {
	private double mean;
	private double variance;
	private double sumOfSquares;
	private ArrayList<Double> dataStore;
	private int size;
	
	public Cluster(double mean){
		this.mean = mean;
		size = 0;
		sumOfSquares = 0;
		dataStore = new ArrayList<Double>();
	}
	
	public void feed(double data){
		sumOfSquares += data*data;
		dataStore.add(data);
		mean = (mean*size+data) / (size+1);
		variance = sumOfSquares / size - mean * mean;
		size++;
	}
	
	public double getMean(){
		return mean;
	}
	
	public double getVariance(){
		return variance;
	}
	
	public ArrayList<Double> getData(){
		return dataStore;
	}
	
	public int size(){
		return size;
	}
}
