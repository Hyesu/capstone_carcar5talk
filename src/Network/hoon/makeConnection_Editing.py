#20103346 park hoon
#20103323 kim tae wook
# 15.05.21 ver 0.5

from wifi import Cell, Scheme
import socket
import time
import commands

################### Variable ##############
nickname = 'home'    
iface = 'wlan0'			#interface
ssid = 'carcar5'		#essid
passKey = 'raspberry'		#password
scheme = None			
port = 8080			
broadcastAddr = '192.168.10.0'	
sendInterval = 0.7   #sec
###########################################

def scanWifi():
	while 1:	
		cellName=None
		#Wifi scannig use interface 'wlan0'
		cellList = Cell.all('wlan0')


		print "Scan Around Cell"
		
		#find in cellList 
		for cell in cellList:
			if cell.ssid == ssid:
				cellName =cell


		if cellName is None:
			print "Can not found <carcar5> try again"
			time.sleep(1)
			continue
		else :
			temp = Scheme.find(iface,nickname)
			if temp is not None:
				temp.delete()

			scheme = Scheme.for_cell(ifcae,nickname,cellName, passKey)
			scheme.save()
			scheme.activate()
				
			print "Try connect to <carcar5>"
			myIp = commands.getoutput("hostname -I")
			print "Connection Success my Ip is : " + myIp
			return True


def sendData():
	print "Send Data Start"
		
	while 1:
		try:
			message = "hoon babo"
			sendSock.sendto(message, (broadcastAddr,port))
			time.sleep(sendInterval)  #0.7sec
	
		#KeyboadInerrupt .. it needs to debug and programming 	
		except KeyboardInterrupt:
			print "KeyboadInterrupt!!!! Exit the program Bye~~"
			sendSock.close()
			return 0 

		#If disconnect ...
		except :
			break 	
			

		

######    Main    ########
sendSock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sendSock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST,1)

myIp = None

while 1:
	if scanWifi() is True:
		print "sending......."
		sendData()

 
	
################################
