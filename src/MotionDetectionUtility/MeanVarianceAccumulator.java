package MotionDetectionUtility;

public class MeanVarianceAccumulator {
	private double mean;
	private double variance;
	private double sumOfSquares;
	private int count;
	
	private final int initializationCount = 10;
	
	public MeanVarianceAccumulator(){
		this.mean = 0;
		this.variance = 0;
		this.count = 0;
		this.sumOfSquares = 0;
	}
	
	public void accumulate(double x){
		double sum = this.mean * this.count;
		this.count ++;
		this.mean = (sum+x) / this.count;
		this.sumOfSquares = this.sumOfSquares + x*x;
		this.variance = this.sumOfSquares / this.count - this.mean * this.mean;
	}
	
//	public void addCandidate(double x){
//		this.candidate = x;
//		if(this.count < this.initializationCount){
//			this.accumulateCandidate();
//		}
//	}
	
	public double getMean(){
		return this.mean;
	}
	
	public double getVariance(){
		return this.variance;
	}
	
	public double getSumOfSquares(){
		return this.sumOfSquares;
	}
	
	public int getCount(){
		return this.count;
	}
	
	public boolean isPointWithinConfidenceInterval(double x){
		double std = Math.sqrt(this.variance);
		double confidenceControl = 3;
		if(x >= this.mean - confidenceControl*std &&
				x <= this.mean + confidenceControl*std){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] args){
		MeanVarianceAccumulator mvAcc = new MeanVarianceAccumulator();
		for(int i=0; i<100; i++){
			mvAcc.accumulate(10);
		}
		System.out.println("The mean is "+mvAcc.getMean()+" and the variance is "+mvAcc.getVariance());
	}
}