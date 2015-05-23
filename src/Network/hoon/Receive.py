###########################################
#
#	CarTalk System
#	
#	Kookmin University Capstone Project
#	Team. Carcar5talk
#	20103323 Kim Taewook
#	20103346 Park Hoon
#
############################################


import threading
import socket
import commands
import time
import signal
import subprocess
import sys
import os



def signalHandler(SIGNO,frame):
	print "kill"
	sys.exit(0)	
	
	
signal.signal(signal.SIGTERM, signalHandler)


time.sleep(10)

#Create Socket for receive msg
recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
#Bind with same network
recvSock.bind(("",8080))


#Get My IP Address
myIP = commands.getoutput("hostname -I")
print myIP

while 1:
	data , addr = recvSock.recvfrom(64)
	srcAddr = addr[0]
	srcAddr = srcAddr +" "

	#Only get others information
	if myIP != srcAddr:
		print srcAddr+"A"
		print myIP+"A"
		print data

