#!/bin/python
from __future__ import print_function
import sys

def format_output(path, **kwargs):
    FM_RR = kwargs.get('FM_RR', None)
    PATH = path
    srcfile = open(PATH+"/cbgp_intermediate/output_cbgp","r")
    
    if FM_RR == 0:
        dstfile_stretch = open(PATH+"/paths/fullmesh/stretch-fullmesh.cbgp","w")
        dstfile_rib = open(PATH+"/rt_sizes/fullmesh/rt_sizes-fullmesh.cbgp","w")
    elif FM_RR == 1:
        RR_list = sorted(kwargs.get('RR_list', None))
        name_stretch = "stretch-rr"
        name_rib = "rt_sizes"

        for rr in RR_list:
            name_stretch += "-"+str(rr)
            name_rib += "-"+str(rr)
        name_stretch += ".cbgp"
        name_rib += ".cbgp"
        
        dstfile_stretch = open(PATH+"/paths/rr/"+name_stretch,"w")
        dstfile_rib = open(PATH+"/rt_sizes/rr/"+name_rib,"w")
    else:
        dstfile_stretch = open(PATH+"/paths/output_stretch","w")
        dstfile_rib = open(PATH+"/rt_sizes/output_rib","w")
        
    routing_table = False
    count = 0
    global_count = 0
    nbr_router = 0

    router_id = None
    for line in srcfile:
        splitted = line.split(" ")
        if line == 'COUNT\n':
            routing_table = True
        
        # Traceroute
        elif not routing_table:
            # Headline
            if splitted[0] != "":
                router = splitted[0].split(".")[3]
                dstfile_stretch.write(router+"\t")
                dstfile_stretch.write(splitted[1][:-1]+"\t"+router)

            # Entry
            elif splitted[4] == "error":
                dstfile_stretch.write(" " + splitted[2].split(".")[3])

            # End of traceroute
            elif splitted[4] == "reply\n":
                dstfile_stretch.write("\n")
        
        # Routing table
        else:
            tabsplit = line.split("\t")
            if splitted[0] == "RIB":
                if router_id is not None:
                    dstfile_rib.write(str(router_id.split("\n")[0]) + " " + str(count)+"\n")
                    count = 0
                router_id = splitted[1]
                nbr_router += 1
            else:
                count += 1
                global_count += 1
                
    dstfile_rib.write(str(router_id.split("\n")[0]) + " " + str(count)+"\n")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Not enough arguments (1 required)")
        print("./format_output.py <PATH>")    
        print("\t<PATH> : name of the folder containing the topology")
        exit()
    path = sys.argv[1]
    print("... formatting output : ",end='')
    format_output(path)
    print(" done")
