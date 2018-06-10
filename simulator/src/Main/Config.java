package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import routing.Prefix;
import routing.Route.Priority;
import solution.CRP;

public class Config {
	
	private static class Tuple {
		int v;
		int w;
		
		public Tuple(int v, int w) {
			this.v = v;
			this.w = w;
		}
		
		@Override
		public int hashCode() {
			int result = 1;
			result = 31*result + v;
			result = 31*result + w;
			
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Tuple) {
				Tuple o = (Tuple) obj;
				return o.v == v && o.w == w;
			}
			return super.equals(obj);
		}
		
		public String toString() {
			return "("+v+", "+w+")";
		}
	}
	
	private HashMap<Integer, ArrayList<String>> prefixes;
	private ArrayList<Integer> borderRouters;
	private HashMap<Tuple, Integer> aspath;

	public Config(String file_prefixes, String file_aspath) {
		this();
		build_prefixes(file_prefixes);
		build_aspath(file_aspath);
	}
	
	public Config() {
		prefixes = new HashMap<>();
		aspath = new HashMap<>();
		borderRouters = new ArrayList<>();
	}
	
	private void build_aspath(String filename) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filename));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		while(scan.hasNextInt()) {
			int v = scan.nextInt();
			int w = scan.nextInt();
			int value = scan.nextInt();
			
			aspath.put(new Tuple(v, w), value);
			aspath.put(new Tuple(w, v), value);
		}
	}
	
	private void build_prefixes(String filename) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filename));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		while(scan.hasNextInt()) {
			int i = scan.nextInt();
			if(prefixes.get(i) == null) {
				prefixes.put(i, new ArrayList<>());
				borderRouters.add(i);
			}
			
			prefixes.get(i).add(scan.next());
		}
		
		scan.close();
	}
	
	public HashMap<Integer, ArrayList<String>> getPrefixes() {
		return prefixes;
	}
	
	public Integer[] getBorderRouters() {
		return borderRouters.toArray(new Integer[1]);
	}
	
	public CRP newCRP(String filename, int root[], boolean onlyBR) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		CRP crp = new CRP(scan, root);
		
		if(!onlyBR) {
			for(int i : borderRouters) {
				for(int j : borderRouters) {
					Tuple t = new Tuple(i, j);
					if(aspath.get(t) > 0) {
						for(String pref : prefixes.get(j)) {
							crp.addRoute(i, 100, aspath.get(new Tuple(i, j)), new Prefix(pref), Priority.MEDIUM);
						}
					}
				}
			}
		} else {
			for(int j : borderRouters) {
				for(String pref : prefixes.get(j)) {
					crp.addRoute(j, 100, aspath.get(new Tuple(j, j)), new Prefix(pref), Priority.MEDIUM);
				}
			}
		}
		
		return crp;
	}
	
	public CRP newCRP(String filename, int root[]) {
		return newCRP(filename, root, false);
	}
	
}
