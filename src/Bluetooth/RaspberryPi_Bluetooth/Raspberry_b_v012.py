"""
import bluetooth

name="bluetooth_server"
target_name="target_name"
uuid="00001101-0000-1000-8000-00805f9b34fb"

def runServer():
	serverSocket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
	port = bluetooth.PORT_ANY
	serverSocket.bind(("", port))
	
	print "Listening for connections on port: ", port

	serverSocket.Listen(1)
	port = serverSocket.getsockname()[1]

	advertise_service( server_sock, "Sample Server",
			service_id = uuid,
			service_class = [ uuid, bluetooth.SERIAL_PORT_CLASS ],
			profiles = [ bluetooth.SERIAL_PORT_PROFILE ]
	)

	inputSocket, address = serverSocket.accept()
	
	print "Success to connection with ", address

	data = inputSocket.recv("1024")
	print "received [%s] \n" % data
	inputSocket.close()
	serverSocket.close()

runServer()
"""



import bluetooth

name="bt_server"
target_name="siggen"
uuid="00001101-0000-1000-8000-00805f9b34fb"

def runServer():
	serverSocket=bluetooth.BluetoothSocket(bluetooth.RFCOMM )
	port=bluetooth.PORT_ANY
	serverSocket.bind(("",port))
	print "Listening for connections on port: ", port   
    	serverSocket.listen(1)
    	port=serverSocket.getsockname()[1]

    	#the missing piece
	advertise_service( server_sock, "SampleServer",
                       service_id = uuid,
                       service_classes = [ uuid, bluetooth.SERIAL_PORT_CLASS ],
                       profiles = [ bluetooth.SERIAL_PORT_PROFILE ] 
	)

    	inputSocket, address=serverSocket.accept()
    	print "Got connection with" , address
    	data=inputSocket.recv("1024")
    	print "received [%s] \n " % data    
    	inputSocket.close()
    	serverSocket.close()  

runServer()  

