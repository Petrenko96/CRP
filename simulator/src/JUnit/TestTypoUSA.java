package JUnit;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.Test;

import routing.Prefix;
import routing.Route;
import routing.Route.Priority;
import routing.RoutingTable2;
import solution.CRP;

class TestTypoUSA {
	
	private CRP usa;
	
	private static CRP init(int[] core) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File("typo1"));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			fail("File not found");
			System.exit(-2);
		}
		
		return new CRP(scan, core);
	}
	
	public TestTypoUSA() {
		int core[] = {5};
		usa = init(core);
	}

	@Test
	public void firstRouteOnR1() {
		Prefix p  = new Prefix("173.14.0.0", 16);
		usa.addRoute(1, 100, 1, p, Priority.MEDIUM);
				
		int test[] = {1, 2, 3, 5, 4, 6};
		int nontest[] = {7, 8, 9, 10, 11, 12, 13, 14};
		
		for(int i : test) {
			
			RoutingTable2 rt = usa.getRoutingTable(i);
			boolean firstTurn = true;
			for(Route route :  rt.get(p)) {
				if(firstTurn == false) {
					fail("Router " + i + " has more than 1 route for the prefix");
				}
				firstTurn = false;
				
				assertEquals(1, route.border_router());
				assertEquals(p, route.prefix());
				assertEquals(100, route.local_pref());
				assertEquals(Priority.MEDIUM, route.priority());
				HashSet<Integer> forward = route.forward();
				if(i == 1)
					assertEquals(true, forward.contains(-1) && forward.size() == 1);
				else if(i == 5)
					assertEquals(true, forward.contains(3) && forward.size() == 1);
				else if(i == 6)
					assertEquals(true, forward.contains(1) && forward.size() == 1);
				else if(i == 4)
					assertEquals(true, forward.contains(3) && forward.size() == 1);
				else
					assertEquals(true, forward.contains(i-1) && forward.size() == 1);
				
				if(i == 5)
					assertEquals(3, route.path_length());
				else if(i==4)
					assertEquals(3, route.path_length());
				else if(i==6)
					assertEquals(1, route.path_length());
				else
					assertEquals(i-1, route.path_length());
			}
		}
		
		for(int i : nontest) {
			RoutingTable2 rt = usa.getRoutingTable(i);
			if(rt.get(p).iterator().hasNext()) {
				fail("Router " + i + " has a route it shouldn't have");
			}
		}
		
	}
	
	@Test
	public void secondRouteOnR14() {
		Prefix p = new Prefix("173.14.0.0", 16);
		usa.addRoute(1, 100, 1, p, Priority.MEDIUM);
		usa.addRoute(14, 120, 1, p, Priority.MEDIUM);

		int newTest[] = {1, 2, 3, 5, 4, 14};
		int pathLength1[] = {0, 1, 2, 3, 4, 5};
		int pathLength14[] = {5, 4, 3, 2, 1, 0};
		int toRoute1[] = {-1, 1, 2, 3, 5, 4};
		int toRoute14[] = {2, 3, 5, 4, 14, -1};
		
		
		int otherTest[] = {7, 8, 9, 10, 11, 12, 13};
		
		for(int i = 0 ; i < newTest.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(newTest[i]);
			Set<Route> set = rt.getSet(p);
			
			Route compare1 = new Route(1, p, toRoute1[i], 100, 1, pathLength1[i], Priority.MEDIUM);
			Route compare14 = new Route(14, p, toRoute14[i], 120, 1, pathLength14[i], Priority.MEDIUM);
			
			if(set.size() != 2) {
				System.out.println(usa.getRoutingTable(newTest[i]));
				fail("Router " + newTest[i] + " should have two route to the prefix");
			}
			
			if(!set.contains(compare1))
				fail("Router " + newTest[i] + " does not contains route " + compare1);
			if(newTest[i] != 6 && !set.contains(compare14)) {
				fail("Router " + newTest[i] + " does not contains route " + compare14);
			}
		}
		
		for(int i = 0 ; i < otherTest.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(otherTest[i]);
			Set<Route> set = rt.getSet(p);
			assertEquals(0, set.size());
		}
	}
	
	@Test
	public void addUnRelatedRoute() {
		Prefix p = new Prefix("173.14.0.0", 16);
		Prefix p2 = new Prefix("124.17.16.1", 20);
		
		usa.addRoute(1, 100, 1, p, Priority.MEDIUM);
		usa.addRoute(14, 120, 1, p, Priority.MEDIUM);
		usa.addRoute(11, 100, 1, p2, Priority.MEDIUM);
	
		int newroute[] = {11, 10};
		int to[] = {-1, 11};
		
		for(int i = 0 ; i < newroute.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(newroute[i]);
			Set<Route> set1 = rt.getSet(p2);
			Set<Route> set2 = rt.getSet(p);
			
			if(!set2.isEmpty())
				fail("Router " + newroute[i] + " contains a route it shouldn't");
			
			if(set1.size() != 1)
				fail("Router " + newroute[i] + " contains more than a route for the new prefix p2");
			
			Route compare = new Route(11, p2, to[i], 100, 1, i, Priority.MEDIUM);
			if(!set1.contains(compare))
				fail("Router " + newroute[i] + " does not contain the new route to p2");
		}
		
		RoutingTable2 rt = usa.getRoutingTable(5);
		Set<Route> set1 = rt.getSet(p2);
		Set<Route> set2 = rt.getSet(p);
		
		assertEquals(1, set1.size());
		assertEquals(2, set2.size());
		
		Route compare = new Route(11, p2, 10, 100, 1, 2, Priority.MEDIUM);
		if(!set1.contains(compare)) {
			fail("Core router does not contain new route");
		}
	}
	
	@Test
	public void withBlankPrefix() {
		Prefix blank = new Prefix("0.0.0.0", 0);
		Prefix other = new Prefix("192.168.0.0", 16);
		
		usa.addRoute(1, 100, 1, blank, Priority.MEDIUM);
		usa.addRoute(11, 100, 1, other, Priority.MEDIUM);
		
		int tab[] = {1, 2, 3, 5, 10, 11};
		int tab2[] = {0, 4, 6, 7, 8, 9, 12, 13, 14};
		
		
		for(int i : tab) {
			
			RoutingTable2 rt = usa.getRoutingTable(i);
			Set<Route> setBlank = rt.getSet(blank);
			Set<Route> setOther = rt.getSet(other);
			
			assertEquals(1, setBlank.size());
			assertEquals(1, setOther.size());
			
		}
		
		for(int i : tab2) {
			RoutingTable2 rt = usa.getRoutingTable(i);
			Set<Route> setBlank = rt.getSet(blank);
			Set<Route> setOther = rt.getSet(other);
			
			if(!setBlank.isEmpty())
				fail("Router " + i + " has an entry for the blank prefix");
			if(!setOther.isEmpty()) 
				fail("Router " + i + " has an entry for the other prefix");
		}
		
		/*for(int i : tab3) {
			RoutingTable2 rt = usa.getRoutingTable(i);
			Set<Route> setBlank = rt.getSet(blank);
			Set<Route> setOther = rt.getSet(other);
			
			if(!setOther.isEmpty())
				fail("Router " + i + " has an entry for the other prefix");
			
			assertEquals(1, setBlank.size());
		}
		
		RoutingTable2 rt = usa.getRoutingTable(9);
		Set<Route> setBlank = rt.getSet(blank);
		Set<Route> setOther = rt.getSet(other);
		
		if(!setBlank.isEmpty())
			fail("Router " + 9 + " has an entry for the other prefix");
		
		assertEquals(1, setOther.size());*/
	}
	
	@Test
	public void testPriorityOneWay() {
		Prefix blank = new Prefix("0.0.0.0", 0);
		Prefix other = new Prefix("156.23.59.8", 16);
		
		usa.addRoute(1, 100, 1, blank, Priority.LOW);
		usa.addRoute(11, 100, 1, other, Priority.MEDIUM);
		
		int tabBlank[] = {1, 2, 3};
		int tabOther[] = {11, 10};
		
		int tabLasts[] = {4, 6, 7, 8, 9, 12, 13, 14};
		
		for(int i = 0 ; i < tabBlank.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabBlank[i]);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			if(blanks.size() != 1)
				fail("Router " + tabBlank[i] + " doens't have the blank route");
			if(others.size() != 0)
				fail("Router " + tabBlank[i] + " has the other route");
			
			int to = (i == 0) ? -1 : tabBlank[i-1];
			Route r = new Route(1, blank, to, 100, 1, i, Priority.LOW);
			
			if(!blanks.contains(r))
				fail("Router " + tabBlank[i] + " doens't have the blank route");
		}
		
		for(int i = 0 ; i < tabOther.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabOther[i]);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			if(blanks.size() != 0)
				fail("Router " + tabOther[i] + " has the blank route");
			if(others.size() != 1)
				fail("Router " + tabOther[i] + " doesn't have the other route");
			
			int to = (i == 0) ? -1 : tabOther[i-1];
			Route r = new Route(11, other, to, 100, 1, i, Priority.MEDIUM);
			
			if(!others.contains(r))
				fail("Router " + tabOther[i] + " doens't have the other route");
		}
		
		for(int i : tabLasts) {
			RoutingTable2 rt = usa.getRoutingTable(i);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			assertEquals(0, blanks.size());
			assertEquals(0, others.size());
			
		}
	}

	@Test
	public void testPriorityOtherWay() {
		Prefix blank = new Prefix("0.0.0.0", 0);
		Prefix other = new Prefix("156.23.59.8", 16);
		
		usa.addRoute(11, 100, 1, other, Priority.MEDIUM);
		usa.addRoute(1, 100, 1, blank, Priority.LOW);
		
		int tabBlank[] = {1, 2, 3};
		int tabOther[] = {11, 10};
		
		int tabLasts[] = {4, 6, 7, 8, 9, 12, 13, 14};
		
		for(int i = 0 ; i < tabBlank.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabBlank[i]);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			if(blanks.size() != 1)
				fail("Router " + tabBlank[i] + " doens't have the blank route");
			if(others.size() != 0)
				fail("Router " + tabBlank[i] + " has the other route");
			
			int to = (i == 0) ? -1 : tabBlank[i-1];
			Route r = new Route(1, blank, to, 100, 1, i, Priority.LOW);
			
			if(!blanks.contains(r))
				fail("Router " + tabBlank[i] + " doens't have the blank route");
		}
		
		for(int i = 0 ; i < tabOther.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabOther[i]);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			if(blanks.size() != 0)
				fail("Router " + tabOther[i] + " has the blank route");
			if(others.size() != 1)
				fail("Router " + tabOther[i] + " doesn't have the other route");
			
			int to = (i == 0) ? -1 : tabOther[i-1];
			Route r = new Route(11, other, to, 100, 1, i, Priority.MEDIUM);
			
			if(!others.contains(r))
				fail("Router " + tabOther[i] + " doens't have the other route");
		}
		
		for(int i : tabLasts) {
			RoutingTable2 rt = usa.getRoutingTable(i);
			Set<Route> blanks = rt.getSet(blank);
			Set<Route> others = rt.getSet(other);
			
			assertEquals(0, blanks.size());
			assertEquals(0, others.size());
			
		}
	}
	
	@Test
	public void test2CoreGroup() {
		int core[] = {5,7};
		usa = init(core);
		
		int groups[][] = {{5,7}, {3, 4, 6, 8, 9, 10}, {1, 2, 11, 13, 14}, {12}};
		
		for(int i = 0 ;  i < groups.length ; i++) {
			for(int j = 0 ; j < groups[i].length ; j++) {
				int r = groups[i][j];
				int group = usa.group(r);
				
				if(group != i) {
					fail("Router " + r + " has the group " + group + " and should have " + i);
				}
			}
		}
	}
	
	@Test
	public void test2CoreFirstRouteOnR1() {
		int core[] = {5, 7};
		usa = init(core);
		
		Prefix p = new Prefix("1.1.0.0", 16);
		usa.addRoute(1, 100, 1, p, Priority.MEDIUM);
		
		
		int tabRoute[] = {1, 5, 6, 7};
		int tablength[] = {0, 5, 1, 2};
		int tabNextHop[] = {-1, 7, 1, 6};
		
		for(int i = 0 ; i < tabRoute.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabRoute[i]);
			Set<Route> routes = rt.getSet(p);
			if(routes.size() != 1)
				fail("Router " + tabRoute[i] + " does not have exactly one route for the prefix " + p);
			
			Route r = new Route(1, p, tabNextHop[i], 100, 1, tablength[i], Priority.MEDIUM);
			if(!routes.contains(r)) {
				fail("Router "+tabRoute[i]+" should contain route "+r+" (contains "+routes.toArray()[0]+")");
			}
		}
	}
	
	@Test
	public void test2CoreSecondRouteOnR14() {
		int core[] = {5,7};
		usa = init(core);
		
		Prefix p = new Prefix("1.1.0.0", 16);
		usa.addRoute(1, 120, 1, p, Priority.MEDIUM);
		usa.addRoute(14, 100, 1, p, Priority.MEDIUM);
		
		int tabHas[] = {1, 4, 5, 6, 7, 14};
		int pathLength1[] = {0, 6, 5, 1, 2, 7};
		int pathLength14[] = {7, 1, 2, 6, 5, 0};
		int to1[] = {-1, 5, 7, 1, 6, 4};
		int to14[] = {6, 14, 4, 7, 5, -1};
		
		for(int i = 0 ; i < tabHas.length ; i++) {
			RoutingTable2 rt = usa.getRoutingTable(tabHas[i]);
			Set<Route> set = rt.getSet(p);
			
			if(set.size() != 2)
				fail("Router "+tabHas[i]+" should have two routes to the given prefix");
			
			Route r1 = new Route(1, p, to1[i], 120, 1, pathLength1[i], Priority.MEDIUM);
			Route r14 = new Route(14, p, to14[i], 100, 1, pathLength14[i], Priority.MEDIUM);
			
			if(!set.contains(r1)) {
				fail("Router "+tabHas[i]+" should contain route "+r1);
			}
			if(!set.contains(r14)) {
				fail("Router "+tabHas[i]+" should contain route "+r14);
			}
		}
	}
}
	