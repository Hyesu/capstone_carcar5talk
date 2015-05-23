import threading
import socket
import time


class ReceivingThread(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)

	def run(self):
		recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		recvSock.bind(("",8080))
		
		while 1:
			data , addr = recvSock.recvfrom(64)
			print data

		


Receive = ReceivingThread()
Receive.start()
Receive.join()

