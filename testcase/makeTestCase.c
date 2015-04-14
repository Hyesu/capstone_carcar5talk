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
#include <string.h>
#include <unistd.h>
#include <fcntl.h>

#define R2A_SIZE	4096
#define BUF_SIZE	32

int main(int argc, char** argv) {
	int binFile;
	char prot[R2A_SIZE];
	char buf[BUF_SIZE];
	int numCar;
	int idx = 0;
	int i;

	// skip flag
	prot[idx] = (char)0;
	idx++;

	// get my GPS	
	scanf("%23s", buf);
	memcpy(prot+idx, buf, 22);
	idx += 22;

	// get my speed
	scanf("%6s", buf);
	memcpy(prot+idx, buf, 6);
	idx+=6;

	// get number of cars
	scanf("%d", &numCar);
	prot[idx] = (char)numCar;
	idx++;

	for(i=0; i<numCar; i++) {
		int flag;
		int j;

		// get ID of other cars
		prot[idx] = (char)i;
		idx++;

		for(j=0; j<5; j++) prot[idx++] = (char)0;

		// get flag of other cars
		scanf("%d", &flag);
		prot[idx] = (char)flag;
		idx++;

		// get GPS of other cars
		scanf("%23s", buf);
		memcpy(prot+idx, buf, 22);
		idx += 22;
		
		// get speed of other cars
		scanf("%6s", buf);
		memcpy(prot+idx, buf, 6);
		idx+=6;
		
	}

	for(i=idx; i<R2A_SIZE; i++) prot[i] = (char)0;
	
	if((binFile = open(argv[1], O_WRONLY | O_CREAT)) < 0) {
		perror("open binary file is error");
		exit(0);
	}

	if(write(binFile, prot, R2A_SIZE) < R2A_SIZE) {
		perror("write error");
		exit(0);
	}
	
	close(binFile);


//read section

	if((binFile = open(argv[1], O_RDONLY)) < 0) {
		perror("read open binary file is error");
		exit(0);
	}
	if(read(binFile, buf, 23) < 23) {
		perror("read error");
		exit(0);
	}
	buf[23] = '\0';

printf("gps: %s\n",buf + 1);

	close(binFile);
	return 0;
}
