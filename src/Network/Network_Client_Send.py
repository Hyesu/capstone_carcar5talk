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
# 	15.05.21 ver 0.7
#
############################################

from wifi import Cell, Scheme
import socket
import time
import commands

import posix_ipc


################ Constants ##################
MQ_NAME = "/CarTalk_mq_net_s"
MQ_FLAG = posix_ipc.O_CREAT | posix_ipc.O_NONBLOCK
MQ_PERM = 0777
MQ_MSG = 10
MSG_SIZE = 64
SEM_NAME = "/CarTalk_sem_net_s"

passKey = 'raspberry'
scheme = None

mq = None
sem = None

def init():
	mq = posix_ipc.MessageQueue(name=MQ_NAME,		\
				    flags=MQ_FLAG,		\
				    mode=MQ_PERM,		\
				    max_message=MQ_MSG,		\
				    max_message_size=MSG_SIZE,	\
				    read=True, write=True)
	sem = posix_ipc.Semaphore(SEM_NAME)

def receiveMsg():
	sem.acquire()
	msg = mq.receive()
	sem.release()

	return data

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
		# debug. get msg from message queue & send msg to other cars
		message = receiveMsg()
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

 
	

