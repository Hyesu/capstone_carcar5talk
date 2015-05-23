#20103346 park hoon
#20103323 kim tae wook
# 15.05.21 ver 0.5

from wifi import Cell, Scheme
import socket
import time
import commands


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
			temp = Scheme.find('wlan0','home')
			if temp is not None:
				temp.delete()

			scheme = Scheme.for_cell('wlan0', 'home',cellName, passKey)
			scheme.save()
			scheme.activate()
				
			print "Try connect to <carcar5>"
			myIp = commands.getoutput("hostname -I")
			print "Connection Success my Ip is : " + myIp
			return True


def sendData():
	print "Send Data Start"
		
	while 1:
		message = "hoon babo"
		sendSock.sendto(message, ("192.168.10.0",8080))
		time.sleep(0.7)

		

######    Main    ########
sendSock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sendSock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST,1)

myIp = None

while 1:
	if scanWifi() is True:
		print "sending......."
		sendData()

 
	

