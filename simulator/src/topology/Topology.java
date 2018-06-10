package topology;


import java.util.LinkedList;
import java.util.Scanner;

public class Topology {
	
	private int V;
	private int E;
	private LinkedList<Edge> adj[];
	
	@SuppressWarnings("unchecked")
	public Topology(int V) {
		this.V = V;
		this.E = 0;
		
		adj = (LinkedList<Edge>[]) new LinkedList[V];
		for(int i = 0 ; i < V ; i++) {
			adj[i] = new LinkedList<Edge>();
		}
	}
	
	public Topology(Scanner sc) {
		this(sc.nextInt());
		while(sc.hasNextInt()) {
			int v = sc.nextInt();
			int w = sc.nextInt();
			
			addEdge(new Edge(v, w));
		}
	}
	
	public int V() {
		return this.V;
	}
	
	public int E() {
		return this.E;
	}
	
	public Edge link(int v, int w) {
		for(Edge e : adj[v]) {
			if(e.other(v) == w) {
				return e;
			}
		}
		
		return null;
	}
	
	public void addEdge(Edge e) {
		E++;
		int v = e.from();
		adj[v].add(e);
		adj[e.other(v)].add(e);
		
	}
	
	public Iterable<Edge> adj(int v) {
		return adj[v];
	}

}
