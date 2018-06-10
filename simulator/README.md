# Simulator use #

## Classes

- Main.GetPaths :  
Print for each router and each prefix, the path followed by a packet originated by this router towards an address of this prefix
- Main.RoutingTableSize :  
Print the size of the routing table of each router

## Usage

To run the first class:
> java Main.GetPaths <TOPO\_FILE> <PREFIX\_FILE> <ASPATH\_FILE>

	<TOPO\_FILE> : the file containing the topology
	<PREFIX\_FILE> : the file containing the prefixes to announce
	<ASPATH\_FILE> : the file containing the AS path lengths for the prefixes

> java Main.RoutingTableSize <TOPO\_FILE> <PREFIX\_FILE> <ASPATH\_FILE>

	<TOPO\_FILE> : the file containing the topology
	<PREFIX\_FILE> : the file containing the prefixes to announce
	<ASPATH\_FILE> : the file containing the AS path lengths for the prefixes

## Tests

We deceloped a few unitary tests to ensure the good working of the simulator. To launch these tests, one can do:

> python test/launch.py

or simply

> launch.py
