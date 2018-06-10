# Simulator use #

## Classes

- Main.GetPaths :  
Print for each router and each prefix, the path followed by a packet originated by this router towards an address of this prefix
- Main.RoutingTableSize :  
Print the size of the routing table of each router

## Usage

To run the first class:
> java Main.GetPaths <TOPO\_FILE> <PREFIX\_FILE> <ASPATH\_FILE>

	<TOPO_FILE> : the file containing the topology
	<PREFIX_FILE> : the file containing the prefixes to announce
	<ASPATH_FILE> : the file containing the AS path lengths for the prefixes

> java Main.RoutingTableSize <TOPO\_FILE> <PREFIX\_FILE> <ASPATH\_FILE>

	<TOPO_FILE> : the file containing the topology
	<PREFIX_FILE> : the file containing the prefixes to announce
	<ASPATH_FILE> : the file containing the AS path lengths for the prefixes

## Tests

We developed a few unitary tests to ensure the good working of the simulator. To launch these tests, one can do:

> python test/launch.py

or simply

> launch.py

We also made tests on the big topologies provided, to ensure that the traffic exits by a correct exit point. To launch these tests:

> python test/test\_exit.py <AS\_NUMBER> <CORE_ROUTER1> <CORE_ROUTER2> <...>

	<AS_NUMBER> : the number of the AS : 209, 5511, 7911 or 1668
	<CORE_ROUTERX> : the id of the core router
