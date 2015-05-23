/*
20103346 ParkHoon
Hw1. UDP Server&Client
client.c

argv[1] : Server Port Number
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

#define BUFSIZE 128 

void main(int argc, char **argv)
{
   int sockfd;
   struct sockaddr_in servAddr;
   struct sockaddr_in clntAddr;
   char recvBuffer[BUFSIZE];
   int clntLen;
   int recvLen;
   struct hostent *ht;
   char *host = "localhost";
   char *udp_port = NULL;
   char *msg = NULL;
   
// socket create
   if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) == -1)
   {
       perror("sock create error");
       exit(1);
   }
   
   host = "localhost";
   udp_port = argv[1];

   servAddr.sin_family = AF_INET;
   servAddr.sin_port   = htons((u_short)atoi(udp_port));
   servAddr.sin_addr.s_addr = htonl(INADDR_ANY);

   //ht = gethostbyname(host);
   //memcpy((char *)&servAddr.sin_addr.s_addr, ht->
   // bind
   if (bind(sockfd, (struct sockaddr *)&servAddr, sizeof(servAddr)) == -1)
   {
      perror("bind fail");
      exit(1);
   }

   while (1)
   {
       // client recv
       clntLen = sizeof(clntAddr);
       if ((recvLen = recvfrom(sockfd, recvBuffer, BUFSIZE-1, 0, (struct sockaddr *)&clntAddr,
                &clntLen)) == -1)
       {
           perror("recvFrom failed");
           exit(1);
       }
       recvBuffer[recvLen] = '\0';

       printf("%s\n", recvBuffer);

       // send
       if (sendto(sockfd, recvBuffer, recvLen, 0, (struct sockaddr *)&clntAddr, sizeof(clntAddr))
                != recvLen)
       {
           perror("sendto fail");
           exit(1);
       }

   }
   close(sockfd);
   exit(1);
}

