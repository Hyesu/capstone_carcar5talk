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
import thread

#################### Constants ####################
X_CHANNEL = 0
Y_CHANNEL = 1
Z_CHANNEL = 2
LED_YELLOW = 18
BUTTON = 7

DELAY = 0.5

COEFF = [0.001861, 0.004444, 0.007667]
CONST = [1.65, 1.65, 2.31]
MIN = [0, -180, -90]
MAX = [360, 180, 90]

POWER_VOLT = 3.3
CONV_CONST = 1023

COLLISION_THR = 10
ROLLOVER_THR = 90
ROLLOVER_MAX = 180

COLLISION_COEF = 0.19
ROLLOVER_COEF = 45.535


###################### Functions ##################
def init():
	spi = spidev.SpiDev()
	spi.open(0,0)

	GPIO.setwarnings(False)
	
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(LED_YELLOW, GPIO.OUT)
	GPIO.setup(BUTTON, GPIO.IN, GPIO.PUD_UP)

	GPIO.output(LED_YELLOW, False)

	return spi

def readChannel(spi, channel):
	adc = spi.xfer2([1, (8 + channel) << 4, 0])
	data = ((adc[1] & 3) << 8) + adc[2]
	return data

def getValue(data, axis):
	value = ((data * POWER_VOLT / float(CONV_CONST)) - CONST[axis]) / COEFF[axis]
	if value < MIN[axis]:
		value = MIN[axis]
	if value > MAX[axis]:
		value = MAX[axis]
	return value

def LEDThread():
	numBlink = 5
	for i in range(0, numBlink):
		GPIO.output(LED_YELLOW, True)
		time.sleep(DELAY)
		GPIO.output(LED_YELLOW, False)
		time.sleep(DELAY)
		i = i + 1

def isCollision(rVector, mVector):
	aVector = [mVector[0]-rVector[0], mVector[1]-rVector[1], mVector[2]-rVector[2]]
	aCrash = pow(aVector[0], 2) + pow(aVector[1], 2) + pow(aVector[2], 2)
	aCrash = math.sqrt(aCrash)
	
	if aCrash >= -COLLISION_THR and aCrash <= COLLISION_THR:
		return False
	else:
		try:
			thread.start_new_thread(LEDThread, ())
		except:
			print "Error: unable to start LED Thread"
		return True

def isRollOver(rVector, mVector):
	exp1 = rVector[0]*mVector[0] + rVector[1]*mVector[1] + rVector[2]*mVector[2] 
	exp2 = math.sqrt(pow(rVector[0],2) + pow(rVector[1],2) + pow(rVector[2],2))
	exp3 = math.sqrt(pow(mVector[0],2) + pow(mVector[1],2) + pow(mVector[2],2))
	exp4 = exp1 / (exp2 * exp3)
	if exp4 > 1:
		exp4 = 1
	if exp4 < -1:
		exp4 = -1
	theta = math.acos(exp4)

	if theta >= ROLLOVER_THR and theta <= ROLLOVER_MAX:
		try:
			thread.start_new_thread(LEDThread, ())
		except:
			print "Error: unable to start LED Thread"
		return True
	else:
		return False

def isPushed():
	if not GPIO.input(BUTTON):
		try:
			thread.start_new_thread(LEDThread, ())
		except:
			print "Error: unable to start LED Thread"

def TriAxisThread(spi):
	rVector = []
	while True:
		xData = readChannel(spi, X_CHANNEL)
		yData = readChannel(spi, Y_CHANNEL)
		zData = readChannel(spi, Z_CHANNEL)

		xValue = getValue(xData, X_CHANNEL)
		yValue = getValue(yData, Y_CHANNEL)
		zValue = getValue(zData, Z_CHANNEL)

		if rVector == []:
			rVector = [xValue, yValue, zValue]
			pass

		mVector = [xValue, yValue, zValue]
		print("isCollision: {}".format(isCollision(rVector, mVector)))
		print("isRollOver: {}\n".format(isRollOver(rVector, mVector)))

		time.sleep(DELAY/2)

def ButtonThread():
	while True:
		isPushed()

	
####################### Main ######################
spi = init()
try:
	thread.start_new_thread(TriAxisThread, (spi))
except:
	print "Error: unable to start TriAxis thread"

try:
	thread.start_new_thread(ButtonThread, ())
except:
	print "Error: unable to start Button thread"

GPIO.cleanup()
