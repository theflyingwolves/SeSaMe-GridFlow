package thresholdAnalyzer;

public class DynamicThresholdSelector {
	private double percentile;
	private double threshold;
	private int greaterThanCount;
	private int lessThanCount;
	private final double controlFactor = 0.1;
	private final int initialThreshold = 4;
	private double maximum;
	private double minimum;
	
	public DynamicThresholdSelector(double percentile){
		this.percentile = percentile;
		this.greaterThanCount = 0;
		this.lessThanCount = 0;
		this.threshold = this.initialThreshold;
		this.maximum = Integer.MIN_VALUE;
		this.minimum = Integer.MAX_VALUE;
	}
	
	public void feed(double elem){
		if(elem > this.threshold){
			this.greaterThanCount ++;
		}else{
			this.lessThanCount ++;
		}
		
		if(elem > this.maximum){
			this.maximum = elem;
		}
		
		if(elem < this.minimum){
			this.minimum = elem;
		}
		
		double currPercentile = (double)this.lessThanCount / (double)(this.lessThanCount + this.greaterThanCount);
		if(currPercentile > this.percentile + controlFactor*this.percentile){
			this.threshold = (this.threshold-1) < this.minimum? this.minimum : (this.threshold-1);
		}else if(currPercentile < this.percentile - controlFactor*this.percentile){
			this.threshold = (this.threshold+1) > this.maximum? this.maximum : (this.threshold+1);
		}
	}
	
	public double getThreshold(){
		return this.threshold;
//		return 5;
	}
	
	public void resetSelector(){
		this.greaterThanCount = 0;
		this.lessThanCount = 0;
		this.threshold = this.initialThreshold;
		this.maximum = Integer.MIN_VALUE;
		this.minimum = Integer.MAX_VALUE;
	}
	
//	public int getGreaterThanCount(){
//		return this.greaterThanCount;
//	}
//	
//	public int getLessThanCount(){
//		return this.lessThanCount;
//	}
//	
//	public static void main(String[] args){
//		DynamicThresholdSelector selector = new DynamicThresholdSelector(0.5);
//		int[] data = {3,4,1,5,4,6,7,8,5,6,4,6,5,9,2,1,3,5,8,1,3,9,3,4,6,2,4,7,8,0,1,3,6,8,4};
//		System.out.println("Length: "+data.length);
//		for(int index = 0; index < data.length; index++){
//			int elem = data[index];
//			selector.feed(elem);
//			System.out.println("Less Than Count: "+selector.getLessThanCount());
//			System.out.println("Threshold: "+selector.getThreshold());
//		}
//	}
}
