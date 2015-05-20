from wifi import Cell, Scheme
import subprocess
import socket
import time
import commands
import threading


connection = False 

#################### Wifi Configure #################################
#wifi password
passKey = 'raspberry'
scheme = None


#Same as "sudo iwlist wlan0 scan"
cellName = None
cellList = Cell.all('wlan0')

#Find cell information which ssid is 'carcar5' 
for cell in cellList:
	if cell.ssid == 'carcar5':
		cellName = cell

#Connect to wifi
scheme = Scheme.find('wlan0','home')
if scheme is None:
	scheme = Scheme.for_cell('wlan0', 'home', cellName, passKey)
	scheme.save()
	scheme.activate()
else :
	scheme.delete()

######################################################################


"""

###################### Monitoring Connection ######################

class MonitoringConnection(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
	def run(self):
		while 1:
			connection = False
			time.sleep(1)
			if connection is True:
				continue	
			else :
				print "Disconnect !!!!"	
				break
			
		
#####################################################################


Monitor = MonitoringConnection()
Monitor.start()
Monitor.join()

"""



######################## Ping Thread ##############################
import subprocess
class PingThread(threading.Thread):

	cmdPing = "ping 192.168.10.1"

	def __init__(self):
		threading.Thread.__init__(self)
	
	def run(self):
		p =  subprocess.Popen(self.cmdPing,shell=True, stderr=subprocess.PIPE)
		
		while 1:
			out = p.stderr.read(1)
			if out == '' and p.poll()!= None:
				break
			if out !='':
				break	
		
		


###################################################################





######################## Receivce Part #############################
recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
recvSock.bind(("",8080))

#Get My Ip Address
myIp = commands.getoutput("hostname -I")
print myIp


#Receive data until disconnect ( need edit )
while 1:
	data, addr = recvSock.recvfrom(64)
	src = addr[0]
	src = src +" "
	if myIp != src:
		print data 

#####################################################################












	


#shell command "sudo ifdown wlan0"
#subprocess.call(["sudo","ifdown","wlan0"])
