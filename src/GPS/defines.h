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
#include <wiringPi.h>

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
// constants for physical output
////////////////////////////////////////
#define LED_RED		4
#define LED_GREEN	5

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

#endif
