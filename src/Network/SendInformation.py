#!/usr/bin/env python
# modified by tnm1264
"USEAGE: %s <receiverIp> <receiverPort>"
from socket import * # import *, but we'll avoid name conflict
from sys import argv, exit
import time

if len(argv) != 3:
	print __doc__ % argv[0]
	exit(0)

sock = socket(AF_INET, SOCK_DGRAM)

#Broadcasting option
sock.setsockopt(SOL_SOCKET,SO_BROADCAST,1)

velocity = 60
lat = 36.54323
long = 127.3572
while 1:
	messout = "Velocity : "+str(velocity)+"km/h"+" GPS : ("+str(lat)+","+str(long)+")"
	
	velocity += 1
	lat += 0.4533
	long += 0.3257
	sock.sendto(messout, (argv[1], int(argv[2])))
	time.sleep(0.7)

sock.close()

