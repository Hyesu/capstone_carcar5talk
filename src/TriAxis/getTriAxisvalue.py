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

#################### Constants ####################
COEFF = [0.001861, 0.004444, 0.007667]
CONST = [1.65, 1.65, 2.31]
MIN = [0, -180, -90]
MAX = [360, 180, 90]

X_CHANNEL = 0
Y_CHANNEL = 1
Z_CHANNEL = 2

POWER_VOLT = 3.3
CONV_CONST = 1023

COLLISION_THR = 10
ROLLOVER_THR = 90
ROLLOVER_MAX = 180
COLLISION_COEF = 0.19
ROLLOVER_COEF = 45.535

DELAY = 0.5

LED_YELLOW = 18

###################### Functions ##################
def init():
	spi = spidev.SpiDev()
	spi.open(0,0)
	
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(LED_YELLOW, GPIO.OUT)
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

def isCollision(rVector, mVector):
	aVector = [mVector[0]-rVector[0], mVector[1]-rVector[1], mVector[2]-rVector[2]]
	aCrash = pow(aVector[0], 2) + pow(aVector[1], 2) + pow(aVector[2], 2)
	aCrash = math.sqrt(aCrash)
	
	if aCrash >= -COLLISION_THR and aCrash <= COLLISION_THR:
		return False
	else:
		GPIO.output(LED_YELLOW, True)
		time.sleep(DELAY)
		GPIO.output(LED_YELLOW, False)
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
		GPIO.output(LED_YELLOW, True)
		time.sleep(DELAY)
		GPIO.output(LED_YELLOW, False)
		return True
	else:
		return False

	
####################### Main ######################
spi = init()
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

	time.sleep(DELAY)
GPIO.cleanup()
