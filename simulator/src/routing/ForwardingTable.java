package routing;
import java.util.ArrayList;

public class ForwardingTable {
	
	ArrayList<ForwardingPath> routes;
	
	public ForwardingTable() {
		this.routes = new ArrayList<>();
	}

	public void add(ForwardingPath r) {
		this.routes.add(r);
	}
	
	public boolean contains(ForwardingPath p) {
		for(ForwardingPath fp : routes) {
			if(fp.equals(p)) 
				return true;
		}
		
		return false;
	}
	
	public ForwardingPath get(Prefix p) {
		for(ForwardingPath i : routes) {
			if(i.prefix().equals(p)) {
				return i;
			}
		}
		return null;
	}
	
	public boolean contains(Route r, boolean in) {
		if(in) {
			for(Integer i : r.forward()) {
				ForwardingPath fp2 = new ForwardingPath(r.prefix(), i, in);
				if(contains(fp2))
					return true;
			}
		} else {
			ForwardingPath fp2 = new ForwardingPath(r.prefix(), r.border_router(), in);
			if(contains(fp2))
				return true;
		}
		
		return false;
	}
	
	public String toString() {
		StringBuilder bld = new StringBuilder();
		
		if(routes.size() == 0)
			return "Empty Forwarding Table\n";
		
		String header = "+--------------------+----------+--------+\n";
		bld.append(header);
		bld.append("|       Prefix       | Next Hop | In/Out |\n");

		ForwardingPath previous = null;
		for(ForwardingPath r : routes) {
			if(previous == null || !r.prefix().equals(previous.prefix())) {
				bld.append(header);
				bld.append(String.format("| %18s |", r.prefix()));
			} else {
				bld.append("|                    |");
			}
			previous = r;
			bld.append(String.format(" %8d | %6s |\n", r.nextHop(), (r.in()) ? "In" : "Out"));
		}
		
		bld.append(header);
		return bld.toString();
	}
}
