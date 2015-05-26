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
int init2(FILE* gps);
int init3(int* gps);
void finalize(int gps);

int getGPSvalue(const int gps, unsigned char* gprmc);
int getGPSvalue2(FILE* gps2, char* gprmc);
int getGPSvalue3(int gps, char* gprmc);
int extractGPSvalue(const unsigned char* gprmc);
int sendGPSvalue(const GPSValue gv);

float knot2kmhr(float knot);

int main(int argc, char** argv) {
	int gps;
	struct termios options;
//	unsigned char gprmc[GPRMC];
	char gprmc[GPRMC];
	GPSValue *gpsAry1, *gpsAry2;


	if(init(&gps, &options, gpsAry1, gpsAry2) < 0) {
		perror("GPS:: init error");
		exit(1);
	}

	init3(&gps);


	while(1) {
		strcpy(gprmc, "\0");
//		if(getGPSvalue(gps, gprmc) < 0) {
//			perror("get GPS value error");
//			exit(1);
//		}
		if(getGPSvalue3(gps, gprmc) < 0) {
			perror("GPS:: getGPSvalue3");
			exit(1);
		}
		if(extractGPSvalue(gprmc) < 0) {
			perror("get GPS value error");
			exit(1);
		}
	}

	finalize(gps);
	return 0;
}

int init(int* gps, struct termios* options, GPSValue* gpsAry1, GPSValue* gpsAry2) {
//	if((*gps = open(DEVICE, O_RDWR | O_NOCTTY)) < 0) {
//		perror("uart serial port open error");
//		return -1;
//	}


//	tcgetattr(*gps, options);
//	options->c_cflag = B9600 | CS8 | CLOCAL | CREAD;
//	options->c_iflag = IGNPAR;
//	options->c_oflag = 0;
//	options->c_lflag = 0;
//	tcflush(*gps, TCIFLUSH);
//	tcsetattr(*gps, TCSANOW, options);

	mqid = getmsgq(MSGQ_NAME, MSG_SIZE);
	semid = getsem(SEM_NAME);

	return 0;
}
int init2(FILE* gps2) {
	if((gps2 = fopen(GPS_FILE, "r")) == NULL) {
		perror("GPS::init2: gps file open error");
		return -1;
	}
	return 0;

}
int init3(int* gps) {
	if((*gps = open(GPS_FILE, O_RDONLY)) < 0) {
		perror("GPS::init3: gps file open error");
		return -1;
	}
	return 0;
}
void finalize(int gps) {
	close(gps);
	rmmsgq(MSGQ_NAME);
	rmsem(SEM_NAME);
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
int getGPSvalue2(FILE* gps, char* gprmc) {
printf("before fgets\n");

	if((fgets(gprmc, GPRMC, gps)) == NULL) {
		perror("GPS::getGPSvalue2: fgets fail");
		return -1;
	}

	if(feof(gps)) {
		fclose(gps);
		if((gps = fopen(GPS_FILE, "r")) == NULL) {
			perror("GPS::getGPSvalue2: fopen");
			return -1;
		}
	}
	return 0;
}
int getGPSvalue3(int gps, char* gprmc) {
	int nread;
	sleep(INTERVAL);

	if((nread = read(gps, gprmc, GPS_DUMMY_LEN)) < 0) {
		perror("GPS::getGPSvalue3: read");
		return -1;
	}
	gprmc[nread] = '\0';

	if(!nread)
		lseek(gps, 0, SEEK_SET); 

	return 0;
}
int extractGPSvalue(const unsigned char* gprmc) {
	char* prot;
	GPSValue gv;
	char temp[15];
	int i;

	if(((prot = strstr(gprmc, "$GPRMC")) != NULL) && strlen(prot) >= PROTLEN) {
//		if(prot[GPS_VALID] == 'A') { // for getting only valid value
			char buf[LEN_GPS + LEN_SPEED + 1];

			strncpy(gv.time, prot + GPS_TIME, TIMELEN);
			gv.time[TIMELEN] = '\0';

			strncpy(temp, prot + GPS_LATITUDE, LATILEN);
			temp[LATILEN] = '\0';
			gv.latitude = atof(temp);
			gv.latAxis = prot[GPS_LATCHAR];

			strncpy(temp, prot + GPS_LONGITUDE, LONGILEN);
			temp[LONGILEN] = '\0';
			gv.longitude = atof(temp);
			gv.lonAxis = prot[GPS_LONCHAR];

			i = 0;
			do {
				temp[i] = prot[GPS_SPEED + i];
				i++;
			} while(prot[GPS_SPEED + i] != ',');
			temp[i] = '\0';
//debug
			gv.speed = atof(temp);
			//gv.speed = knot2kmhr(atof(temp));


			// print value for log
//			printf("GPSvalue: time(%s), lat(%f)%c, lon(%f)%c, speed(%f)\n", 
//				gv.time, gv.latitude, gv.latAxis, gv.longitude, gv.lonAxis, gv.speed);

			if(sendGPSvalue(gv) < 0) {
				perror("sendGPSvalue");
				return -1;
			}
//		}	
		
	}
	return 0;
}
int sendGPSvalue(const GPSValue gv) {
	char buf[MSG_SIZE];
	int result;

	// send message to queue
	sprintf(buf, "%010.5f%c%010.4f%c%06.2f", 
		gv.latitude, gv.latAxis, gv.longitude, gv.lonAxis, gv.speed);

	sem_wait(semid);
	result = mq_send(mqid, buf, strlen(buf), 0);
	sem_post(semid);

//debug
printf("GPS::sendGPSvalue: buf(%s) - gps send\n", buf);

	if(result < 0 && errno != EAGAIN)	return -1;
	else					return  0;
}
float knot2kmhr(float knot) {
	return knot * 1.852;
}
