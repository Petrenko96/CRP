#!/bin/python

import os
import sys

if(len(sys.argv) < 3):
	print("Usage : command [AS number] [cr1 cr2 ...]")
	exit()

AS = sys.argv[1]

_try_paths = ["bin/", "../bin/", "simulator/bin/"]
bin_path = None
for p in _try_paths:
	if os.path.isdir(p):
		bin_path = p

if bin_path == None:
	print("AS not found")
	exit()


conf_path = bin_path+"../../"+AS+"/"
topo = conf_path+"topo"
prefixes = conf_path+"prefixes"
aspath = conf_path+"aspath"

command = "java -cp " + bin_path + " Main.GetPaths " + topo + " " + prefixes + " " + aspath

filename = "stretch"
for cr in sys.argv[2:]:
	command += " " + cr
	filename += "-"+cr

results = open(filename+".crp", "w");

out = os.popen(command).read()
lines = out.split("\n")[:-1]

def write_path(file, path):
	path = path.split(" -> ")
	for i in range(1, len(path)-2):
		file.write(path[i]+" ")
	file.write(path[len(path)-2]+"\n")

for line in lines:
	info = line.split("\\")
	router = info[0]
	ip = info[1]
	path = info[2]
	results.write(router+"\t"+ip+"\t")
	write_path(results, path)

results.close()
		

