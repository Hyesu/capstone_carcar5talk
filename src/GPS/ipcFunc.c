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

void getSharedMemory(GPSValue** gpsAry1, GPSValue** gpsAry2) {
	if(shmid1 = shmget(SHMKEY1, sizeof(ENTRY * GPSValue), IPCPERM | IPCFLAG) == -1) fatal("shmget1");
	if(shmid2 = shmget(SHMKEY2, sizeof(ENTRY * GPSValue), IPCPERM | IPCFLAG) == -1) fatal("shmget2");

	if((*gpsAry1 = (GPSValue*)shmat(shmid1, 0, 0) == (GPSValue*)-1) fatal("shmat1");
	if((*gpsAry2 = (GPSValue*)shmat(shmid2, 0, 0) == (GPSValue*)-1) fatal("shmat2");
}

void getSemaphore(void) {
	semun x;
	x.val = 1;

	if((semid = semget(SEMKEY, NUMSEM, IPCPERM | IPCFLAG)) == -1)	fatal("semget");
	
	if(semctl(semid, 0, SETVAL, x) == -1)	fatal("semctl for 1st semaphore");
	if(semctl(semid, 1, SETVAL, x) == -1)	fatal("semctl for 2nd semaphore");
}

void removeFacility() {
	if(shmctl(shmid1, IPC_RMID, NULL) == -1) fatal("shmctl remove 1st shared memory");
	if(shmctl(shmid2, IPC_RMID, NULL) == -1) fatal("shmctl remove 2nd shared memory");
	if(semctl(semid, IPC_RMID, NULL) == -1)  fatal("semctl remove semaphore");
}
