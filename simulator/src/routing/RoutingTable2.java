package routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class RoutingTable2 {
	
	private ArrayList<Route> routes;
	
	public RoutingTable2() {
		routes = new ArrayList<>();
	}
	
	public void add(Route r, int s) {
		if(r == null)
			return;
		
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).border_router() == r.border_router() 
					&& routes.get(i).prefix().equals(r.prefix())) {
				routes.get(i).addForward(s);
				return;
			}
		}
		
		routes.add(r);
		
		RouteComparator c = new RouteComparator();
		Collections.sort(routes, c);
	}
	
	public int size() {
		return routes.size();
	}
	
	public void delete(Route r) {
		if(r == null)
			return;
		
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).equals(r)) {
				routes.remove(i);
				Collections.sort(routes, new RouteComparator());
				return;
			}
		}
	}
	
	public boolean alreadyReceivedRelated(Route r2, int source) {
		for(Route r : routes) {
			if(r.prefix().related(r2.prefix()) && r.forward().contains(source))
				return true;
		}
		return false;
	}
	
	public Iterable<Route> getAll() {
		ArrayList<Route> ret = new ArrayList<>(routes);
		return ret;
	}
	
	public boolean containsDwn(Route r, int source) {
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).border_router() == r.border_router()
					&& routes.get(i).prefix().equals(r.prefix())
					//&& (routes.get(i).forward().contains(source)|| routes.get(i).path_length() != r.path_length()))
					&& routes.get(i).forward().contains(source))
				return true;
		}
		
		return false;
	}
	
	public boolean contains(Route r, int source) {
		
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).border_router() == r.border_router()
					&& routes.get(i).prefix().equals(r.prefix())
					&& (routes.get(i).forward().contains(source) || routes.get(i).path_length() != r.path_length()))
				return true;
		}
		
		return false;
	}
	
	public Iterable<Route> get(Prefix p) {
		ArrayList<Route> ret = new ArrayList<>();
		
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).prefix().equals(p)) {
				ret.add(routes.get(i));
			}
		}
		
		return ret;
	}
	
	public boolean hasRelated(Prefix p) {
		for(int i = 0 ; i < routes.size() ; i++) {
			if(routes.get(i).prefix().related(p)) {
				return true;
			}
		}
		return false;
	}
	
	public Route best(Address addr) {
		Route r = null;
		Prefix p = null;
		for(int i = 0 ; i < routes.size() ; i++) {
			Prefix p2 = routes.get(i).prefix();
			if(p2.related(addr) && (p == null || p2.length() > p.length())) {
				r = routes.get(i);
				p = r.prefix();
			} else if(p2.related(addr) && p2.length() == p.length()) {
				if(routes.get(i).local_pref() > r.local_pref()) {
					r = routes.get(i);
					p = r.prefix();
				} else if(routes.get(i).local_pref() == r.local_pref()) {
					if(routes.get(i).as_path_length() < r.as_path_length()) {
						r = routes.get(i);
						p = r.prefix();
					} else if(routes.get(i).as_path_length() == r.as_path_length()) {
						if(routes.get(i).path_length() < r.path_length()) {
							r = routes.get(i);
							p = r.prefix();
						} else if(routes.get(i).path_length() == r.path_length()) {
							if(routes.get(i).border_router() < r.border_router()) {
								r = routes.get(i);
								p = r.prefix();
							}
						}
					}
				}
			}
		}
		
		return r;
	}
	
	public String toString() {
		StringBuilder bld = new StringBuilder();
		
		if(routes.size() == 0)
			return "Empty Routing Table";
		
		String header = "+--------------------+-----+------+---------+------------+----------+\n";
		bld.append(header);
		bld.append("|       Prefix       |  BR | Pref | AS Path | Path Length | Priority |\n");

		Route previous = null;
		for(Route r : routes) {
			if(previous == null || !r.prefix().equals(previous.prefix())) {
				bld.append(header);
				bld.append(String.format("| %18s |", r.prefix()));
			} else {
				bld.append("|                    |");
			}
			previous = r;
			bld.append(String.format(" %3d | %4d | %7d |%11d | %8s | ", r.border_router(), r.local_pref(), r.as_path_length(), r.path_length(), r.priority()));
		
			bld.append(r.forward());
			bld.append("\n");
		}
		
		bld.append(header);
		return bld.toString();
	}
	
	private class RouteComparator implements Comparator<Route> {

		@Override
		public int compare(Route arg0, Route arg1) {
			int i = -(arg0.prefix().length() - arg1.prefix().length());
			if(i != 0)
				return i;
			
			return new Integer(arg0.hashCode()).compareTo(arg1.hashCode());
		}
		
	}
	
	/* DEBUG PURPOSE ONLY -> TO NOT USE IN THE CODE */
	public Set<Route> getSet(Prefix p) {
		Set<Route> x = new HashSet<>();
		for(Route r : routes) {
			if(r.prefix().equals(p)) {
				x.add(r);
			}
		}
		
		return x;
	}

}
