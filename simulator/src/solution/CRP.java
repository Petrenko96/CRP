package solution;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

import Flowable.Flowable;
import messages.AbstractMessage;
import messages.UCTCMessage;
import messages.UDWNMessage;
import messages.UFWDMessage;
import messages.UNBRMessage;
import routing.Address;
import routing.Prefix;
import routing.Route.Priority;
import routing.RoutingTable2;
import topology.Edge;
import topology.ShortestPath;
import topology.Topology;
import routing.Route;

public class CRP extends Topology implements Flowable{
	
	private RoutingTable2 routing[];
	private int group[];
	private int root[];
	
	private void init(int[] root) {
		this.routing = new RoutingTable2[V()];
		this.group = new int[V()];

		ShortestPath[] sp = new ShortestPath[root.length];
		for(int i = 0 ; i < root.length ; i++) {
			sp[i] = new ShortestPath(this, root[i]);
		}
		
		for(int i = 0 ; i < V() ; i++) {
			routing[i] = new RoutingTable2();
			int group = Integer.MAX_VALUE;
			for(int j = 0 ; j < root.length ; j++) {
				if(sp[j].pathLength(i) < group) {
					group = sp[j].pathLength(i);
				}
			}
			this.group[i] = group;
		}
		
		this.root = root;
		
	}
	
	public CRP(int V, int[] root) {
		super(V);
		init(root);
	}
	
	public CRP(Scanner sc, int[] root) {
		super(sc);
		init(root);
	}
	
	private void send(AbstractMessage mess, PriorityQueue<AbstractMessage> pq) {
		
		Route r = new Route(mess.borderRouter(), mess.prefix(), mess.destination(), mess.preference(), mess.ASPathLength(), mess.pathLength(), mess.priority());
		if(routing[mess.destination()].contains(r, mess.source()))
				return;
		
		if(mess instanceof UCTCMessage) {
			ShortestPath sp = new ShortestPath(this, mess.source());
			for(Edge e : sp.path(mess.destination())) {
				mess.increaseDelay(e.weight());
				mess.increasePathLength(1);
			}
			pq.add(mess);
		} else {
			for(Edge e : adj(mess.source())) {
				int w = e.other(mess.source());
				if(w == mess.destination()) {
					mess.increaseDelay(e.weight());
					pq.add(mess);
				}
			}
		}
	}
	
	private boolean rootContains(int i) {
		for(int j = 0 ; j < root.length ; j++) {
			if(root[j] == i)
				return true;
		}
		
		return false;
	}
	
	public String toString() {
		StringBuilder bld = new StringBuilder();
		
		for(int i = 0 ;  i < V() ; i++) {
			bld.append("Node " + i);
			bld.append(rootContains(i) ? " - root\n" : "\n");
			bld.append("Group "+group[i]+"\n");
			bld.append(routing[i]+"\n");
		}
		
		bld.append("\n");
		return bld.toString();
	}

	static 		int i = 0;
	public void addRoute(int borderRouter, int preference, int as_path_length, Prefix prefix, Priority priority) {
		PriorityQueue<AbstractMessage> pq = new PriorityQueue<>();
		UFWDMessage mess2 = new UFWDMessage(borderRouter, -1, borderRouter, prefix, preference, as_path_length, 0, priority, 0);
		pq.add(mess2);
		
		while(!pq.isEmpty()) {
			AbstractMessage mess = pq.remove();
			if(mess instanceof UFWDMessage) {
				processUFWDMessage((UFWDMessage) mess, pq);
			}
			else if(mess instanceof UDWNMessage) {
				processUDWNMessage((UDWNMessage) mess, pq);
			}
			else if(mess instanceof UNBRMessage) {
				//processUNBRMessage((UNBRMessage) mess, pq);
			}
			else if(mess instanceof UCTCMessage) {
				processUCTCMessage((UCTCMessage) mess, pq);
			}
		}
	}
	
	private void processUCTCMessage(UCTCMessage mess, PriorityQueue<AbstractMessage> pq) {
		Route r = new Route(mess.borderRouter(), mess.prefix(), mess.source(), mess.preference(), mess.ASPathLength(), mess.pathLength(), mess.priority());
		if(routing[mess.destination()].contains(r, mess.source())) return;
		
		if(mess.priority() != Priority.LOW) {
			HashSet<Integer> sent = new HashSet<>();
			for(Route r2 : routing[mess.destination()].getAll()) {
				if(r2.prefix().related(r.prefix()) && r2.priority() != Priority.LOW) {
					for(int v : r2.forward()) {
						if(v >=0) {
							if(group[v] > group[mess.destination()]) {
								UDWNMessage dwn = mess.toDwn(mess.destination(), v);
								if(!sent.contains(v)) {
									send(dwn, pq);
									sent.add(v);
								}
							}
						}
					}
				}
			}
		}
		
		routing[mess.destination()].add(r, mess.source());
	}
		
