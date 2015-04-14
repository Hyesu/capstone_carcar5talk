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
#include <fcntl.h>
#include <string.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#include <sys/stat.h>
#include <mqueue.h>
#include <pthread.h>

#define MSGQ_NAME	"CarTalk_MsgQ"
#define MSGQ_PERM	0644
#define MAX_MSG		1024
#define MSG_SIZE	4096

#define SEM_KEY		(key_t) 0x81
#define SEM_PERM	0600
#define SEM_FLAG	IPC_CREAT | IPC_EXCL
#define NUM_SEM		4

#define NUM_THREAD	4			// for 4 modules: GPS, DetectAccident, Bluetooth, Network 
#define THREAD_GPS	0
#define THREAD_ACCI	1
#define THREAD_BLUE	2
#define THREAD_NET	3

#define LEN_ID		6
#define LEN_GPS		22
#define LEN_SPEED	6


typedef union _semun {
	int val;
	struct semid_ds* buf;
	ushort* array;
} semun;
typedef struct carInfo {
	char id[LEN_ID];
	char flag; // for one byte
	char gps[LEN_GPS + 1];
	char speed[LEN_SPEED + 1];
	char dirVector[LEN_GPS + 1];
} CarInfo;

struct sembuf p_GPS  = {THREAD_GPS,  -1, 0}; 
struct sembuf p_Acci = {THREAD_ACCI, -1, 0};
struct sembuf p_Blue = {THREAD_BLUE, -1, 0};
struct sembuf p_Net  = {THREAD_NET,  -1, 0};

struct sembuf v_GPS  = {THREAD_GPS,  1, 0}; 
struct sembuf v_Acci = {THREAD_ACCI, 1, 0};
struct sembuf v_Blue = {THREAD_BLUE, 1, 0};
struct sembuf v_Net  = {THREAD_NET,  1, 0};

static pthread_t thrid[NUM_THREAD];
static char thrName[][] = {"GPS", "Detect Accident", "Bluetooth", "Network"};
static CarInfo myInfo;
static int semid;
static mqd_t mqid;

#endif
