/**************************************************
*
*   CarTalk System
*   
*   Kookmin University Capstone Project
*   Team. Carcar5talk
*   20123381 Shin Hyesu
*
**************************************************/
#include "ipc.h"

int getsem(const int numSem) {
	semun x;
	int id;
	int i;

	x.val = 1;
	if((id = semget(SEM_KEY, numSem, SEM_PERM | SEM_FLAG)) == -1) {
		perror("semget error");
		return -1;
	}
	for(i=0; i<numSem; i++) {
		if(semctl(id, i, SETVAL, x) == -1) {
			printf("semctl: set initial value for semaphore for %d error", i);
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
int rmipc(const int semid, const mqd_t mqid, const int numSem) {
	int i;

	for(i=0; i<numSem; i++) {
		if(semctl(semid, i, IPC_RMID, NULL) == -1) {
			perror("semctl: remove semaphore error");
			return -1;
		}
	}
	if(mq_close(mqid) < 0) {
		perror("mq_close error");
		return -1;
	}
	return 0;
}