	private void processUDWNMessage(UDWNMessage mess, PriorityQueue<AbstractMessage> pq) {		
		Route r = new Route(mess.borderRouter(), mess.prefix(), mess.source(), mess.preference(), mess.ASPathLength(), mess.pathLength(), mess.priority());
		
		if(routing[mess.destination()].contains(r, mess.source())) return;

		HashSet<Integer> sent = new HashSet<>();
		for(Route r2 : routing[mess.destination()].getAll()) {
			if(r2.prefix().related(mess.prefix())) {
				for(int v : r2.forward()) {
					if(v >= 0 && group[v] > group[mess.destination()] && !sent.contains(v)) {
						UDWNMessage dwn = new UDWNMessage(r, mess.destination(), v, 0);
						send(dwn, pq);
						sent.add(v);
					}
				}
			}
		}
		
		routing[mess.destination()].add(r, mess.source());
	}
	
	static int sentUFWD = 0;
	static int sentUDWN = 0;
	static int sentUDWN_f = 0;
	static int sentUDWN_c = 0;
	static int sentUDWN_d = 0;
	private void processUFWDMessage(UFWDMessage mess, PriorityQueue<AbstractMessage> pq) {		
		Route r = new Route(mess.borderRouter(), mess.prefix(), mess.source(), mess.preference(), mess.ASPathLength(), mess.pathLength(), mess.priority());
		
		if(routing[mess.destination()].contains(r, mess.source())) return;
		
		boolean b = routing[mess.destination()].alreadyReceivedRelated(r, mess.source());
		
		if(mess.priority() != Priority.LOW) {
			HashSet<Integer> sent = new HashSet<>();
			for(Route r2 : routing[mess.destination()].getAll()) {
				if( r2.prefix().related(r.prefix()) && r2.priority() != Priority.LOW) {
					boolean sendDown = false;
					for(int v : r2.forward()) {
						if(v != mess.source()) {
							sendDown = true;
							if(v < 0 || (group[v] > group[mess.destination()] || (group[v] == 0 && group[mess.destination()] > 0))) {
								if(v >= 0 && group[v] > 0) {
									UDWNMessage dwn = mess.toDwn(mess.destination(), v);
									if(!sent.contains(v)) {
										send(dwn, pq);
										sent.add(v);
									}
								}
								
								sendDown = true;
							} else if(!rootContains(v) && group[v] == group[mess.destination()]) {
								sendDown = true;
							} else if(v >= 0 && group[v] == 0 && group[mess.destination()] == 0) {
								sendDown = true;
							} else {
							}
						}
					}
					if(sendDown && mess.source() >= 0 && !b) {
						UDWNMessage dwn2 = new UDWNMessage(r2, mess.destination(), mess.source(), mess.delay());
						send(dwn2, pq);
					}
				}
			}
		}
		
		this.routing[mess.destination()].add(r, mess.source());
		
		for(Edge e : adj(mess.destination())) {
			int v = e.other(mess.destination());
			if(group[v] < group[mess.destination()]) {
				UFWDMessage fwd = new UFWDMessage(mess, mess.destination(), v);
				send(fwd, pq);
			}
		}
		
		if(rootContains(mess.destination())) {
			for(int v : root) {
				if(v != mess.destination()) {
					UCTCMessage ctc = new UCTCMessage(mess, mess.destination(), v);
					send(ctc, pq);
				}
			}
		}
				
	}

	@Override
	public Flow flow(Address address, int borderRouter) {
		FlowBuilder bld = new FlowBuilder(address, borderRouter);
		Route r = routing[borderRouter].best(address);
		
		int router = borderRouter;
		while(r == null) {
			int temp = router;
			for(Edge e : adj(router)) {
				int v = e.other(router);
				if(group[v] < group[temp] || (group[v] == group[temp] && v < temp)) {
					temp = v;
				}
			}
			router = temp;
			bld.addNode(router);
			r = routing[router].best(address);
		}
		
		int to = r.border_router();
		ShortestPath sp = new ShortestPath(this, router);
		for(Edge e : sp.path(to)) {
			int v = e.other(router);
			bld.addNode(v);
			router = v;
		}
		
		return bld.build();
	}
	
	//================= FOR TEST PURPOSES ONLY ========================
	public RoutingTable2 getRoutingTable(int i) {
		return routing[i];
	}

	public int group(int i) {
		return group[i];
	}
}
