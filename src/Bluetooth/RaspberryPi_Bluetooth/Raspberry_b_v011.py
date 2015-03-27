# file: rfcomm-server.py
# desc: simple demonstration of a server application that uses RFCOMM sockets
#
from bluetooth import *

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "00001101-0000-1000-8000-00805f9b34fb"
#uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( 
	server_sock, "SampleServer",
	service_id = uuid,
        service_classes = [ uuid, SERIAL_PORT_CLASS ],
        profiles = [ SERIAL_PORT_PROFILE ], 
#       protocols = [ OBEX_UUID ] 
)
                   
print "Waiting for connection on RFCOMM channel %d" % port

client_sock, client_info = server_sock.accept()
print "Accepted connection from ", client_info

try:
    while True:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print "received [%s]" % data
except IOError:
    pass

print "disconnected"

client_sock.close()
server_sock.close()
print "all done"
