package Flowable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import routing.Address;
import topology.Edge;

public interface Flowable {
	
	public static class Flow {	
		private Address address;
		private int borderRouter;
		private int path_length;
		private Queue<Integer> path;
		private int last;
		
		private Flow(Address address, int borderRouter) {
			this.address = address;
			this.borderRouter = borderRouter;
			this.path_length = 0;
			this.path = new LinkedList<Integer>();
			path.add(borderRouter);
		}
		
		public Iterable<Integer> path() {
			return path;
		}
		
		private void addNode(int n) {
			last = n;
			path_length++;
			path.add(n);
		}
		
		public Address address() {
			return address;
		}
		
		public int borderRouter() {
			return borderRouter;
		}
		
		public int pathLength() {
			return path_length;
		}
		
		public int last() {
			return last;
		}
		
		public String toString() {
			StringBuilder bld = new StringBuilder();
			
			bld.append("IN -> ");
			for(Integer i : path) {
				bld.append(i + " -> ");
			}
			
			bld.append("OUT");
			return bld.toString();
		}
	}
	
	public static class FlowBuilder {
		
		private Flow flow;
		
		public FlowBuilder(Address address, int border_router) {
			this.flow = new Flow(address, border_router);
		}
		
		public FlowBuilder(Iterable<Edge> edges) {
			this.flow = null;
			Edge e = null;
			Iterator<Edge> iterator = edges.iterator();
			while(iterator.hasNext()) {
				e = iterator.next();
				if(flow == null)
					flow = new Flow(null, e.from());
				else
					flow.addNode(e.from());
			}
			flow.path.add(e.other(e.from()));
		}
		
		public FlowBuilder addNode(int n) {
			flow.addNode(n);
			return this;
		}
		
		public Flow build() {
			return flow;
		}
		
	}
	
	public Flow flow(Address address, int borderRouter);

}
