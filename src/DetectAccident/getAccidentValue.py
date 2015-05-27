###################################################
#
#   CarTalk System
#   
#   Kookmin University Capstone Project
#   Team. Carcar5talk
#   20123381 Shin Hyesu
#
###################################################

import spidev, os, time
import math
import RPi.GPIO as GPIO
import threading
import posix_ipc


#################### Constants ####################
X_CHANNEL = 0
Y_CHANNEL = 1
Z_CHANNEL = 2
LED_YELLOW = 18
BUTTON = 7

DELAY = 1
MAX_REF = 5

COEFF = [0.001861, 0.004444, 0.007667]
CONST = [1.65, 1.65, 2.31]
MIN = [0, -180, -90]
MAX = [360, 180, 90]

POWER_VOLT = 3.3
CONV_CONST = 1023

COLLISION_THR = 10
ROLLOVER_THR = 90
ROLLOVER_MAX = 180

COLLISION_COEF_HEAD_ON = 0.175
COLLISION_COEF_BROADSIDE = 0.1857
COLLISION_COEF_Z = 0.3
ROLLOVER_COEF = 49

MQ_NAME = '/CarTalk_mq_da'
MQ_FLAG = posix_ipc.O_CREAT | posix_ipc.O_NONBLOCK
MQ_PERM = 0777
MQ_MSG = 10
MSG_SIZE = 2

SEM_NAME = '/CarTalk_sem_da'


###################### Global Vars ##################
mq = None
sem = None


###################### Functions ##################
def init():
	spi = spidev.SpiDev()
	spi.open(0,0)

	GPIO.setwarnings(False)
	
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(LED_YELLOW, GPIO.OUT)
	GPIO.setup(BUTTON, GPIO.IN, GPIO.PUD_UP)

	GPIO.output(LED_YELLOW, False)

	mq = posix_ipc.MessageQueue(name=MQ_NAME, 		\
				    flags=MQ_FLAG,		\
				    mode=MQ_PERM,		\
				    max_messages=MQ_MSG,	\
				    max_message_size=MSG_SIZE,	\
				    read=True, write=True)
	sem = posix_ipc.Semaphore(SEM_NAME)

	return spi, mq, sem

def sendMsg(isAccident):
	fullFlag = False
	try:
		sem.acquire()
		if isAccident:
			mq.send("T", 0)
		else:
			mq.send("F", 0)
		sem.release()
	except posix_ipc.BusyError:
		if fullFlag:
			if isAccident:
				mq.send("T", 0)
			else:
				mq.send("F", 0)
			sem.release()
		else:
			print "DetectAccident::sendMsg: da queue is full"
			fullFlag = True
			while True:
				mq.receive()


class LEDThread(threading.Thread):
	numBlink = None

	def __init__(self):
		threading.Thread.__init__(self)
		self.numBlink = 5

	def run(self):
		for i in range(0, self.numBlink):
			GPIO.output(LED_YELLOW, True)
			time.sleep(DELAY)
			GPIO.output(LED_YELLOW, False)
			time.sleep(DELAY)
			i = i + 1


