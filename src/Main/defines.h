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
#include <sys/sem.h>
#include <errno.h>

#define MAC_FILE	"/sys/class/net/eth0/address"
#define LEN_MAC		18

#define NUM_THREAD	4			// for 4 modules: GPS, DetectAccident, Bluetooth, Network 
#define THREAD_GPS	0
#define THREAD_ACCI	1
#define THREAD_BLUE	2
#define THREAD_NET	3

#define LEN_ID		6
#define LEN_GPS		22
#define LEN_SPEED	6

typedef struct carInfo {
	char id[LEN_ID];
	char flag; // for one byte
	char gps[LEN_GPS + 1];
	char speed[LEN_SPEED + 1];
	char dirVector[LEN_GPS + 1];
} CarInfo;

static pthread_t thrid[NUM_THREAD];
static char* thrName[] = {"GPS", "Detect Accident", "Bluetooth", "Network"};
static CarInfo myInfo;

static struct sembuf p_GPS  = {THREAD_GPS,  -1, 0}; 
static struct sembuf p_Acci = {THREAD_ACCI, -1, 0};
static struct sembuf p_Blue = {THREAD_BLUE, -1, 0};
static struct sembuf p_Net  = {THREAD_NET,  -1, 0};

static struct sembuf v_GPS  = {THREAD_GPS,  1, 0}; 
static struct sembuf v_Acci = {THREAD_ACCI, 1, 0};
static struct sembuf v_Blue = {THREAD_BLUE, 1, 0};
static struct sembuf v_Net  = {THREAD_NET,  1, 0};

static int semid = 0;
static mqd_t mqid = (mqd_t) 0;

#endif
