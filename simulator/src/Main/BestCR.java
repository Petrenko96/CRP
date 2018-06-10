package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Flowable.Flowable.Flow;
import routing.Address;
import routing.Prefix;
import routing.Route.Priority;
import solution.CRP;

public class BestCR {
	
	public static void main(String args[]) {
		Scanner s1;
		try {
			s1 = new Scanner(new File("AS209"));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		if(args.length < 2) {
			System.err.println("Should provide at least two arguments - the number of routers and the core router");
			s1.close();
			return;
		}
		
		ArrayList<Integer> temp = new ArrayList<>();
		for(int i = 1 ; i < args.length ; i++) {
			temp.add(Integer.valueOf(args[i]));
		}
		
		int root[] = temp.stream().mapToInt(i->i).toArray();
		
		CRP usa = new CRP(s1, root);
		
		// Add prefixes to Salt Lake City
		String utah[] = {"161.119.0.0/16", "161.119.0.0/23", "161.119.3.0/24", "161.119.4.0/22", "161.119.8.0/21", "161.119.16.0/20", "161.119.32.0/19",
				"161.119.64.0/18", "161.119.128.0/17", "165.239.0.0/16", "168.177.0.0/16", "168.178.0.0/16", "168.179.0.0/16", "168.180.0.0/16", 
				"204.113.0.0/22", "204.113.3.0/24", "204.113.6.0/23", "204.113.8.0/21", "204.113.16.0/20"};	
		
		String newYork[] = {"128.228.0.0/16", "134.74.0.0/16", "146.95.0.0/16", "146.96.0.0/16", "146.111.0.0/16", "146.245.0.0/16", "146.245.0.0/17", 
				"146.245.128.0/17", "148.84.0.0/16", "149.4.0.0/16", "150.210.0.0/16", "163.238.0.0/16", 
				"198.61.16.0/20", "198.180.141.0/24", "207.159.192.0/18"};
		
		String losAngeles[] = {"161.149.0.0/16", "161.149.5.0/24", "161.149.11.0/24", "161.149.12.0/24", "161.149.14.0/24", "161.149.40.0/24", 
				"161.149.49.0/24", "161.149.50.0/24", "161.149.57.0/24", "161.149.63.0/24", "161.149.80.0/24", "161.149.95.0/24", 
				"161.149.97.0/24", "161.149.102.0/24", "161.149.108.0/24", "161.149.196.0/24", "161.149.204.0/24", "161.149.213.0/24",
				"161.149.221.0/24", "161.149.223.0/24", "161.149.240.0/24", "161.149.251.0/24"};
		
		String miami[] = {"134.53.0.0/16", "134.53.0.0/18", "134.53.64.0/19", "134.53.96.0/19", "134.53.119.0/24", "134.53.128.0/19", 
				"134.53.160.0/19", "134.53.192.0/18", "208.115.160.0/19", "208.115.160.0/24"};

		String seattle[] = {"72.233.128.0/18", "72.233.128.0/20", "72.233.142.0/24", "72.233.143.0/24", "72.233.144.0/22", "72.233.148.0/22", 
				"72.233.152.0/22", "72.233.156.0/22", "72.233.160.0/19", "72.233.160.0/20", "72.233.176.0/20"};
		
		int borderRouters[] = {16, 21, 23, 47, 14};
		String prefixes[][] = {utah, newYork, losAngeles, miami, seattle};
		int ASPathLength[][] = {{1, 2, 3, 2, 2},
								{2, 1, 3, 2, 3},
								{3, 3, 1, 3, 3},
								{2, 2, 3, 1, 3},
								{2, 3, 3, 3, 1}};
		
		for(int i = 0 ; i < borderRouters.length ; i++) {
			for(int j = 0 ; j < prefixes.length ; j++) {
				for(int k = 0 ; k < prefixes[j].length ; k++) {
					usa.addRoute(borderRouters[i], 200, ASPathLength[i][j], new Prefix(prefixes[j][k]), Priority.MEDIUM);
				}
			}
		}
		
		int routers = Integer.valueOf(args[0]);
		for(int i = 0 ; i < routers ; i++) {
			for(int j = 0 ; j < prefixes.length ; j++) {
				for(int k = 0 ; k < prefixes[j].length ; k++) {
					String pref = prefixes[j][k].split("/")[0];
					Flow flow = usa.flow(new Address(pref), i);
					System.out.println(i	+"\\"+prefixes[j][k]+"\\"+flow);
				}
			}
		}
	}
}
