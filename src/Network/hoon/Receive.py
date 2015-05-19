import threading
import socket
import commands

recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
recvSock.bind(("",8080))

#Get My IP Address
myIP = commands.getoutput("hostname -I")
print myIP

while 1:
	data , addr = recvSock.recvfrom(64)
	srcAddr = addr[0]
	srcAddr = srcAddr +" "

	if myIP != srcAddr:
		print data

