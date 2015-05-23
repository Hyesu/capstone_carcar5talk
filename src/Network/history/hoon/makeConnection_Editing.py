#!/usr/bin/python

#20103346 park hoon
#20103323 kim tae wook
# 15.05.21 ver 0.5

from wifi import Cell, Scheme
import socket
import time
import commands
import sys
import os
import signal
import subprocess

################### Variable ##############
nickname = 'home'    
iface = 'wlan0'			#interface
ssid = 'carcar5'		#essid
passKey = 'raspberry'		#password
scheme = None			
port = 8080			
broadcastAddr = '192.168.10.0'	
sendInterval = 0.7   #sec
isChild = False
pid = 0  
recvSock =None 
###########################################

def scanWifi():
	while 1:	
		cellName=None
		#Wifi scannig use interface 'wlan0'
		cellList = Cell.all(iface)


		print "Scan Around Cell"
		
		#find in cellList 
		for cell in cellList:
			if cell.ssid == ssid:
				cellName =cell


		if cellName is None:
			print "Can not found <carcar5> try again"
			time.sleep(1)
			continue

		# if there is 'carcar5' ap 
		# Make connection 
		else :
			temp = Scheme.find(iface,nickname)
			if temp is not None:
				temp.delete()

			scheme = Scheme.for_cell(iface,nickname,cellName, passKey)
			scheme.save()
			scheme.activate()
				
			print "Try connect to <carcar5>"
			myIp = commands.getoutput("hostname -I")
			print "Connection Success my Ip is : " + myIp

			pid = os.fork()
			# Parent process : Send Data	
			if pid > 0 :
				print "Parent PID : " + str(os.getpid())
				return True
			# Child process : Receive Data
			else :
				print "Child PID : " + str(os.getpid()) + " Parent PID : " + str(os.getppid())	
				#subprocess.call(['python','Receive.py'])
				#os.execl('/usr/bin/python2.7/python','/usr/bin/python2.7/python','/home/pi/capstone_carcaar5talk/src/Network/hoon/Recieve.py')
				isChild = True
				print "This isChild : " + str(isChild) 
				return True

def sendData():
	print "Send Data Start"
		
	while 1:
		try:
			message = "Right pi"
			sendSock.sendto(message, (broadcastAddr,port))
			time.sleep(sendInterval)  #0.7sec
	
		#KeyboadInerrupt .. it needs to debug and programming 	
		except KeyboardInterrupt:
			print "KeyboadInterrupt!!!! Exit the program Bye~~"
			sys.exit(0)
			

		#If disconnect ...
		except :	
			os.kill(pid,signal.SIGTERM)
			break 	

def receiveData():
	print "Receive Data Start"

	#Create Socket for receive msg	
	recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	#Bind with same network
	recvSock.bind(("",8080))

	#Get My IP Address
	myIP = commands.getoutput("hostname -I")
	
	while 1:
		data, addr = recvSock.recvfrom(64)
		srcAddr = addr[0]
		srcAddr = srcAddr + " "
		
		#Only get others information
		if myIP != srcAddr:
			print data		
			


def signalHandler(signal, frame):
	print "kill signal"
	sys.exit(0)

signal.signal(signal.SIGTERM,signalHandler)


######    Main    ########
sendSock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sendSock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST,1)

myIp = None

while 1:
	if scanWifi() is True:
		if isChild:
			print "Child PID : " + str(os.getpid())
			receiveData()	
			break	
		print "sending......."
		sendData()

 
	
################################