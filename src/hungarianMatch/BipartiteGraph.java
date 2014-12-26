package hungarianMatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BipartiteGraph<GType> {
	private ArrayList<GType> nodePartition1;
	private ArrayList<GType> nodePartition2;
	private ArrayList<Edge<GType>> edges;
	private Map<Edge<GType>,Double> graph;
	
	public BipartiteGraph(Map<Edge<GType>,Double> graph){
		this.graph = graph;
		initEdges();
		initNodePartitions(edges);
	}
	
	private void initEdges(){
		this.edges = new ArrayList<Edge<GType>>();
		Set<Edge<GType>> keys = graph.keySet();
		Iterator<Edge<GType>> it = keys.iterator();
		while(it.hasNext()){
			Edge<GType> edge = it.next();
			this.edges.add(edge);
		}
	}
	
	private void initNodePartitions(ArrayList<Edge<GType>> edges){
		this.nodePartition1 = new ArrayList<GType>();
		this.nodePartition2 = new ArrayList<GType>();
		Iterator<Edge<GType>> it = edges.iterator();
		while(it.hasNext()){
			Edge<GType> edge = it.next();
			GType node1 = edge.getNode1();
			if(!nodePartition1.contains(node1)){
				nodePartition1.add(node1);
			}
			
			GType node2 = edge.getNode2();
			if(!nodePartition2.contains(node2)){
				nodePartition2.add(node2);
			}
		}
	}
	
	public double getEdgeWeightForNodes(GType node1, GType node2){
		Edge<GType> edge = new Edge<GType>(node1,node2);
		double weight = this.graph.get(edge);
		return weight;
	}
	
	public ArrayList<GType> getNodePartition1(){
		return this.nodePartition1;
	}
	
	public ArrayList<GType> getNodePartition2(){
		return this.nodePartition2;
	}
}
