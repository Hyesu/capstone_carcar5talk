/**************************************************
*
*   CarTalk System
*   
*   Kookmin University Capstone Project
*   Team. Carcar5talk
*   20123381 Shin Hyesu
*
**************************************************/
#include "defines.h"

int init(int* gps, struct termios* options, GPSValue* gpsAry1, GPSValue* gpsAry2);
int getGPSvalue(const int gps, unsigned char* gprmc);
int extractGPSvalue(const unsigned char* gprmc);
float knot2kmhr(float knot);
void printValid(char isValid);

int main(int argc, char** argv) {
	int gps;
	struct termios options;
	unsigned char gprmc[GPRMC];
	GPSValue *gpsAry1, *gpsAry2;

	if(init(&gps, &options, gpsAry1, gpsAry2) < 0) {
		perror("init error");
		exit(0);
	}

	while(1) {
		strcpy(gprmc, "\0");
		if(getGPSvalue(gps, gprmc) < 0) {
			perror("get GPS value error");
			exit(0);
		}
		if(extractGPSvalue(gprmc) < 0) {
			perror("get GPS value error");
			exit(0);
		}
	}
	close(gps);
	return 0;
}

int init(int* gps, struct termios* options, GPSValue* gpsAry1, GPSValue* gpsAry2) {
	if((*gps = open(DEVICE, O_RDWR | O_NOCTTY)) < 0) {
		perror("uart serial port open error");
		return -1;
	}

	tcgetattr(*gps, options);
	options->c_cflag = B9600 | CS8 | CLOCAL | CREAD;
	options->c_iflag = IGNPAR;
	options->c_oflag = 0;
	options->c_lflag = 0;
	tcflush(*gps, TCIFLUSH);
	tcsetattr(*gps, TCSANOW, options);

	wiringPiSetup();
	pinMode(LED_RED, OUTPUT);
	pinMode(LED_GREEN, OUTPUT);

	return 0;
}
int getGPSvalue(const int gps, unsigned char* gprmc) {
	unsigned char buf[BUFSIZE];
	int loop = GPRMC / BUFSIZE;
	int length, i;

	for(i=0; i<loop; i++) {
		if((length = read(gps, (void*)buf, BUFSIZE)) < 0) {
			perror("read error");
			return -1;
		}
		buf[length] = '\0';
		strcat(gprmc, buf);
	}

	return 0;
}
int extractGPSvalue(const unsigned char* gprmc) {
	char* prot;
	GPSValue gv;
	char temp[10];
	int i;

	if(((prot = strstr(gprmc, "$GPRMC")) != NULL) && strlen(prot) >= PROTLEN) {
printf("prot(%s)\n", prot);
//		if(prot[GPS_VALID] == 'A') {
			strncpy(gv.time, prot + GPS_TIME, TIMELEN);
			gv.time[TIMELEN] = '\0';
			
			strncpy(temp, prot + GPS_LATITUDE, LATILEN);
			gv.latitude = atof(temp);
			gv.latAxis = prot[GPS_LATCHAR];
			
			strncpy(temp, prot + GPS_LONGITUDE, LONGILEN);
			gv.longitude = atof(temp);
			gv.lonAxis = prot[GPS_LONCHAR];

			i = 0;
			do {
				temp[i] = prot[GPS_SPEED + i];
				i++;
			} while(prot[GPS_SPEED + i] != ',');
			temp[i] = '\0';
			gv.speed = knot2kmhr(atof(temp));

			printValid(prot[GPS_VALID]);

printf("GPSvalue: time(%s), lat(%f)%c, lon(%f)%c, speed(%f)\n", gv.time, gv.latitude, gv.latAxis, gv.longitude, gv.lonAxis, gv.speed);
//		}	
		
	}
	return 0;
}
float knot2kmhr(float knot) {
	return knot * 1.852;
}
void printValid(char isValid) {
	if(isValid == 'V') {
		digitalWrite(LED_RED, HIGH);
		digitalWrite(LED_GREEN, LOW);
	} else {
		digitalWrite(LED_GREEN, HIGH);
		digitalWrite(LED_RED, LOW);
	}
}
