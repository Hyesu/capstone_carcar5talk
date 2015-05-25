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
int thr_Network_Send();
int thr_Network_Receive();

int updateDirInfo(const char* oldGPS);
int updateOtherCarInfo(const char* buf, int carIdx, CarInfo* otherInfo);
int getMACAddress();
int getMsg1(const int id, char* old, char* new, const int msgSize);
int getMsg2(const int id, char* buf, const int msgSize);
int sendMsg(const int id, const char* buf);
int makeMsgForPi(char* buf);
int makeMsgForHUD(char* buf, const int numCars, const CarInfo* otherInfo);

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

	// unlink ipc facility
	sem_unlink(SEM_NAME_GPS);
	sem_unlink(SEM_NAME_DA);
	sem_unlink(SEM_NAME_NET_R);
	sem_unlink(SEM_NAME_NET_S);
	sem_unlink(SEM_NAME_BLUE);

	mq_unlink(MQ_NAME_GPS);
	mq_unlink(MQ_NAME_DA);
	mq_unlink(MQ_NAME_NET_R);
	mq_unlink(MQ_NAME_NET_S);
	mq_unlink(MQ_NAME_BLUE);

	// make ipc facility
	semid[GPS] = getsem(SEM_NAME_GPS);
	semid[DETECT_ACCIDENT] = getsem(SEM_NAME_DA);
	semid[NETWORK_S] = getsem(SEM_NAME_NET_S);	// semaphore for my info to send
	semid[NETWORK_R] = getsem(SEM_NAME_NET_R); 	// semaphore for receiving other info
	semid[BLUETOOTH] = getsem(SEM_NAME_BLUE); 

	mqid[GPS] = getmsgq(MQ_NAME_GPS, MSG_SIZE_GPS); 
	mqid[DETECT_ACCIDENT] = getmsgq(MQ_NAME_DA, MSG_SIZE_DA); 
	mqid[NETWORK_R] = getmsgq(MQ_NAME_NET_R, MSG_SIZE_NET);
	mqid[NETWORK_S] = getmsgq(MQ_NAME_NET_S, MSG_SIZE_NET);
	mqid[BLUETOOTH] = getmsgq(MQ_NAME_BLUE, MSG_SIZE_BLUE);

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
	rmmsgq(mqid, MQ_NAME_DA);
	rmmsgq(mqid, MQ_NAME_NET_R);
	rmmsgq(mqid, MQ_NAME_NET_S);
	rmmsgq(mqid, MQ_NAME_BLUE);

	rmsem(SEM_NAME_GPS);
	rmsem(SEM_NAME_DA);
	rmsem(SEM_NAME_NET_R);
	rmsem(SEM_NAME_NET_S);
	rmsem(SEM_NAME_BLUE);
}
void* runThread(void* arg) {
	pthread_t id = pthread_self();

	if(pthread_equal(id, thrid[GPS])) {
		if(thr_GPS() < 0)	 	perror("GPS thread error");
	} else if(pthread_equal(id, thrid[DETECT_ACCIDENT])) {
		if(thr_DetectAccident() < 0) 	perror("Detect Accident thread error");
	} else if(pthread_equal(id, thrid[NETWORK_S])) { 
		if(thr_Network_Send() < 0)	perror("Network Send thread error");
	} else if(pthread_equal(id, thrid[NETWORK_R])) {
		if(thr_Network_Receive() < 0)	perror("Network Receive thread error");
	} else {
		perror("abnormal thread id");
		return NULL;
	}
}

