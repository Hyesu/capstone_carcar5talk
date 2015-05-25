"""
	file: Raspberry_b_v014.py

	Kookmin University
	Department of compurt science
	20103310 Sungjung Kim 

	--- added IPC
	--- by 20123381 Hyesu Shin

"""

import os
import sys
import time
from bluetooth import *

import posix_ipc

SEM_NAME = '/CarTalk_sem_blue'
MQ_NAME = '/CarTalk_mq_blue'
MQ_FLAG = posix_ipc.O_CREAT | posix_ipc.O_NONBLOCK
MQ_PERM = 0777
MQ_MSG = 10
MSG_SIZE = 4096

class Bluetooth:
	sem = None
	mq = None
	uuid = ""
	server_sock = None
	client_sock = None

        def __init__(self):
                os.system("sudo /etc/init.d/bluetooth restart")
                self.uuid = "00001101-0000-1000-8000-00805f9b34fb"	# SerialPortServiceClass_UUID
                self.server_sock = BluetoothSocket(RFCOMM)

		self.mq = posix_ipc.MessageQueue(name=MQ_NAME, 			\
						 flags=MQ_FLAG,			\
						 mode=MQ_PERM, 			\
						 max_messages=MQ_MSG, 		\
						 max_message_size=MSG_SIZE, 	\
						 read=True, write=True)
		self.sem = posix_ipc.Semaphore(SEM_NAME)

        def __del__(self):
                self.client_sock.close()
                self.server_sock.close()
                print "Bluetooth::__del__: Server is closed."

	def receiveMsg(self):
		try:
			self.sem.acquire()
			data = self.mq.receive()		
			self.sem.release()

			if msg:
				return data[0]
			else:
				return None

		except posix_ipc.BusyError:
			self.sem.release()
			print "Bluetooth::receiveMsg: bluetooth queue is empty!"
			return None


        def run(self):
		self.server_sock.bind(("", PORT_ANY))	
		self.server_sock.listen(1)
		localMacAddr = self.server_sock.getsockname()[0]
                port = self.server_sock.getsockname()[1]
                print "Bluetooth::run: Waiting for a connection on RFCOMM channel %d" % port

                advertise_service(
                        self.server_sock, "Raspberry Pi",
                        service_id = self.uuid,
                        service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
                        profiles = [ SERIAL_PORT_PROFILE ],
                )
                self.client_sock, client_info = self.server_sock.accept()
                print "Bluetooth::run: Accepted connection from ", client_info[0]


	def receive(self):
		return self.client_sock.recv(4)		# SYN, ACK

	def send(self, data):
		return self.client_sock.send(data);

        def process(self):
		while True:
			data = self.receiveMsg()

			if data is not None:
				print "Bluetooth::process: sucess receive data(%s) from queue" %data
				ret = self.send(data)
				print "Bluetooth::process: Send [%d] OK" % ret

			# Set non-blocking - loop and poll for data
			self.client_sock.settimeout(0)

                print "Bluetooth::process: Disconnected."


if __name__ == "__main__":
        bt = Bluetooth()
        bt.run()
        bt.process()

