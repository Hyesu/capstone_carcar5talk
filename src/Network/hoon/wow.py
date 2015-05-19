import threading
from socket import *
import time
from SocketServer import DatagramRequesthandler, UDPServer

class SendingThread(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
	
	def run(self):
		sendSock = socket(AF_INET, SOCK_DGRAM)
		
		sendSock.setsockopt(SOL_SOCKET,SO_BROADCAST,1)
	
		while 1:
			message = "hohohoh"
			
			sendSock.sendto(message, ("192.168.10.255",8080))

			time.sleep(0.7)

class ReceivingThread(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)

	def run(self):
		recvSock = socket(AF_INET, SOCK_DGRAM)
		
		recvSock.bind(("",8080))
		
		while 1:
			data, addr = recvSock.recvfrom(64)
			print data	
		
	

Receive = RecevingThread()
Receive.start()
Receive.join():

