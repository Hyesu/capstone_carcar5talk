from wifi import Cell, Scheme
import socket
import time
import commands
import os
import sys


passKey = 'raspberry'
scheme = None


def scanWifi():
	while 1:
		cellName =None
		cellList = Cell.all('wlan0')


		print "Scan Around Cell"

		for cell in cellList:
			if cell.ssid == 'carcar5':
				cellName =cell

		if cellName is None:
			print "Can not found <carcar5> try again"
			time.sleep(1)
			continue
		else :
			scheme = Scheme.for_cell('wlan0', 'home','carcar5',passKey)
			scheme.activate()
			print "Try connect to <carcar5>"
			myIp = commands.getoutput("hostname -I")
			print "Connection Success my Ip is : " + myIp
			return True


def receiveData():
	print "Receive Data Start"
		
	while 1:
		data , addr = recvSock.recvfrom(64)
		src = addr[0]
		src = src +" "
		if myIp != src:
			print data 

		

######    Main    ########

recvSock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
recvSock.bind(("",8080))


myIp = None

while 1:
	if scanWifi() is True:
#		os.system("python Send.py")
		receiveData()

 
	

