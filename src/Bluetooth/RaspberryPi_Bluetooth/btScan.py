# -*- coding: utf-8 -*-
"""
# discover_devices() : 주변 기기들의 MAC 주소를 획득
# lookup_name() : 블루투스의 MAC 주소를 좀 더 읽기 편한 이름으로 변환
from bluetooth import *
devList = discover_devices()
for device in devList:
	name = str(lookup_name(device))
	print "[+] Found Bluetooth Device " + str(name)
	print "[+] MAC address: " + str(device)

# findDevs() : 새로 찾는 기기만 출력하도록
# alreadyFound : 이미 발견된 기기들을 저장
import time
from bluetooth import *

alreadyFound = []
def findDevs():
	foundDevs = discover_devices(lookup_names=True)
	for(addr, name) in foundDevs:
		if addr not in alreadyFound:
			print '[*] Found Bluetooth Device: ' + str(name)
			print '[+] MAC address: ' + str(addr)
			alreadyFound.append(addr)

while True:
	findDevs()
	time.sleep(5)
"""

import time
from bluetooth import *

alreadyFound = []
def findDevs():
	foundDevs = discover_devices(lookup_names=True)
	for(addr, name) in foundDevs:
		if addr not in alreadyFound:
			print '[*] Found Bluetooth Device: ' + str(name)
			print '[+] MAC address: ' + str(addr)
			alreadyFound.append(addr)
while True:
	findDevs()
	time.sleep(5)
