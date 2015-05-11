gps: getGPSvalue.o
	gcc -o gps getGPSvalue.o -l wiringPi -L../IPC -lipc -lpthread -lrt
getGPSvalue.o: getGPSvalue.c
	gcc -c getGPSvalue.c -l wiringPi

clean:
	rm -f *.o gps

