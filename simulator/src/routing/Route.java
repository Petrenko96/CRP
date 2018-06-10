package routing;

import java.util.HashSet;

public class Route implements Comparable<Route> {

	public static enum Priority {
		LOW, MEDIUM, HIGH
	};
	
	private int border_router;
	private Priority priority;
	private int local_pref;
	private int path_length;
	private HashSet<Integer> to;
	private Prefix prefix;
	private int as_path_length;
	
	public Route(int border_router, Prefix prefix, int to, int local_pref, int as_path_length, int path_length, Priority priority) {
		this.border_router = border_router;
		this.local_pref = local_pref;
		this.path_length = path_length;
		this.priority = priority;
		this.to = new HashSet<>();
		this.to.add(to);
		this.prefix = prefix;
		this.as_path_length = as_path_length;
	}
	
	public int border_router() {
		return border_router;
	}
	
	public Prefix prefix() {
		return prefix;
	}
	
	public Priority priority() {
		return priority;
	}
	
	public int local_pref() {
		return local_pref;
	}
	
	public int path_length() {
		return path_length;
	}
	
	public int as_path_length() {
		return as_path_length;
	}
	
	public HashSet<Integer> forward() {
		return to;
	}
	
	public void addForward(int i) {
		to.add(i);
	}
	
	public String toString() {
		return "Prefix:"+prefix+" BR:"+border_router+" To:"+to+" LP:"+local_pref+" PL:"+path_length+" P:"+priority;
	}

	@Override
	public int compareTo(Route o) {
		int x = this.local_pref-o.local_pref;
		if(x == 0) {
			int y = o.as_path_length-this.as_path_length;
			if(y == 0) {
				return o.border_router - border_router;
			} else {
				return y;
			}
		} else {
			return x;
		}
	}
	
	
	// ====================== FOR TEST PURPOSE ONLY ===================
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Route) {
			Route r = (Route) obj;
			return r.border_router == border_router && r.local_pref == local_pref && r.path_length == path_length && r.prefix.equals(prefix)
					&& r.priority == priority && r.as_path_length == as_path_length;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return prefix.hashCode();
	}
	
}
