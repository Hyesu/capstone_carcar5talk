from socket import *
import time

sendSock = socket(AF_INET,SOCK_DGRAM)
sendSock.setsockopt(SOL_SOCKET,SO_BROADCAST,1)

while 1:
	message = "TEST"
	sendSock.sendto(message, ("192.168.10.0",8080))
	time.sleep(0.7)
