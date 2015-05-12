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
int run();
int join();
void finalize();
void* runThread(void* arg);

int thr_GPS();
int thr_DetectAccident();
int thr_Bluetooth();
int thr_Network();

int updateDirInfo(const char* oldGPS);
int getMACAddress();

int main(int argc, char** argv) {
	if(init() < 0) {
		perror("init error");
		exit(1);
	}
	if(run() < 0) {
		perror("run error");
		exit(1);
	}
	if(join() < 0) {
		perror("join error");
		exit(1);
	}
	finalize();

	return 0;
}

int init() {
	int i;

	sem_unlink(SEM_NAME_GPS);
	//sem_unlink(SEM_NAME_DA);
	//sem_unlink(SEM_NAME_NET_R);
	//sem_unlink(SEM_NAME_NET_S);
	//sem_unlink(SEM_NAME_BLUE);

	mq_unlink(MQ_NAME_GPS);
	//mq_unlink(MQ_NAME_DA);
	//mq_unlink(MQ_NAME_NET_R);
	//mq_unlink(MQ_NAME_NET_S);
	//mq_unlink(MQ_NAME_BLUE);

	semid[GPS] = getsem(SEM_NAME_GPS);
	//semid[DETECT_ACCIDENT] = getsem(SEM_NAME_DA);
	//semid[NETWORKE] = getsem(SEM_NAME_NET_R);
	//semid[NETWORK+1] = getsem(SEM_NAME_NET_S); 
	//semid[BLUETOOTH] = getsem(SEM_NAME_BLUETOOTH); 

	mqid[GPS] = getmsgq(MQ_NAME_GPS, MSG_SIZE_GPS); 
//	mqid[DETECT_ACCIDENT] = getmsgq(MQ_NAME_DA, MSG_SIZE_DA); 
//	mqid[NETWORK_RECEIVE] = getmsgq(MQ_NAME_NET_R, MSG_SIZE_NET);
//	mqid[NETWORK_SEND] = getmsgq(MQ_NAME_NET_S, MSG_SIZE_NET);
//	mqid[BLUETOOTH] = getmsgq(MQ_NAME_BLUE, MSG_SIZE_BLUE);

	if(getMACAddress() < 0) {
		perror("get MAC Address error");
		return -1;
	}

	return 0;
}
int run() {
	int i;
	for(i=0; i<NUM_THREAD; i++) {
		if(pthread_create(thrid + i, NULL, &runThread, NULL) < 0) {
			printf("create %s thread error", thrName[i]);
			return -1;
		}
	}
	return 0;
}
int join() {
	int i;
	for(i=0; i<NUM_THREAD; i++) {
		if(pthread_join(thrid[i], NULL) < 0) {
			printf("join %s thread error", thrName[i]);
			return -1;
		}
	}
	return 0;
}
void finalize() {
	rmmsgq(mqid, MQ_NAME_GPS);
//	rmmsgq(mqid, MQ_NAME_DA);
//	rmmsgq(mqid, MQ_NAME_NET_R);
//	rmmsgq(mqid, MQ_NAME_NET_S);
//	rmmsgq(mqid, MQ_NAME_BLUE);

	rmsem(SEM_NAME_GPS);
//	rmsem(SEM_NAME_DA);
//	rmsem(SEM_NAME_NET_R);
//	rmsem(SEM_NAME_NET_S);
//	rmsem(SEM_NAME_BLUE);
}
void* runThread(void* arg) {
	pthread_t id = pthread_self();

	if(pthread_equal(id, thrid[GPS])) {
		if(thr_GPS() < 0)	 	perror("GPS thread error");
	} else if(pthread_equal(id, thrid[DETECT_ACCIDENT])) {
		if(thr_DetectAccident() < 0) 	perror("Detect Accident thread error");
	} else if(pthread_equal(id, thrid[BLUETOOTH])) {
		if(thr_Bluetooth() < 0)		perror("Bluetooth thread error");
	} else if(pthread_equal(id, thrid[NETWORK])) {
		if(thr_Network() < 0)		perror("Network thread error");
	} else {
		perror("abnormal thread id");
		return NULL;
	}
}

int thr_GPS() {
	char old[MSG_SIZE_GPS];
	char new[MSG_SIZE_GPS];

	old[0] = new[0] = '\0';
	while(1) {
		sleep(INTERVAL);
		sem_wait(semid[GPS]);

		// receive message; while for newest message(last message)
		while(mq_receive(mqid[GPS], new, MSG_SIZE_GPS, NULL) > 0){
			strcpy(old, new);
		}
		sem_post(semid[GPS]);

		if(old[0] != '\0' && errno == EAGAIN) { // when no existing message in queue
			char oldGPS[LEN_GPS + 1];

			strcpy(oldGPS, myInfo.gps);
			strncpy(myInfo.gps, old, LEN_GPS);
			myInfo.gps[LEN_GPS] = '\0';
			strncpy(myInfo.speed, old + LEN_GPS, LEN_SPEED);
			myInfo.speed[LEN_SPEED] = '\0';

			updateDirInfo(oldGPS);
		}
	}

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
	latitude[10] = '\0';
	strncpy(longitude, myInfo.gps + 11, 10);
	longitude[10] = '\0';

	if(!strlen(oldGPS)) // initial vector is initial gps value
		sprintf(myInfo.dirVector, "%c%s%c%s", 
			atof(latitude) >= 0 ? '+' : '-', latitude, atof(longitude) >= 0 ? '+' : '-', longitude);
	else {
		float lat, lon;
		char oldLat[11], oldLon[11];

		strncpy(oldLat, oldGPS, 10);
		oldLat[11] = '\0';
		strncpy(oldLon, oldGPS + 11, 10);
		oldLon[11] = '\0';

		lat = atof(latitude) - atof(oldLat);
		lon = atof(longitude) - atof(oldLon);

		sprintf(myInfo.dirVector, "%c%010.5f%c%010.4f", 
			lat >= 0 ? '+' : '-', lat, lon >= 0 ? '+' : '-', lon);
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
