import os
import sys
import time
from bluetooth import *

class Bluetooth:
        def __init__(self):
                os.system("sudo /etc/init.d/bluetooth restart")
		self.f = open("case01.normal.bin", "rb")
		#self.f = open("input.txt", "r")
                self.uuid = "00001101-0000-1000-8000-00805f9b34fb"
                self.server_sock = BluetoothSocket(RFCOMM)

        def __del__(self):
		self.f.close()
                self.client_sock.close()
                self.server_sock.close()
                print "Server is closed."

        def listen(self):
                self.server_sock.bind(("", PORT_ANY))
                self.server_sock.listen(1)
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

        def process(self):
                try:
			byte = self.f.read(30)
			data = byte.decode('utf-8')
			print(data)
			self.client_sock.send(data)

                        while True:
				time.sleep(1)

				byte = self.f.read(35)
				data = byte.decode('utf-8')
				if len(data) == 0: break
				print(data)
	                        self.client_sock.send(data)
                except IOError:
                        pass
                print "Disconnected."


if __name__ == "__main__":
        bt = Bluetooth()
        bt.listen()
        bt.process()

