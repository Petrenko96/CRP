#!/bin/python

import os
import sys

if(len(sys.argv) < 3):
	print("Usage : command [AS number] [cr1-cr2-...]")
	exit()

AS = sys.argv[1]
reference = "stretch-"+sys.argv[2]+".crp"

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

aspath = open(aspath)

ap = dict()
best = dict()
for line in aspath:
	s = line[:-1].split(" ")
	i = int(s[0])
	j = int(s[1])
	z = int(s[2])
	ap[(i, j)] = z
	ap[(j, i)] = z

	if(z <= 0):
		continue

	if(i not in best):
		best[i] = z
	else:
		if z < best[i] and z > 0:
			best[i] = z

	if(j not in best):
		best[j] = z
	else:
		if z < best[j]:
			best[j] = z

b = dict()
for i, j in ap:
	if ap[(i, j)] == best[i]:
		if i not in b:
			b[i] = list()

		b[i].append(j)


prefixes = open(prefixes)
p = dict()
for line in prefixes:
	s = line[:-1].split("\t")
	p[s[1]] = int(s[0])


fm = open(reference)
ret = ""
for line in fm:
	s = line[:-1].split("\t")
	pref = s[1]
	exit = int(s[2].split(" ")[-1])

	if(exit not in b[p[pref]]):
		ret += str(p[pref])+"\t"+str(exit)+"\t"+str(b[p[pref]])+"\t"+str(pref)+"\t"+str(s[2])+"\n"

if not ret == "":
	ret = "Prefix of\tExited by\tShould have\tPrefix\tPath\n"+ret
	print(ret)
else:
	print("All the traffic extis by a correct exit point")

