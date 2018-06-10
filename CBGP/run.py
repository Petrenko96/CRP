#!/bin/python
from __future__ import print_function
import sys

from generate_cbgp_commands import generate_cbgp_commands
from CBGP import run_cbgp
from format_output import format_output

if len(sys.argv) < 4:
    print("Not enough arguments (3 required)")
    print("./run.py <PATH> <FM_RR> <lp_AS>")
    print("\t<PATH> : name of the folder containing the topology")
    print("\t<FM_RR> : 0 to use a full-mesh, 1 to use route reflection")
    print("\t<lp_AS> : 0 to vary local-pref attribute, 1 to vary AS-path attribute")
    exit() 

PATH = sys.argv[1]
FM_RR = int(sys.argv[2]) # To use a Full-Mesh : 0, route reflection : 1
lp_AS = int(sys.argv[3]) # To use local-pref : 0, AS : 1

if FM_RR:
    for line in open(PATH+"/route_reflectors", "r"):
        print("Handling route_reflectors "+line,end='')
        RR_list = set()
        for rr in line.split("\n")[0].split(" "):    
            RR_list.add(int(rr))
    
        print("... generating commands for C-BGP : ",end='')
        generate_cbgp_commands(PATH, FM_RR, lp_AS, RR_list=RR_list)
        print(" done")
        print("... running C-BGP : ",end='')
        run_cbgp(PATH)
        print(" done")
        print("... formatting output : ",end='')
        format_output(PATH, FM_RR=FM_RR, RR_list=RR_list)
        print(" done")
else:
    print("Handling route_reflectors "+line,end='')
    print("... generating commands for C-BGP : ",end='')
    generate_cbgp_commands(PATH, FM_RR, lp_AS)
    print(" done")
    print("... running C-BGP : ",end='')
    run_cbgp(PATH)
    print(" done")
    print("... formatting output : ",end='')
    format_output(PATH, FM_RR=FM_RR)
    print(" done")
