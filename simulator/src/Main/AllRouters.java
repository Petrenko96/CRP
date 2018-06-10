package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Flowable.Flowable.Flow;
import routing.Address;
import solution.CRP;
import topology.ShortestPath;

public class AllRouters {
	
	public static void main(String args[]) {
		
		if(args.length < 4) {
			System.err.println("The number of arguments must be at least 4");
			System.err.println("Usage : command [topo_file] [prefix_file] [aspath_file] [cr1 cr2 ...]");
			return;
		}
		
		String topo_file = args[0];
		String prefix_file = args[1];
		String aspath_file = args[2];
		
		ArrayList<Integer> cr = new ArrayList<>();
		for(int i = 3 ; i < args.length ; i++) {
			cr.add(Integer.valueOf(args[i]));
		}
		
		Config conf = new Config(prefix_file, aspath_file);
		int root[] = cr.stream().mapToInt(i->i).toArray();
		CRP usa = conf.newCRP(topo_file, root);
		
		HashMap<Integer, ArrayList<String>> prefixes = conf.getPrefixes();
		
		for(int i = 0 ; i < usa.V() ; i++) {
			for(Map.Entry<Integer, ArrayList<String>> entry : prefixes.entrySet()) {
				for(String prefix : entry.getValue()) {
					String split[] = prefix.split("/"); 
					String pref = split[0];
					Flow flow = usa.flow(new Address(pref), i);
					System.out.println(i + "\\" + prefix + "\\" + flow);
				}
			}
		}
	}
}
