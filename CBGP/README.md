# Using C-BGP #
The scripts provided are meant to ease the use of C-BGP (http://c-bgp.sourceforge.net/), a solver for BGP. Therefore, they required C-BGP to be installed before executing them. Also, the path given at execution needs to correspond to a folder respecting the structure described in the README of parent folder.

## Files ##
- generate\_cbgp\_commands.py:  
translates a topology and prefixes into C-BGP commands  
output file : <PATH>/cbgp\_intermediate/command\_cbgp  
- CBGP.py:  
executes C-BGP solver and displays the RIB of every router as well as a simulated traceroute for every node toward every prefix and RIB of every  
output file : <PATH>/cbgp\_intermediate/output\_cbgp  
- format\_output.py:  
reads the output of CBGP.py and translates  
output files : depending on the kind of configuration used (full-mesh, route-reflectors, or not given), files are put in folders : <PATH>/paths/; <PATH>/rt_sizes/  
- run.py :  
executes the three previous programs

## How to use ##
The easiest way to run these scripts is to use "run.py".
> python run.py <PATH> <FM_RR> <ASP_LP>

	<PATH> : name of the file containing the topology
	<FM_RR> : 0 to use a full-mesh, 1 to use route reflection
	<ASP_LP> : 0 to vary local-pref attribute, 1 to vary AS-path attribute

However it is possible to run independently every part. Syntex is provided 

> ./run.py <PATH> <FM_RR> <ASP_LP>

    <PATH> : name of the folder containing the topology
    <FM_RR> : 0 to use a full-mesh, 1 to use route reflection
    <ASP_LP> : 0 to vary local-pref attribute, 1 to vary AS-path attribute

> ./CBGP.py <PATH>

    <PATH> : name of the folder containing the topology

> ./format_output.py <PATH>

    <PATH> : name of the folder containing the topology