int thr_GPS() {
	char old[MSG_SIZE_GPS];
	char new[MSG_SIZE_GPS];
	int res;

	old[0] = new[0] = '\0';
	while(1) {
		sleep(INTERVAL);
		res = getMsg1(GPS, old, new, MSG_SIZE_GPS);
		if(old[0] != '\0' && res < 0 && errno == EAGAIN) { // when no existing message in queue
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
	while(1) {
		char old[MSG_SIZE_DA];
		char new[MSG_SIZE_DA];
		int res;

		old[0] = new[0] = '\0';

		sleep(INTERVAL);
		res = getMsg1(DETECT_ACCIDENT, old, new, MSG_SIZE_DA);
		if(old[0] != '0' && res < 0 && errno == EAGAIN) {
			if(!strcmp(old, "T")) 		myInfo.flag |= 1;
			else if(myInfo.flag % 2)	myInfo.flag--;
		}
	}
	return 0;
}
int thr_Network_Send() {
	while(1) {
		char buf[MSG_SIZE_NET];
		if(makeMsgForPi(buf) < 0) {
			perror("CarTalk::thr_Network_Send: makeMsgForPi");
			return -1;
		}
		if(sendMsg(NETWORK_S, buf) < 0) {
			if(errno != EAGAIN) {
				perror("CarTalk::thr_Network_Send: sendMsg error not by full queue");
				return -1;
			}
		}
		sleep(INTERVAL);
	}
	return 0;
}
int thr_Network_Receive() {
	while(1) {
		int numCars = 0;
		// otherCars should be implement later!
		CarInfo otherCars[MAX_NUM_CARS];
		char buf[MSG_SIZE_NET];  // buf for receiving other info
		char msg[MSG_SIZE_BLUE]; // buf for bluetooth

		sleep(INTERVAL);
		while(getMsg2(NETWORK_R, buf, MSG_SIZE_NET) > 0) {
//debug
printf("CarTalk::thr_Network_Receive: getMsg2 from net_r queue buf(%s)\n", buf);
			if(updateOtherCarInfo(buf, numCars, otherCars) < 0) {
				perror("CarTalk::thr_Network_Receive: updateOtherInfo");
				return -1;
			}
			numCars++;
		}
		//if(buf[0] == '\0') continue;

		// make msg for android to display to HUD
		if(makeMsgForHUD(msg, numCars, otherCars) < 0) {
			perror("CarTalk::thr_Network_Receive: makeMsgForHUD");
			return -1;
		}
		if(sendMsg(BLUETOOTH, msg) < 0) {
			if(errno != EAGAIN) {
				perror("CarTalk::thr_Network_Receive: sendMsg(bluetooth) error not by full queue");
				return -1;
			}
		}
	}
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
int updateOtherCarInfo(const char* buf, int carIdx, CarInfo* otherCars) {
	int idx = 0;
	otherCars[carIdx].flag = buf[idx];
	idx++;

	memcpy(otherCars[carIdx].id, buf+idx, LEN_ID);
	idx += LEN_ID;

	memcpy(otherCars[carIdx].gps, buf+idx, LEN_GPS);
	idx += LEN_GPS;
	otherCars[carIdx].gps[LEN_GPS] = '\0';

	memcpy(otherCars[carIdx].speed, buf+idx, LEN_SPEED);
	idx += LEN_SPEED;
	otherCars[carIdx].speed[LEN_SPEED] = '\0';

	// check direction whether other car's direction is equal to me or not
	// not implemented yet. currently default yes!
	otherCars[carIdx].flag |= 2;

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
int getMsg1(const int id, char* old, char* new, const int msgSize) {
	int res;

	sem_wait(semid[id]);
	// receive message; while for newest message(last message)
	while((res = mq_receive(mqid[id], new, msgSize, NULL)) > 0){
		strcpy(old, new);
	}
	sem_post(semid[id]);
	return res;
}
int getMsg2(const int id, char* buf, const int msgSize) {
	int res;
	
	sem_wait(semid[id]);
	res = mq_receive(mqid[id], buf, msgSize, NULL);
	sem_post(semid[id]);
	return res;
}
int sendMsg(const int id, const char* buf) {
	int res;

	sem_wait(semid[id]);
	res = (int) mq_send(mqid[id], buf, strlen(buf), 0);
	sem_post(semid[id]);
	return res;
}
int makeMsgForPi(char* buf) {
	int idx=0;

	// set flag	
	buf[idx] = myInfo.flag;
	idx++;

	// set MAC Address
	memcpy(buf+idx, myInfo.id, LEN_ID);
	idx += LEN_ID;

	// set GPS
	memcpy(buf+idx, myInfo.gps, LEN_GPS);
	idx += LEN_GPS;

	// set speed
	memcpy(buf+idx, myInfo.speed, LEN_SPEED);
	idx += LEN_SPEED;

	// set vector
	memcpy(buf+idx, myInfo.dirVector, LEN_GPS);
	idx += LEN_GPS;

	return 0;
}
int makeMsgForHUD(char* buf, const int numCars, const CarInfo* otherInfo) {
	int idx = 0;
	int i;
	
	//set flag. now not used
	idx++;

	// set my gps
	memcpy(buf+idx, myInfo.gps, LEN_GPS);
	idx += LEN_GPS;

	// set my speed
	memcpy(buf+idx, myInfo.speed, LEN_SPEED);
	idx += LEN_SPEED;

	memcpy(buf+idx, myInfo.dirVector, LEN_GPS);
	idx += LEN_GPS;
	
	// set number of other cars
	buf[idx] = (char)numCars;
	idx++;

	// set other cars info 
	for(i=0; i<numCars; i++) {
		// set id of other car
		memcpy(buf+idx, otherInfo[i].id, LEN_ID);
		idx += LEN_ID;

		// set flag of other car
		buf[idx] = otherInfo[i].flag;
		idx++;

		// set gps of other car
		memcpy(buf+idx, otherInfo[i].gps, LEN_GPS);
		idx += LEN_GPS;

		// set speed of other car
		memcpy(buf+idx, otherInfo[i].speed, LEN_SPEED);
		idx += LEN_SPEED;
	}
	return 0;
}
