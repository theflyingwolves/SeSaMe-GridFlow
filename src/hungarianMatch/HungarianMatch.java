package hungarianMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HungarianMatch<GType> {
	private BipartiteGraph<GType> originalGraph;
	private ArrayList<GType> rowLabels;
	private ArrayList<GType> colLabels;
	private Map<GType, GType> minCostMatch;
	private double[][] costMatrix;
	private int[] match;
	private int rows;
	private int cols;
	private int dim;
	private final double[] labelByWorker, labelByJob;
	private final int[] minSlackWorkerByJob;
	private final double[] minSlackValueByJob;
	private final int[] matchJobByWorker, matchWorkerByJob;
	private final int[] parentWorkerByCommittedJob;
	private final boolean[] committedWorkers;

	/**
	 * Construct an instance of the algorithm.
	 * 
	 * @param graphAsEdgeWeightMap
	 *            The bipartite graph that is used to run the algorithm
	 *            It should be represented as a mapping from edges to costs of the corresponding edge
	 */
	public HungarianMatch(Map<Edge<GType>, Double> graphAsEdgeWeightMap){
		this.originalGraph = new BipartiteGraph<GType>(graphAsEdgeWeightMap);
		this.rowLabels = originalGraph.getNodePartition1();
		this.colLabels = originalGraph.getNodePartition2();
		
		initCostMatrix();
		
		labelByWorker = new double[this.dim];
		labelByJob = new double[this.dim];
		minSlackWorkerByJob = new int[this.dim];
		minSlackValueByJob = new double[this.dim];
		committedWorkers = new boolean[this.dim];
		parentWorkerByCommittedJob = new int[this.dim];
		matchJobByWorker = new int[this.dim];
		Arrays.fill(matchJobByWorker, -1);
		matchWorkerByJob = new int[this.dim];
		Arrays.fill(matchWorkerByJob, -1);
		
		findMinCostMatch();
	}
	
	
	/**
	 * @return The minimum cost match as a map with keys from the first elements 
	 * 			of the edges and values from the second elements of the edges
	 * 
	 */
	public Map<GType,GType> getMinCostMatch(){
		return minCostMatch;
	}
	
	/**
	 * @return The maximum weight of the best matched edges.
	 */
	public double getMaxCost(){
		double maxCost = 0;
		double weight;
		initCostMatrix();
		for(int index = 0; index < match.length; index++){
			if(match[index] >= 0){
				weight = costMatrix[index][match[index]];
				maxCost = weight>maxCost? weight : maxCost;
			}
		}
		
		return maxCost;
	}
	
	/**
	 * @return Sum of weight of the best matched edges, i.e. total costs
	 */
	public double getTotalCosts(){
		double totalCost = 0;
		double weight;
		initCostMatrix();
		for(int index = 0; index < match.length; index++){
			if(match[index] >= 0){
				weight = costMatrix[index][match[index]];
				totalCost += weight;
			}
		}
		return totalCost;
	}
	
	private void initCostMatrix(){
		rows = rowLabels.size();
		cols = colLabels.size();
		dim = Math.max(rows, cols);
		costMatrix = new double[dim][dim];
		ArrayList<Double> allWeights = new ArrayList<Double>();
		
		for(int i=0;i<rows;i++){
			GType node1 = rowLabels.get(i);
			for(int j=0;j<cols;j++){
				GType node2 = colLabels.get(j);
				double weight = originalGraph.getEdgeWeightForNodes(node1, node2);
				costMatrix[i][j] = weight;
				allWeights.add(weight);
			}
		}
		
		if(rows > cols){
			double globalMax = 0;
			for(int i=0;i<allWeights.size();i++){
				double candidate = allWeights.get(i);
				if(globalMax < candidate){
					globalMax = candidate;
				}
			}
			
			for(int row=0; row<rows;row++){
				for(int col=cols;col<dim;col++){
					costMatrix[row][col] = globalMax;
				}
			}
		}
	}
	
	private void computeInitialFeasibleSolution() {
		for (int j = 0; j < dim; j++) {
			labelByJob[j] = Double.POSITIVE_INFINITY;
		}
		for (int w = 0; w < dim; w++) {
			for (int j = 0; j < dim; j++) {
				if (costMatrix[w][j] < labelByJob[j]) {
					labelByJob[j] = costMatrix[w][j];
				}
			}
		}
	}
	
	private void findMinCostMatch(){
		minCostMatch = new HashMap<GType, GType>();
		int colLabelSize = colLabels.size();
		
		match = execute();
		for(int i=0;i<match.length;i++){
			if(match[i] < colLabelSize && match[i] >= 0){
				minCostMatch.put(rowLabels.get(i), colLabels.get(match[i]));
			}
		}
	}
	
	private int[] execute() {
		/*
		 * Heuristics to improve performance: Reduce rows and columns by their
		 * smallest element, compute an initial non-zero dual feasible solution
		 * and create a greedy matching from workers to jobs of the cost matrix.
		 */
		reduce();
		computeInitialFeasibleSolution();
		greedyMatch();

		int w = fetchUnmatchedWorker();
		while (w < dim) {
			initializePhase(w);
			executePhase();
			w = fetchUnmatchedWorker();
		}
		int[] result = Arrays.copyOf(matchJobByWorker, rows);
		for (w = 0; w < result.length; w++) {
			if (result[w] >= cols) {
				result[w] = -1;
			}
		}
		return result;
	}

	private void executePhase() {
		while (true) {
			int minSlackWorker = -1, minSlackJob = -1;
			double minSlackValue = Double.POSITIVE_INFINITY;
			for (int j = 0; j < dim; j++) {
				if (parentWorkerByCommittedJob[j] == -1) {
					if (minSlackValueByJob[j] < minSlackValue) {
						minSlackValue = minSlackValueByJob[j];
						minSlackWorker = minSlackWorkerByJob[j];
						minSlackJob = j;
					}
				}
			}
			if (minSlackValue > 0) {
				updateLabeling(minSlackValue);
			}
			parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
			if (matchWorkerByJob[minSlackJob] == -1) {
				/*
				 * An augmenting path has been found.
				 */
				int committedJob = minSlackJob;
				int parentWorker = parentWorkerByCommittedJob[committedJob];
				while (true) {
					int temp = matchJobByWorker[parentWorker];
					match(parentWorker, committedJob);
					committedJob = temp;
					if (committedJob == -1) {
						break;
					}
					parentWorker = parentWorkerByCommittedJob[committedJob];
				}
				return;
			} else {
				/*
				 * Update slack values since we increased the size of the
				 * committed workers set.
				 */
				int worker = matchWorkerByJob[minSlackJob];
				committedWorkers[worker] = true;
				for (int j = 0; j < dim; j++) {
					if (parentWorkerByCommittedJob[j] == -1) {
						double slack = costMatrix[worker][j]
								- labelByWorker[worker] - labelByJob[j];
						if (minSlackValueByJob[j] > slack) {
							minSlackValueByJob[j] = slack;
							minSlackWorkerByJob[j] = worker;
						}
					}
				}
			}
		}
	}

	private int fetchUnmatchedWorker() {
		int w;
		for (w = 0; w < dim; w++) {
			if (matchJobByWorker[w] == -1) {
				break;
			}
		}
		return w;
	}

	private void greedyMatch() {
		for (int w = 0; w < dim; w++) {
			for (int j = 0; j < dim; j++) {
				if (matchJobByWorker[w] == -1
						&& matchWorkerByJob[j] == -1
						&& costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0) {
					match(w, j);
				}
			}
		}
	}

	private void initializePhase(int w) {
		Arrays.fill(committedWorkers, false);
		Arrays.fill(parentWorkerByCommittedJob, -1);
		committedWorkers[w] = true;
		for (int j = 0; j < dim; j++) {
			minSlackValueByJob[j] = costMatrix[w][j] - labelByWorker[w]
					- labelByJob[j];
			minSlackWorkerByJob[j] = w;
		}
	}

	private void match(int w, int j) {
		matchJobByWorker[w] = j;
		matchWorkerByJob[j] = w;
	}

	private void reduce() {
		for (int w = 0; w < dim; w++) {
			double min = Double.POSITIVE_INFINITY;
			for (int j = 0; j < dim; j++) {
				if (costMatrix[w][j] < min) {
					min = costMatrix[w][j];
				}
			}
			for (int j = 0; j < dim; j++) {
				costMatrix[w][j] -= min;
			}
		}
		double[] min = new double[dim];
		for (int j = 0; j < dim; j++) {
			min[j] = Double.POSITIVE_INFINITY;
		}
		for (int w = 0; w < dim; w++) {
			for (int j = 0; j < dim; j++) {
				if (costMatrix[w][j] < min[j]) {
					min[j] = costMatrix[w][j];
				}
			}
		}
		for (int w = 0; w < dim; w++) {
			for (int j = 0; j < dim; j++) {
				costMatrix[w][j] -= min[j];
			}
		}
	}

	private void updateLabeling(double slack) {
		for (int w = 0; w < dim; w++) {
			if (committedWorkers[w]) {
				labelByWorker[w] += slack;
			}
		}
		for (int j = 0; j < dim; j++) {
			if (parentWorkerByCommittedJob[j] != -1) {
				labelByJob[j] -= slack;
			} else {
				minSlackValueByJob[j] -= slack;
			}
		}
	}
}