##############################################
#
#	CarTalk System
#	
#	Kookmin University Capstone Project
#	Team. Carcar5talk
#	20103323 Kim Taewook
#	20103346 Park hoon
#
##############################################


from socket import *
import time


#Create socket for Send msg
sendSock = socket(AF_INET,SOCK_DGRAM)

#Option for BroadCasting
sendSock.setsockopt(SOL_SOCKET,SO_BROADCAST,1)

while 1:
	message = "Hello Hi Hello"
	#print message
	#Send message
	sendSock.sendto(message, ("192.168.10.0",8080))
	time.sleep(0.7)
		
