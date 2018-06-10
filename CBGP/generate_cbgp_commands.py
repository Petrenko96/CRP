#!/bin/python
from __future__ import print_function
import sys, random, os

def generate_cbgp_commands(path, FM_RR, lp_AS, **kwargs):
    topo = open(path+"/topo", "r")
    prefixes = open(path+"/prefixes", "r")
    as_path_info = open(path+"/aspath", "r")
    if not os.path.exists(path+"/cbgp_intermediate/"):
        os.makedirs(path+"/cbgp_intermediate/")
    dstfile = open(path+"/cbgp_intermediate/commands_cbgp", "w")
    RR_list = kwargs.get('RR_list', None)
    
    number = topo.readline() # read the number of routers in the network

    # Create a hashtable to assign preferences (set as AS-path length or local-preference)
    hashtable = {}
    for line in as_path_info:
        split=line.split(" ")
        r1 = int(split[0])
        r2 = int(split[1])
        aspath = int(split[2])
        hashtable[(r1,r2)] = aspath
        hashtable[(r2,r1)] = aspath

    # Gather border routers ID
    border_routers = set()
    for line in prefixes:
        split = line.split("\t")[0]
        border_routers.add(split)
    prefixes.seek(0)    

    # Generate an AS path for the BGP route
    def generate_as_path(r_id, prefix, length):
        path = str(r_id)
        while length-2 > 0:
            path += " "+str(random.randint(1,3000)+r_id)
            length = length-1    
        # Compute final AS number, should be the same no matter where the prefix is advertised
        path += " " + str(int(prefix.split("/")[0].split(".")[0]) + int(prefix.split("/")[0].split(".")[1]) + int(prefix.split("/")[1]))
        return path
    
    # Collect route reflectors ID if used    
    if FM_RR:
        for elem in RR_list:
            if int(elem) >= int(number):
                print(elem)
                RR_list.remove(elem)
            
    dstfile.write("net add domain 1 igp\n")

    # Create nodes
    for n in range(0,int(number)):
        dstfile.write("net add node 1.0.0."+str(n)+"\n")
        dstfile.write("net node 1.0.0."+str(n)+" domain 1\n")

    # Create links
    for line in topo:
        x = (line.split(' ')[0])
        y = (line.split(' ')[1].replace('\n', ''))
        dstfile.write("net add link 1.0.0."+x+" 1.0.0."+y+"\n")
        dstfile.write("net link 1.0.0."+x+" 1.0.0."+y+" igp-weight --bidir 1\n")
       
    # Compute IGP 
    dstfile.write("net domain 1 compute\n")


    # Add BGP routers
    for n in range(0,int(number)):
        dstfile.write("bgp add router 1 1.0.0."+str(n)+"\n")

    # Enable Full-Mesh if required
    if not FM_RR:
        dstfile.write("bgp domain 1 full-mesh\n")

    # Enable Route Reflection otherwise    
    else:
        # Create BGP sessions
        for n in range(0,int(number)):
            if n not in RR_list:
                for RR in RR_list:
                    dstfile.write("bgp router 1.0.0."+str(RR)+" add peer 1 1.0.0."+str(n)+"\n")
                    dstfile.write("bgp router 1.0.0."+str(RR)+" peer 1.0.0."+str(n)+" up\n")
                    dstfile.write("bgp router 1.0.0."+str(n)+" add peer 1 1.0.0."+str(RR)+"\n")
                    dstfile.write("bgp router 1.0.0."+str(n)+" peer 1.0.0."+str(RR)+" up\n")
                        
        # Define BGP sessions as RR
        for n in range(0,int(number)):
            if n not in RR_list:
                for RR in RR_list:
                    dstfile.write("bgp router 1.0.0."+str(RR)+" peer 1.0.0."+str(n)+" rr-client\n")
        
        # Create BGP sessions between RR
        RR_list_copy = RR_list.copy()
        for RR in RR_list:
            for RR_c in RR_list_copy:
                if RR_c is not RR:
                    dstfile.write("bgp router 1.0.0."+str(RR)+" add peer 1 1.0.0."+str(RR_c)+"\n")
                    dstfile.write("bgp router 1.0.0."+str(RR)+" peer 1.0.0."+str(RR_c)+" up\n")
                    dstfile.write("bgp router 1.0.0."+str(RR_c)+" add peer 1 1.0.0."+str(RR)+"\n")
                    dstfile.write("bgp router 1.0.0."+str(RR_c)+" peer 1.0.0."+str(RR)+" up\n")
            RR_list_copy.remove(RR)
                        
    # Create virtual peer used to advertise prefixes
    for br in border_routers:
        dstfile.write("net add domain 1"+str(br)+" igp\n")
        dstfile.write("net add node 255."+str(br)+".0.1\n")
        dstfile.write("net add link 1.0.0."+str(br)+" 255."+str(br)+".0.1\n")
        dstfile.write("net link 1.0.0."+str(br)+" 255."+str(br)+".0.1 igp-weight --bidir 5\n")
        dstfile.write("net node 1.0.0."+str(br)+" route add --oif=255."+str(br)+".0.1 255."+str(br)+".0.1/32 1\n")
        dstfile.write("net domain 1"+str(br)+" compute\n")
        dstfile.write("bgp router 1.0.0."+str(br)+" add peer "+str(br)+" 255."+str(br)+".0.1\n")
        dstfile.write("bgp router 1.0.0."+str(br)+" peer 255."+str(br)+".0.1 virtual\n")
        dstfile.write("bgp router 1.0.0."+str(br)+" peer 255."+str(br)+".0.1 next-hop-self\n")
        dstfile.write("bgp router 1.0.0."+str(br)+" peer 255."+str(br)+".0.1 up\n")

    # Advertise prefixes according to the AS length or local-pref given
    if lp_AS:
        count = 0
        # Generate AS path
        for br in border_routers:
            for line in prefixes:
                br_prefix = line.split("\t")
                value = hashtable[int(br),int(br_prefix[0])]
                prefix = br_prefix[1].split("\n")[0]
                if value >= 1:
                    dstfile.write('bgp router 1.0.0.'+str(br)+' peer 255.'+str(br)+'.0.1 recv "BGP4|0|A|1.0.0.'+str(br)+'|1|'+prefix+'|'+generate_as_path(int(br), prefix, int(value)+1)+'|IGP|255.'+str(br)+'.0.1|0|0"\n')
            prefixes.seek(0)
    
    else:
        # Generation local-pref
        for br in border_routers:
            for length, set_routes in zip(path_length[str(br)],prefixes):
                for prefix in set_routes:
                    dstfile.write('bgp router 1.0.0.'+str(br)+' peer 255.'+str(br)+'.0.1 recv "BGP4|0|A|1.0.0.'+str(br)+'|1|'+prefix+'|'+str(br)+'|IGP|255.'+str(br)+'.0.1|'+str(length)+'|0"\n')
                    
    dstfile.write("sim run\n")

    # Following commands ask C-BGP to simulate a traceroute on towards every prefixe for each node
    for line in prefixes:
        prefix = line.split("\t")[1].split("\n")[0]
        for n in range(0, int(number)):
            dstfile.write('print "1.0.0.'+str(n) + " " + prefix +'\\n"\n')
            dstfile.write('net node 1.0.0.'+str(n)+' traceroute '+ prefix.split("/")[0] +'\n')

    # Following commands ask C-BGP to print content of RIBs
    dstfile.write('print "COUNT\\n"\n')
    for n in range(0,int(number)):
        dstfile.write('print "RIB '+str(n)+'\\n"\n')
        dstfile.write('bgp router 1.0.0.'+str(n)+' show adj-rib in * *\n')
        
if __name__ == "__main__":

    if len(sys.argv) < 4:
        print("Not enough arguments (3 required)")
        print("./run.py <PATH> <FM_RR> <lp_AS>")
        print("\t<PATH> : name of the folder containing the topology")
        print("\t<FM_RR> : 0 to use a full-mesh, 1 to use route reflection")
        print("\t<lp_AS> : 0 to vary local-pref attribute, 1 to vary AS-path attribute")
        exit()
    
    path = sys.argv[1]
    FM_RR = int(sys.argv[2]) # Full-Mesh:0, RouteReflection:1
    lp_AS = int(sys.argv[3]) # Local-pref:0, AS:1
    if FM_RR:
        for line in open(path+"/route_reflectors", "r"):
            print("Handling route_reflectors "+line,end='')
            RR_list = set()
            for rr in line.split("\n")[0].split(" "):    
                RR_list.add(int(rr))
            
            print("... generating commands for C-BGP : ",end='')
            generate_cbgp_commands(path, FM_RR, lp_AS, RR_list=RR_list)
            print(" done")
        
    else:
        print("Handling full mesh configuration")
        print("... generating commands for C-BGP : ",end='')
        generate_cbgp_commands(path, FM_RR, lp_AS)
        print(" done")
        
