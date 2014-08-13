package kMean;

public class KMean {
	private double[] data;
	private final int numOfClusters;
	private Cluster[] clusters;
	
	public KMean(double[] data, double[] clusterMeans){
		this.numOfClusters = clusterMeans.length;
		init(data,clusterMeans);
	}
	
	public KMean(float[] data, float[] clusterMeans){
		this.numOfClusters = clusterMeans.length;
		double[] dataAsDouble = floatToDouble(data);
		double[] meanAsDouble = floatToDouble(clusterMeans);
		init(dataAsDouble, meanAsDouble);
	}
	
	private void init(double[] data, double[] clusterMeans){
		this.data = data;
		initClustersByMeans(clusterMeans);
	}
	
	private double[] floatToDouble(float[] arr){
		double[] arrAsDouble = new double[arr.length];
		
		for(int i=0; i<arr.length; i++){
			arrAsDouble[i] = arr[i];
		}
		
		return arrAsDouble;
	}
	
	public void cluster(){
		prepareClustering();
		Cluster cluster;
		int index;
		
		for(double d : data){
			index = nearestMeanIndex(d);
			cluster = clusters[index];
			cluster.feed(d);
		}
	}
	
	private void prepareClustering(){
		double[] clusterMeans = new double[clusters.length];
		for(int i = 0; i < clusters.length; i++){
			clusterMeans[i] = clusters[i].getMean();
		}
		
		initClustersByMeans(clusterMeans);
	}
	
	
	private void initClustersByMeans(double[] clusterMeans){
		this.clusters = new Cluster[numOfClusters];
		for(int i=0;  i<numOfClusters; i++){
			clusters[i] = new Cluster(clusterMeans[i]);
		}
	}
	
	private int nearestMeanIndex(double data){
		int nearestIndex = 0;
		double minDist = Double.MAX_VALUE;
		double dist;
		Cluster cluster;
		
		for(int i=0;i<numOfClusters;i++){
			cluster = clusters[i];
			dist = Math.abs(data - cluster.getMean());
			if(dist < minDist){
				nearestIndex = i;
				minDist = dist;
			}
		}
		return nearestIndex;
	}
	
	public Cluster[] getClusters(){
		return clusters;
	}
	
	public void printData(){
		Cluster[] clusters = getClusters();
		for(Cluster cluster : clusters){
			System.out.println("Cluster: "+cluster.getMean());
//			System.out.println("Data: "+cluster.getData());
		}
	}
	
	public static void main(String[] args){
		double[] data = {1,2,2,2,3,2,3,3,3,4,4,3,5,5,6,7,7,6,6,7,7,9,9,9,9,9,9,8,8,9,9,9,9,19,10,10,10,10};
		double[] means = {1,10};
		KMean kMean= new KMean(data,means);
		kMean.cluster();
		kMean.printData();
		kMean.cluster();
		kMean.printData();
	}
}