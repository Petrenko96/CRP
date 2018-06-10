package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import topology.ShortestPath;
import topology.Topology;

public class TestDijkstra extends Topology{

	public TestDijkstra(Scanner sc) {
		super(sc);
	}

	public static void main(String args[]) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File("typo1"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		TestDijkstra d = new TestDijkstra(scan);
		
		for(int i = 0 ; i < d.V() ; i++) {
			ShortestPath sp = new ShortestPath(d, i);
			System.out.println(i+" : "+sp.pathLength(5));
		}
	}
	
}
