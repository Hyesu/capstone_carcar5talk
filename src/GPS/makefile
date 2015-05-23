gps: getGPSvalue.o
	gcc -o gps getGPSvalue.o -L../IPC -lipc -lpthread -lrt
getGPSvalue.o: getGPSvalue.c
	gcc -c getGPSvalue.c

clean:
	rm -f *.o gps

