"""
	file: Raspberry_b_v014.py

	Kookmin University
	Department of compurt science
	20103310 Sungjung Kim 

"""
import os
import sys
import time
from bluetooth import *

class Bluetooth:
        def __init__(self):
                os.system("sudo /etc/init.d/bluetooth restart")
		self.f = open("case01.normal.bin", "rb")
                self.uuid = "00001101-0000-1000-8000-00805f9b34fb"	# SerialPortServiceClass_UUID
                self.server_sock = BluetoothSocket(RFCOMM)

        def __del__(self):
		self.f.close()
                self.client_sock.close()
                self.server_sock.close()
                print "Server is closed."

        def run(self):
		self.server_sock.bind(("", PORT_ANY))	
		self.server_sock.listen(1)
		localMacAddr = self.server_sock.getsockname()[0]
                port = self.server_sock.getsockname()[1]
                print "Waiting for a connection on RFCOMM channel %d" % port

                advertise_service(
                        self.server_sock, "Raspberry Pi",
                        service_id = self.uuid,
                        service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
                        profiles = [ SERIAL_PORT_PROFILE ],
                )
                self.client_sock, client_info = self.server_sock.accept()
                print "Accepted connection from ", client_info[0]


	def receive(self):
		return self.client_sock.recv(4)		# SYN, ACK

	def send(self, data):
		return self.client_sock.send(data);

        def process(self):
		#data = self.receive()
		#data = data.decode(encoding='UTF-8')		# byte data
		#if data == "SYN":

		#print "Sent [%s] packet." % data
		#n = os.path.getsize("case01.normal.bin")	# get file size

		#print "File size is %d." % n

		#ret = self.send(bytes(n))
		#print "Send [%d] OK" % ret
		#print "Size [%d]" % sys.getsizeof(bytes(n))

		#data = self.receive()
		#data = data.decode(encoding='UTF-8')
		#print "Sent [%s] packet." % data

		#if data == "ACK":
		data = self.f.read(1000)
		#debug
		print "data type(%s)" %type(data)
		ret = self.send(data)

		print "Send [%d] OK" % ret

		# Set non-blocking - loop and poll for data
		self.client_sock.settimeout(0)
                print "Disconnected."


if __name__ == "__main__":
        bt = Bluetooth()
        bt.run()
        bt.process()

