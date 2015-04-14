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

int getsem();
int getmsgq();
int rmipc();

int init();
void* runThread(void* arg);

int thr_GPS();
int thr_DetectAccident();
int thr_Bluetooth();
int thr_Network();

int main(int argc, char** argv) {
	int i;

	if(init() < 0) {
		perror("init error");
		exit(1);
	}
	for(i=0; i<NUM_THREAD; i++) {
		if(pthread_create(thrid + i, NULL, &runThread, NULL) < 0) {
			perror("create %s thread error", thrName[i]);
		}
	}

	rmipc();
	return 0;
}

int init() {
	semid = getsem();
	mqid = getmsgq();

	// debug: init myInfo.id from RPi's MAC address
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
		if(thr_Netword() < 0)		perror("Network thread error");
	} else {
		perror("abnormal thread id");
		return NULL;
	}
}


int getsem() {
	semun x;
	int id
	int i;

	x.val = 1;
	if((id = semget(SEM_KEY, NUM_SEM, SEM_PERM | SEM_FLAG)) == -1) {
		perror("semget error");
		return -1;
	}
	for(i=0; i<NUM_SEM; i++) {
		if(semctl(id, i, SETVAL, x) == -1) {
			perror("semctl: set initial value for semaphore for %s error", thrName[i]);
			return -1;
		}
	}
	return id;
}
mqd_t getmsgq() {
	mqd_t id;
	struct mq_attr attr;
	attr.mq_flags = O_NONBLOCK;
	attr.mq_maxmsg = MAX_MSG;
	attr.mq_msgsize = MSG_SIZE;
	attr.mq_curmsgs = 0;
	
	if((id = mq_open(MSGQ_NAME, O_CREAT | O_RDWR, MSGQ_PERM, &attr)) < 0) {
		perror("mq_open error");
		return -1;
	}
	return id;
}
int rmsem() {
	if(semctl(semid, IPC_RMID, NULL) == -1) {
		perror("semctl: remove semaphore error");
		return -1;
	}
	if(mq_close(mqid) < 0) {
		perror("mq_close error");
		return -1;
	}
	return 0;
}

int thr_GPS() {
	char buf[LEN_GPS+LEN_SPEED+1];
	int priority = THREAD_GPS;

	semop(semid, &p_GPS, 1);
	while(mq_receive(mqid, buf, LEN_GPS + LEN_SPEED, &priority) > 0);
	if(errno == EAGAIN) {
		char oldGPS[LEN_GPS + 1];
		strcpy(oldGPS, myInfo.gps);

		strncpy(myInfo.gps, buf, LEN_GPS);
		myInfo.gps[LEN_GPS] = '\0';
		strncpy(myInfo.speed, buf + LEN_GPS, LEN_SPEED);
		myInfo.speed[LEN_SPEED] = '\0';

		// debug: calc direction & update myInfo.dirVector & update myInfo.flag 
	}

	semop(semid, &v_GPS, 1);
	return 0;
}
int thr_DetectAccident() {
	semop(semid, &p_Acci, 1);

	semop(semid, &v_Acci, 1);	
	return 0;
}
int thr_Bluetooth()  {
	semop(semid, &p_Blue, 1);

	semop(semid, &v_Blue, 1);	
	return 0;
}
int thr_Network() {
	semop(semid, &p_Net, 1);

	semop(semid, &v_Net, 1);	
	return 0;
}
