#!/usr/bin/env python
"USEAGE: %s <receiverIp> <receiverPort>"
from socket import * # import *, but we'll avoid name conflict
from sys import argv, exit

if len(argv) != 3:
	print __doc__ % argv[0]
	exit(0)

sock = socket(AF_INET, SOCK_DGRAM)

while 1:
	messout = raw_input("message to send : ")
	sock.sendto(messout, (argv[1], int(argv[2])))
	messin, server = sock.recvfrom(255)

	if messin == "quit":
		break

	if messin != messout:
		print "Failed to receive idential message"
		break
	else:
		print "Received:", messin

sock.close()

