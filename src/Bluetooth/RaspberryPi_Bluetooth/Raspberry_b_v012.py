import os
from bluetooth import *

server_sock = BluetoothSocket(RFCOMM)
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid="00001101-0000-1000-8000-00805f9b34fb"

advertise_service(
	server_sock, "Raspberry Pi",
	service_id = uuid,
	service_classes = [ uuid, SERIAL_PORT_CLASS ],
	profiles = [ SERIAL_PORT_PROFILE ],
)

print "Waiting for connection on RFCOMM channel %d" % port

client_sock, client_info = server_sock.accept()

print "Accepted connection from ", client_info


client_sock.send("Hello World")

try:
	while True:
		data = client_sock.recv(1024)
		if len(data) == 0: break
		print "[Android] %s" % data

		#message = input()
		client_sock.send("hello world")		

		"""
		if not message: break

		if len(message) == 0:
			break
		else:

		print "Raspberry Pi: %s" % message
		"""
		
except IOError:
	pass

print "Disconnected."

client_sock.close()
server_sock.close()

print "Server is closed."
