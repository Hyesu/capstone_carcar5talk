CarTalk: CarTalk.o
	gcc -o CarTalk CarTalk.o -lpthread -lrt -L../IPC -lipc
CarTalk.o: CarTalk.c
	gcc -c CarTalk.c

clean:
	rm -f CarTalk *.o

