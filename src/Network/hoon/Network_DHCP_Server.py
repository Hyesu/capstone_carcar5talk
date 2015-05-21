###########################################
#
#	CarTalk System
#	
#	Kookmin University Capstone Project
#	Team. Carcar5talk
#	20103323 Kim Taewook
#	20103346 Park Hoon
#	20123381 Shin Hyesu
#
############################################

import threading
import socket
import commands
import time

import posix_ipc


################# Constants ################
MQ_NAME = "/CarTalk_mq_net_r"
MQ_FLAG = posix_ipc.O_CREAT | posix_ipc.O_NONBLOCK
MQ_PERM = 0777
MQ_MSG = 10
MSG_SIZE = 64
SEM_NAME = "/CarTalk_sem_net_r"


def init():
	time.sleep(10)

	mq = posix_ipc.MessageQueue(name=MQ_NAME,		\
				    flags=MQ_FLAG, 		\
				    mode=MQ_MODE,		\
				    max_messages=MQ_MSG,	\
				    max_message_size=MSG_SIZE,	\
				    read=True, write=True)
	sem = posix_ipc.Semaphore(SEM_NAME)
				
	#Create Socket for receive msg
	recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	#Bind with same network
	recvSock.bind(("",8080))

	#Get My IP Address
	myIP = commands.getoutput("hostname -I")
	print myIP

	return recvSock, myIp

def sendMsg(data):
	sem.acquire()
	mq.send(data, 0)
	sem.release()


################# Main #####################
recvSock, myIp = init()
while True:
	data , addr = recvSock.recvfrom(MSG_SIZE)
	srcAddr = addr[0]
	srcAddr = srcAddr +" "

	#Only get others information
	if myIP != srcAddr:
		print srcAddr+"A"
 		print myIP+"A"
		print data

		sendMsg(data)
		
