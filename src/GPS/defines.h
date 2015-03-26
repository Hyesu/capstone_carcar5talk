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
#include <termios.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>

////////////////////////////////////////
// constants for getting gps value
////////////////////////////////////////
#define DEVICE 		"/dev/ttyAMA0"
#define BUFSIZE		8		// buffer size for getting gps value
#define GPRMC		128
#define PROTLEN		51

#define GPS_TIME	7
#define GPS_VALID	18
#define GPS_LATITUDE	20
#define GPS_LATCHAR	30
#define GPS_LONGITUDE	32
#define GPS_LONCHAR	43
#define GPS_SPEED	45

#define TIMELEN		10
#define LATILEN		9
#define LONGILEN	10

////////////////////////////////////////
// constants for ipc & synchronization
////////////////////////////////////////
#define SHMKEY1	(key_t)0x10
#define SHMKEY2	(key_t)0x15
#define SEMKEY	(key_t)0x20
#define NUMSEM	2
#define IPCPERM	0600		// can read and write using shared memory
#define IPCFLAG	IPC_CREAT | IPC_EXCL

////////////////////////////////////////
// constants for amending gps value
////////////////////////////////////////
#define ENTRY	8		// number of gps value entries for amending value. 

////////////////////////////////////////
// sturctures 
////////////////////////////////////////
typedef struct gpsValue {
	char time[TIMELEN];	// hhmmss.sss
	float latitude;
	char latAxis;		// N(North) or S(South)
	float longitude;	
	char lonAxis;		// E(East) or W(West)
	float speed;
} GPSValue;
typedef union Semun {
	int val;
	struct semid_ds *buf;
	ushort *array;
} semun;

struct sembuf p1 = {0, -1, 0}, p2 = {1, -1, 0};
struct sembuf v1 = {0,  1, 0}, v2 = {1,  1, 0};

////////////////////////////////////////
// static variables 
////////////////////////////////////////
static int shmid1, shmid2, semid;
static char gpsProtocol[] = "$GPR";

#endif
