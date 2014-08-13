package MotionDetectionUtility;

public class MeanVarianceAccumulator {
	private double mean;
	private double variance;
	private double sumOfSquares;
	private int count;
	private double p = 0.5;
	private double candidate;
	private final int candidateUnsetValue = -1;
	private final int initializationCount = 10;
	
	public MeanVarianceAccumulator(){
		this.mean = 0;
		this.variance = 0;
		this.count = 0;
		this.sumOfSquares = 0;
		this.candidate = candidateUnsetValue;
	}
	
	public void accumulateCandidate(){
		if(this.candidate != this.candidateUnsetValue && this.candidate > 0){
			double x = this.candidate;
			if(this.count < this.initializationCount || this.variance == 0){
				double sum = this.mean * this.count;
				this.count ++;
				this.mean = (sum+x) / this.count;
				this.sumOfSquares = this.sumOfSquares + x*x;
				this.variance = this.sumOfSquares / this.count - this.mean * this.mean;
			}else{
//				this.p = 0.5*(1.0f/Math.sqrt(2*Math.PI*this.variance)) * Math.exp(-((x-this.mean)*(x-this.mean))/(2*this.variance));
				this.mean = (1-p)*this.mean + p*x;
				this.variance = (1-p)*this.variance + p*(x-this.mean)*(x-this.mean);
				this.count++;
				this.sumOfSquares = this.sumOfSquares+x*x;
			}
		}
	}
	
	public void addCandidate(double x){
		this.candidate = x;
		if(this.count < this.initializationCount){
			this.accumulateCandidate();
		}
	}
	
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
	
	public boolean isCandidateWithinConfidenceInterval(){
		
		double std = Math.sqrt(this.variance);
		
		if(this.candidate >= this.mean - 10*std &&
				this.candidate <= this.mean + 10*std){
//			if(std > 0){
//				System.out.println("Mean: "+this.mean+" Candidate: "+this.candidate+" Std: "+std);
//			}

			return true;
		}else{
			if(std > 0){
//				System.out.println("Mean: "+this.mean+" Candidate: "+this.candidate+" Std: "+std);
			}
			return false;
		}
	}
}