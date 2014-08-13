package kMean;

public class TwoMeanThresholdSelector {
	private KMean kMean;
	private float threshold;
	public TwoMeanThresholdSelector(float[] data){
		float[] means = getInitialMeansFromData(data);
		kMean = new KMean(data,means);
		performKMeanIterations();
	}
	
	private float[] getInitialMeansFromData(float[] data){
		float min = data[0];
		float max = data[0];
		for(float d : data){
			if(d < min){
				min = d;
			}
			
			if(d > max){
				max = d;
			}
		}
		
		float[] means = new float[2];
		means[0] = min;
		means[1] = max;
		return means;
	}
	
	private void performKMeanIterations(){
		float prevMean = 0;
		Cluster[] clusters = kMean.getClusters();
		float currMean = (float) clusters[1].getMean();
		while(Math.abs(prevMean-currMean) >= 0.1){
			kMean.cluster();
			prevMean = currMean;
			currMean = (float) kMean.getClusters()[1].getMean();
		}
		System.out.println("Mean 0: "+kMean.getClusters()[0].getMean() +" Mean 1: "+kMean.getClusters()[1].getMean());
		System.out.println("Variance 0: "+kMean.getClusters()[0].getVariance()+" Variance 1: "+kMean.getClusters()[1].getVariance());
		clusters = kMean.getClusters();
		determineThreshold(clusters);
	}
	
	private void determineThreshold(Cluster[] clusters){
		float m1 = (float) clusters[0].getMean();
		float m2 = (float) clusters[1].getMean();
		float v1 = (float) clusters[0].getVariance();
		float v2 = (float) clusters[1].getVariance();
		float std1 = (float) Math.sqrt(v1);
		float std2 = (float) Math.sqrt(v2);
		
		if(std1 != 0 && std2 != 0){
			float A = v2 - v1;
			float B = 2*m2*v1 - 2*m1*v2;
			float C = (float) (v2*m1*m1-v1*m2*m2-2*v1*v2*Math.log(std2 / std1));
			float root = (float) ((-B+Math.sqrt(B*B-4*A*C))/(2*A));
			System.out.println("Root solved to be "+root);
//			threshold = (float) ((Math.exp(-(root-m1)*(root-m1)/(2*std1*std1)))/(Math.sqrt(2*Math.PI)*std1));
			threshold = root;
		}else{
			threshold = 0;
		}
	}
	
	public float getThreshold(){
		return threshold;
	}
}
