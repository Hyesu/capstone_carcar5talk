/* 20103346 Park Hoon
Hw1. UDP Server&Client 
server.c

argv[1] : Client Ip Address
argv[2] : Port Number
argv[3] : ms
*/

#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <stdlib.h>
#include <time.h>
#define BUFSIZE 128 

void main(int argc, char **argv){
	
	int sockfd;
	struct sockaddr_in servAddr;
	char sendBuffer[BUFSIZE], recvBuffer[BUFSIZE];
	int recvLen, servLen;
	struct hostent *ht;
	char *host = "localhost";
	char *udp_port = NULL;
        char *msec = "20000"; 
	int packetNum = 0;
	char buf[128] ;
	char time[128] ;
	struct timespec ts_start;
        int period = 0; 

	// sock create
	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) == -1){
		perror("sock create error");
		exit(1);
	}
	host = argv[1];  
	udp_port = argv[2];
        msec = argv[3];

	// addr create
	servAddr.sin_family = AF_INET;
	servAddr.sin_port = htons((u_short)atoi(argv[2]));

	ht = gethostbyname(host);
	memcpy((char *)&servAddr.sin_addr.s_addr, ht->h_addr_list[0], ht->h_length);
	period = atoi(msec);
        period = period * 1000 ; 
	// loop
	while(1){
		clock_gettime(CLOCK_MONOTONIC, &ts_start);

		sprintf(buf, "%d  ",packetNum++);
		sprintf(time,"%ld",ts_start.tv_nsec); 

		printf( "PacketNumber : %s, Time : %s\n",buf, time); 
		strcat(buf,time);
		usleep(period);
		
		// send
		if( sendto(sockfd , buf, strlen(buf), 0, (struct sockaddr *)&servAddr, sizeof(servAddr)) != strlen(buf)){
			perror("sendto fail");
			exit(1);
		}
	}
	close(sockfd);
	exit(0);
}


