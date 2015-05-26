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
#include <semaphore.h>
#include <mqueue.h>
#include <wiringPi.h>
#include <errno.h>

////////////////////////////////////////
// constants for getting gps value
////////////////////////////////////////
#define GPS_FILE	"/home/pi/capstone_carcar5talk/src/GPS/log/dummy"
#define GPS_DUMMY_LEN	59

#define DEVICE 		"/dev/ttyAMA0"
#define BUFSIZE		8		// buffer size for getting gps value
#define GPRMC		128
#define PROTLEN		51

//#define GPS_TIME	7
//#define GPS_VALID	18
//#define GPS_LATITUDE	20
//#define GPS_LATCHAR	30
//#define GPS_LONGITUDE	32
//#define GPS_LONCHAR	43
//#define GPS_SPEED	45

#define GPS_TIME	7
#define GPS_VALID	18
#define GPS_LATITUDE	20
#define GPS_LATCHAR	32
#define GPS_LONGITUDE	34
#define GPS_LONCHAR	47
#define GPS_SPEED	49

#define TIMELEN		10
#define LATILEN		9
#define LONGILEN	10

#define LEN_GPS		22
#define LEN_SPEED	6


////////////////////////////////////////
// constants for ipc
////////////////////////////////////////
#define SEM_NAME	"/CarTalk_sem_gps"
#define MSGQ_NAME	"/CarTalk_mq_gps"
#define MSG_SIZE	(LEN_GPS+LEN_SPEED+1)

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

////////////////////////////////////////
// static variables 
////////////////////////////////////////
static char gpsProtocol[] = "$GPR";
static mqd_t mqid;
static sem_t* semid;

#endif