class TriAxisThread(threading.Thread):
	spi = None
	rVector = None
	numRef = 0

	def __init__(self, spi):
		threading.Thread.__init__(self)
		self.spi = spi
		self.rVector = []

	def readChannel(self, spi, channel):
		adc = spi.xfer2([1, (8 + channel) << 4, 0])
		data = ((adc[1] & 3) << 8) + adc[2]
		return data

	def getValue(self, data, axis):
		value = ((data * POWER_VOLT / float(CONV_CONST)) - CONST[axis]) / COEFF[axis]
		if value < MIN[axis]:
			value = MIN[axis]
		if value > MAX[axis]:
			value = MAX[axis]

		return value

	def isHeadOnCollision(self, rVector, mVector):
		aVector = [mVector[0]-rVector[0], mVector[1]-rVector[1], mVector[2]-rVector[2]]
		aCrash = pow(aVector[0], 2) + pow(aVector[1], 2) + pow(aVector[2], 2)
		aCrash = math.sqrt(aCrash) * COLLISION_COEF_HEAD_ON

		# print value
		#print "DA::isHeadCollision: mVector(%f, %f, %f)" %(mVector[0], mVector[1], mVector[2])
		#print "DA::isHeadCollision: aCrash(%f)" %aCrash

		if aCrash <= COLLISION_THR:
			return False
		else:
			LED = LEDThread()
			LED.start()
			return True

	def isBroadsideCollision(self, rVector, mVector):
		# change x-axis and y-axis
		rVector = [rVector[1], rVector[0], rVector[2]]
		mVector = [mVector[1], mVector[0], mVector[2]]
		
		aVector = [mVector[0]-rVector[0], mVector[1]-rVector[1], mVector[2]-rVector[2]]
		aCrash = pow(aVector[0], 2) + pow(aVector[1], 2) + pow(aVector[2], 2)
		aCrash = math.sqrt(aCrash) * COLLISION_COEF_BROADSIDE

		# print value
		#print "DA::isBroadCollision: mVector(%f, %f, %f)" %(mVector[0], mVector[1], mVector[2])
		#print "DA::isBroadCollision: aCrash(%f)" %aCrash

		if aCrash <= COLLISION_THR:
			return False
		else:
			LED = LEDThread()
			LED.start()
			return True

	def isRollOver(self, rVector, mVector):
		exp1 = rVector[0]*mVector[0] + rVector[1]*mVector[1] + rVector[2]*mVector[2] 
		exp2 = math.sqrt(pow(rVector[0],2) + pow(rVector[1],2) + pow(rVector[2],2))
		exp3 = math.sqrt(pow(mVector[0],2) + pow(mVector[1],2) + pow(mVector[2],2))
		exp4 = exp1 / (exp2 * exp3)
		if exp4 > 1:
			exp4 = 1
		if exp4 < -1:
			exp4 = -1
		theta = math.acos(exp4) * ROLLOVER_COEF

		# print value
		#print "DA::isRollOver: mVector(%f, %f, %f)" %(mVector[0], mVector[1], mVector[2])
		#print "DA::isRollover: theta(%f)" %theta

		if theta >= ROLLOVER_THR and theta <= ROLLOVER_MAX:
			LED = LEDThread()
			LED.start()
			return True
		else:
			return False

	def run(self):
		while True:
			xData = self.readChannel(spi, X_CHANNEL)
			yData = self.readChannel(spi, Y_CHANNEL)
			zData = self.readChannel(spi, Z_CHANNEL)

			xValue = self.getValue(xData, X_CHANNEL)
			yValue = self.getValue(yData, Y_CHANNEL)
			zValue = self.getValue(zData, Z_CHANNEL)

			if self.rVector == []:
				self.rVector = [xValue, yValue, zValue]
				pass

			isAccident = False
			mVector = [xValue, yValue, zValue]
			if self.isRollOver(self.rVector, mVector):
				isAccident = True

			mVector[2] = mVector[2] * COLLISION_COEF_Z
			if self.isHeadOnCollision(self.rVector, mVector):
				isAccident = True
			if self.isBroadsideCollision(self.rVector, mVector):
				isAccident = True

			sendMsg(isAccident)
			
			if self.numRef == MAX_REF:
				self.numRef = 0
				self.rVector = []
			else:
				self.numRef = self.numRef + 1
			time.sleep(DELAY)

class ButtonThread(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)

	def isPushed(self):
		if not GPIO.input(BUTTON):
			LED = LEDThread()
			LED.start()
			return True
		else:
			return False

	def run(self):
		while True:
			isAccident = self.isPushed()
			time.sleep(DELAY/2)
			sendMsg(isAccident)
	

	
####################### Main ######################
spi, mq, sem = init()

TriAxis = TriAxisThread(spi)
Button = ButtonThread()

TriAxis.start()
Button.start()

TriAxis.join()
Button.join()


#GPIO.cleanup()
