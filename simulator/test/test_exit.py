#!/bin/python

import os
import sys

if(len(sys.argv) < 3):
	print("Usage : command [AS number] [cr1 cr2-...]")
	exit()

AS = sys.argv[1]
ref_gen = " ".join(sys.argv[2:])
ref_check = "-".join(sys.argv[2:])

__paths = ["./", "test/", "simulator/test/"]
conf_path = None
for path in __paths:
	if(os.path.exists(path+"generate_paths.py")):
		conf_path = path

if conf_path is None:
	print("No test found")
	exit()

command1 = "python "+conf_path+"generate_paths.py " + AS + " " + ref_gen

os.system(command1)

command2 = "python "+conf_path+"check_exit_point.py " + AS + " " + ref_check

os.system(command2)

os.remove("stretch-"+ref_check+".crp")
