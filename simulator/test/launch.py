#!/bin/python

import sys
import os

paths = [("../bin", "./"), ("bin", "test")]

config_path = None
for path in paths:
	if os.path.isdir(path[0]):
		config_path = path

if config_path is None:
	print("No test file found")
	exit(-1)

command = "java -jar " + config_path[1] + "/junit-platform-console-standalone-1.2.0.jar --classpath " + config_path[0] + " -c JUnit.TestTypoUSA"
os.system(command)
