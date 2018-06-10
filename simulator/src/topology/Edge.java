package topology;

public class Edge {
	
	private int v;
	private int w;
	private int weight;
	
	public Edge(int v, int w) {
		this(v, w, 1);
	}
	
	public Edge(int v, int w, int weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
	}
	
	public int weight() {
		return weight;
	}
	
	public int from() {
		return this.v;
	}
	
	public String toString() {
		return v + " -> "  + w;
	}
	
	public int other(int n) {
		if(n == this.v) return this.w;
		else if(n == this.w) return this.v;
		else throw new IllegalArgumentException("The parameter is not an extrmity of the edge");
	}

}
