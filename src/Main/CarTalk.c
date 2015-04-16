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

int init();
void* runThread(void* arg);

int thr_GPS();
int thr_DetectAccident();
int thr_Bluetooth();
int thr_Network();

int updateDirInfo(const char* oldGPS);
int getMACAddress();

int main(int argc, char** argv) {
	int i;

	if(init() < 0) {
		perror("init error");
		exit(1);
	}
	for(i=0; i<NUM_THREAD; i++) {
		if(pthread_create(thrid + i, NULL, &runThread, NULL) < 0) {
			printf("create %s thread error", thrName[i]);
		}
	}

	for(i=0; i<NUM_THREAD; i++) {
		if(pthread_join(thrid[i], NULL) < 0) {
			printf("join %s thread error", thrName[i]);
		}
	}

	rmmsgq(MSGQ_NAME);
	rmsem(SEM_NAME_GPS);
	rmsem(SEM_NAME_ACCI);
	rmsem(SEM_NAME_BLUE);
	rmsem(SEM_NAME_NET);
	return 0;
}

int init() {
	int i;
	semid_gps = getsem(SEM_NAME_GPS);
	semid_acci = getsem(SEM_NAME_ACCI);
	semid_blue = getsem(SEM_NAME_BLUE);
	semid_net = getsem(SEM_NAME_NET);
	mqid = getmsgq(MSGQ_NAME);
	if(getMACAddress() < 0) {
		perror("get MAC Address error");
		return -1;
	}

	return 0;
}
void* runThread(void* arg) {
	pthread_t id = pthread_self();

	if(pthread_equal(id, thrid[THREAD_GPS])) {
		if(thr_GPS() < 0)	 	perror("GPS thread error");
	} else if(pthread_equal(id, thrid[THREAD_ACCI])) {
		if(thr_DetectAccident() < 0) 	perror("Detect Accident thread error");
	} else if(pthread_equal(id, thrid[THREAD_BLUE])) {
		if(thr_Bluetooth() < 0)		perror("Bluetooth thread error");
	} else if(pthread_equal(id, thrid[THREAD_NET])) {
		if(thr_Network() < 0)		perror("Network thread error");
	} else {
		perror("abnormal thread id");
		return NULL;
	}
}

int thr_GPS() {
	char buf[LEN_GPS+LEN_SPEED+1];
	int priority = THREAD_GPS;

	sem_wait(semid_gps);
	while(mq_receive(mqid, buf, LEN_GPS + LEN_SPEED + 1, &priority) > 0);
	if(errno == EAGAIN) {
		char oldGPS[LEN_GPS + 1];
		strcpy(oldGPS, myInfo.gps);

		strncpy(myInfo.gps, buf, LEN_GPS);
		myInfo.gps[LEN_GPS] = '\0';
		strncpy(myInfo.speed, buf + LEN_GPS, LEN_SPEED);
		myInfo.speed[LEN_SPEED] = '\0';

		updateDirInfo(oldGPS);
	}
	sem_post(semid_gps);

	return 0;
}
int thr_DetectAccident() {
	return 0;
}
int thr_Bluetooth()  {
	return 0;
}
int thr_Network() {
	return 0;
}

int updateDirInfo(const char* oldGPS) {
	char latitude[11];
	char longitude[11];

	strncpy(latitude, myInfo.gps, 10);
	latitude[11] = '\0';
	strncpy(longitude, myInfo.gps + 11, 10);
	longitude[11] = '\0';

	if(!strlen(oldGPS))
		sprintf(myInfo.dirVector, "%c%s%c%s", atof(latitude) >= 0 ? '+' : '-', latitude, atof(longitude) >= 0 ? '+' : '-', longitude);
	else {
		float lat, lon;
		char oldLat[11], oldLon[11];

		strncpy(oldLat, oldGPS, 10);
		oldLat[11] = '\0';
		strncpy(oldLon, oldGPS + 11, 10);
		oldLon[11] = '\0';

		lat = atof(latitude) - atof(oldLat);
		lon = atof(longitude) - atof(oldLon);

		sprintf(myInfo.dirVector, "%c%10.5f%c%10.4f", lat >= 0 ? '+' : '-', lat, lon >= 0 ? '+' : '-', lon);
	}

	return 0;
}
int getMACAddress() {
	FILE* macFile;
	char macAddr[LEN_MAC];
	int i, j;

	if((macFile = fopen(MAC_FILE, "r")) == NULL) {
		perror("MAC_FILE open error");
		return -1;
	}
	
	if(fscanf(macFile, "%s", macAddr) < 0) {
		perror("MAC_FILE read error");
		return -1;
	}

	for(i=0, j=0; i<LEN_ID; i++) {
		char hexa[3];
		int hex;
		hexa[0] = macAddr[j++];
		hexa[1] = macAddr[j++];
		hexa[2] = '\0', j++;

		sscanf(hexa, "%x", &hex);
		myInfo.id[i] = (char) hex;
	}

	fclose(macFile);
}
