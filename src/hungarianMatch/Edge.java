package hungarianMatch;

public class Edge<GType> {
	private GType node1;
	private GType node2;
	
	public Edge(GType node1, GType node2){
		this.node1 = node1;
		this.node2 = node2;
	}
	
	public GType getNode1(){
		return node1;
	}
	
	public GType getNode2(){
		return node2;
	}
	
	// Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	// Override
	public String toString(){
		String str = "("+node1.toString()+", "+node2.toString()+")";
		return str;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Edge)){
			return false;
		}
		
		if(obj == this){
			return true;
		}
		
		@SuppressWarnings("unchecked")
		Edge<Integer> e2 = (Edge<Integer>) obj;
		return this.toString().equalsIgnoreCase(e2.toString());
	}
}
