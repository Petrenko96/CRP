package topology;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ShortestPath {
	
	private int dist[];
	public boolean marked[];
	private Edge edgeTo[];

	public ShortestPath(Topology t, int node) {
		dist = new int[t.V()];
		marked = new boolean[t.V()];
		edgeTo = new Edge[t.V()];
		bfs(t, node);
	}
	
	private void bfs(Topology t, int n) {
		Queue<Integer> q = new LinkedList<>();
		marked[n] = true;
		dist[n] = 0;
		q.add(n);
		while(!q.isEmpty()) {
			int s = q.remove();
			for(Edge e : t.adj(s)) {
				int v = e.other(s);
				if(!marked[v]) {
					marked[v] = true;
					dist[v] = dist[s]+1;
					edgeTo[v] = e;
					q.add(v);
				}
			}
		}
	}
	
	public boolean hasPath(int n) {
		return marked[n];
	}
	
	public int pathLength(int n) {
		if(!hasPath(n)) return Integer.MAX_VALUE;
		else return dist[n];
	}
	
	public Iterable<Edge> path(int n) {
		Deque<Edge> stack = new LinkedList<>();
		
		while(dist[n] != 0) {
			Edge e = edgeTo[n];
			stack.addFirst(e);
			n = e.other(n);
		}
		
		return stack;
	}
	
}
