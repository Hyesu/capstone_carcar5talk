/**************************************************
*
*   CarTalk System
*   
*   Kookmin University Capstone Project
*   Team. Carcar5talk
*   20123381 Shin Hyesu
*
**************************************************/
#ifndef __DEFINES_H__
#define __DEFINES_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <mqueue.h>
#include <semaphore.h>
#include <errno.h>

#define MAC_FILE	"/sys/class/net/eth0/address"
#define LEN_MAC		18

#define NUM_THREAD	4			// for 4 modules: GPS, DetectAccident, Bluetooth, Network 
#define GPS		0
#define DETECT_ACCIDENT	1
#define NETWORK_R	2
#define NETWORK_S	3
#define BLUETOOTH	4

#define LEN_ID		17
#define LEN_GPS		22
#define LEN_SPEED	6
#define LEN_BYTE	3

#define MQ_NAME_GPS	"/CarTalk_mq_gps"
#define MQ_NAME_DA	"/CarTalk_mq_da"
#define MQ_NAME_NET_R	"/CarTalk_mq_net_r"
#define MQ_NAME_NET_S	"/CarTalk_mq_net_s"
#define MQ_NAME_BLUE	"/CarTalk_mq_blue"

#define SEM_NAME_GPS	"/CarTalk_sem_gps"
#define SEM_NAME_DA	"/CarTalk_sem_da"
#define SEM_NAME_NET_R	"/CarTalk_sem_net_r"
#define SEM_NAME_NET_S	"/CarTalk_sem_net_s"
#define SEM_NAME_BLUE	"/CarTalk_sem_blue"

#define MSG_SIZE_GPS	29 			// "ddmm.mmmmmNdddmm.mmmmEsss.ss"
#define MSG_SIZE_DA	2			// "T" / "F"
#define MSG_SIZE_NET	64
#define MSG_SIZE_BLUE	4096

#define INTERVAL	1			// seconds
#define MAX_NUM_CARS	10

typedef struct carInfo {
	char id[LEN_ID+1];			// xx:xx:xx:xx:xx:xx
	char flag; 				// for one byte. LSB is used for "isAccident"
	char gps[LEN_GPS + 1];
	char speed[LEN_SPEED + 1];
	char dirVector[LEN_GPS + 1];
} CarInfo;
static pthread_t thrid[NUM_THREAD]; 
static char* thrName[] = {"GPS", "Detect Accident", "Network_receive", "Network_send"};
static CarInfo myInfo;

static sem_t* semid[NUM_THREAD+1];
static mqd_t mqid[NUM_THREAD+1];

#endif
