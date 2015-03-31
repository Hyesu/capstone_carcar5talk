###################################################
#
#   CarTalk System
#   
#   Kookmin University Capstone Project
#   Team. Carcar5talk
#   20123381 Shin Hyesu
#
###################################################

import spidev, os

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


###################### Functions ##################
def init():
	spi = spidev.SpiDev()
	spi.open(0,0)
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
	
####################### Main ######################
spi = init()
while True:
	xData = readChannel(spi, X_CHANNEL)
	yData = readChannel(spi, Y_CHANNEL)
	zData = readChannel(spi, Z_CHANNEL)

	xValue = getValue(xData, X_CHANNEL)
	yValue = getValue(yData, Y_CHANNEL)
	zValue = getValue(zData, Z_CHANNEL)

	print("{} {} {}\n".format(xValue, yValue, zValue))
