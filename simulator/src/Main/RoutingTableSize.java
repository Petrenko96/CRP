package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import routing.Prefix;
import routing.Route.Priority;
import solution.CRP;

public class RoutingTableSize {

	public static void main(String[] args) {
		
		if(args.length < 4) {
			System.err.println("The number of arguments must be 4");
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
		
		int nbr_prefixes = 0;
		HashMap<Integer, ArrayList<String>> prefixes = conf.getPrefixes();
		Integer br[] = conf.getBorderRouters();
		
		for(Integer i : prefixes.keySet()) {
			nbr_prefixes += prefixes.get(i).size();
		}
		
		System.out.println("Nbr routes : " + nbr_prefixes*br.length);
		
		for(int i = 0 ; i < usa.V() ; i++) {
			System.out.println(i + " (Group " + usa.group(i) +") : " + usa.getRoutingTable(i).size());
		}

	}

}
