#!/usr/bin/python

#20103346 park hoon
#20103323 kim tae wook
#20123381 shin hyesu
# 15.05.21 ver 0.5

from wifi import Cell, Scheme
import socket
import time
import commands
import sys
import os
import signal
import subprocess

import posix_ipc


################ Constants ##################
MQ_NAME_S = "/CarTalk_mq_net_s"
MQ_NAME_R = "/CarTalk_mq_net_r"
MQ_FLAG = posix_ipc.O_CREAT | posix_ipc.O_NONBLOCK
MQ_PERM = 0777
MQ_MSG = 10
MSG_SIZE = 128
SEM_NAME_S = "/CarTalk_sem_net_s"
SEM_NAME_R = "/CarTalk_sem_net_r"

passKey = 'raspberry'
scheme = None

mq_s = None
mq_r = None
sem_s = None
sem_r = None



################### Variable ##############
nickname = 'home'    
iface = 'wlan0'			#interface
ssid = 'carcar5'		#essid
passKey = 'raspberry'		#password
scheme = None			
port = 8080			
broadcastAddr = '192.168.10.0'	
INTERVAL = 1   #sec
isChild = False
pid = os.getpid() 
childPid =0
recvSock =None 
isParent = None

###########################################
def init():
	mq_s = posix_ipc.MessageQueue(name=MQ_NAME_S,		\
				    flags=MQ_FLAG,		\
				    mode=MQ_PERM,		\
				    max_messages=MQ_MSG,		\
				    max_message_size=MSG_SIZE,	\
				    read=True, write=True)
	sem_s = posix_ipc.Semaphore(SEM_NAME_S)

	mq_r = posix_ipc.MessageQueue(name=MQ_NAME_R,		\
				    flags=MQ_FLAG,		\
				    mode=MQ_PERM,		\
				    max_messages=MQ_MSG,		\
				    max_message_size=MSG_SIZE,	\
				    read=True, write=True)
	sem_r = posix_ipc.Semaphore(SEM_NAME_R)
	
	return mq_s, sem_s, mq_r, sem_r

def receiveMsg():
	try :
		sem_s.acquire()
		msg = mq_s.receive()
		sem_s.release()
		
		if msg:
			return msg[0]
		else:
			return None

	except posix_ipc.BusyError:
		sem_s.release()	
		print "Network::receiveMsg: net_s queue is empty!"
		time.sleep(INTERVAL)
		return None


def sendMsg(data):
	try:
		sem_r.acquire()
		mq_r.send(data)
		sem_r.release()

	except posix_ipc.BusyError:
		sem_r.release()	
		print "Network::sendMsg: net_r queue is full!"
		time.sleep(INTERVAL)


def scanWifi():
	while 1:	
		cellName=None
		#Wifi scannig use interface 'wlan0'
		cellList = Cell.all(iface)


		print "Network::scanWifi: Scan Around Cell"
		
		#find in cellList 
		for cell in cellList:
			if cell.ssid == ssid:
				cellName =cell


		if cellName is None:
			print "Network::scanWifi: Can not found <carcar5> try again"
			time.sleep(1)
			continue

		# if there is 'carcar5' ap 
		# Make connection 
		else :
			temp = Scheme.find(iface,nickname)
			if temp is not None:
				temp.delete()

			scheme = Scheme.for_cell(iface,nickname,cellName, passKey)
			scheme.save()
			scheme.activate()
				
			print "Network::scanWifi: Try connect to <carcar5>"
			myIp = commands.getoutput("hostname -I")
			print "Network::scanWifi: Connection Success my Ip(" + myIp + ")"

			childPid = os.fork()
			#print "after os.fork() " + str(pid)
			#print str(os.getpid()) + "--> child Pid : " + str(childPid)
			# Parent process : Send Data	
			if childPid:
				#print "Parent PID : " + str(os.getpid()) + " , Child PID : "+ str(childPid)
				sendData(childPid)


			# Child process : Receive Data
			else :
				#print "Child PID : " + str(os.getpid()) + " Parent PID : " + str(os.getppid())	
				receiveData()

def sendData(pid):
	print "Network::sendData: Send Data Start"
	isParent = True
		
	while 1:
		try:
			message = receiveMsg()
			if message is not None:
				sendSock.sendto(message, (broadcastAddr,port))
				#print "Network::sendData: msg(%s) from net_s queue - success send" %message

			time.sleep(INTERVAL)
	
		#KeyboadInerrupt .. it needs to debug and programming 	
		except KeyboardInterrupt:
			print "KeyboadInterrupt!!!! Exit the program Bye~~"
			sys.exit(0)
			

		#If disconnect ...
		except socket.error:
			#print str(os.getpid()) + "---> " + str(pid)
			os.kill(pid,signal.SIGTERM)
			break 	

def receiveData():
	print "Network::receiveData: Receive Data Start"

	#Create Socket for receive msg	
	recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	#Bind with same network
	recvSock.bind(("",8080))

	#Get My IP Address
	myIP = commands.getoutput("hostname -I")
	
	while 1:
		data, addr = recvSock.recvfrom(MSG_SIZE)
		srcAddr = addr[0]
		srcAddr = srcAddr + " "
		
		#Only get others information
		if myIP != srcAddr:
			print "Network::receiveData: success receive data(%s) from other pi" %data
			sendMsg(data)

		time.sleep(INTERVAL)
			

def signalChild(signal, frame):
	return

signal.signal(signal.SIGCHLD,signalChild)

def signalHandler(signal, frame):
	if isParent:
		print " I will not die because I'm a parent " + str(os.getpid())
	else :
		print " I will die becaue I'm a child " + str(os.getpid())
		sys.exit()
signal.signal(signal.SIGTERM,signalHandler)


##########   Main    ###########
sendSock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sendSock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST,1)

mq_s, sem_s, mq_r, sem_r =init()
scanWifi()
	
################################
