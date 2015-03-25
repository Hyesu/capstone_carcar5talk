/**************************************************
*
*   CarTalk System
*   
*   Kookmin University Capstone Project
*   Team. Carcar5talk
*   20123381 Shin Hyesu
*
**************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <termios.h>

#define DEVICE 	"/dev/ttyAMA0"
#define BUFSIZE	8

int main(int argc, char** argv) {
	int gps = -1;
	struct termios options;
	unsigned char buf[BUFSIZE];
	int length;

	if((gps = open(DEVICE, O_RDWR | O_NOCTTY)) < 0) {
		perror("uart serial port open error");
		exit(0);
	}

	tcgetattr(gps, &options);
	options.c_cflag = B9600 | CS8 | CLOCAL | CREAD;
	options.c_iflag = IGNPAR;
	options.c_oflag = 0;
	options.c_lflag = 0;
	tcflush(gps, TCIFLUSH);
	tcsetattr(gps, TCSANOW, &options);

	while(1) {
		if((length = read(gps, (void*)buf, BUFSIZE)) < 0) {
			perror("read error");
			exit(0);
		}
		buf[length] = '\0';
		printf("data(%d): %s\n", length, buf);
	}
	close(gps);
	return 0;
}
