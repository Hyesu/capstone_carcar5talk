#!/usr/bin/env python
# modified by tnm1264

"USAGE: %s <port>"
from SocketServer import DatagramRequestHandler, UDPServer
from sys import argv

class EchoHandler (DatagramRequestHandler):
	def handle(self):
		#print "Client connected:", self.client_address
		message = self.rfile.read()
		if message == "quit":
			return
		print "Vehicle sent::: ", message
		self.wfile.write(message)
if len(argv) !=2:
	print __doc__ % argv[0]
else:
	UDPServer(('',int(argv[1])), EchoHandler).serve_forever()
