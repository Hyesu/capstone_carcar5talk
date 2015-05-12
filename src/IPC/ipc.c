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

sem_t* getsem(const char* semName) {
	sem_t* id;

	if((id = sem_open(semName, SEM_FLAG, SEM_PERM, 1)) == NULL) {
		if(errno == EEXIST) {
			if((id = sem_open(semName, O_RDWR, SEM_PERM, 1)) == NULL) {
				perror("sem_open");
				return (sem_t*)SEM_FAILED;
			}
		} else {
			perror("sem_open");
			return (sem_t*)SEM_FAILED;
		}
	}
	return id;
}
mqd_t getmsgq(const char* msgqName, const int msgSize) {
	mqd_t id;
	struct mq_attr attr;
	attr.mq_flags = O_NONBLOCK;
	attr.mq_maxmsg = MAX_MSG;
	attr.mq_msgsize = msgSize;
	attr.mq_curmsgs = 0;

	if((id = mq_open(msgqName, MSGQ_FLAG, MSGQ_PERM, &attr)) < 0) {
		perror("mq_open error");
		return -1;
	}
	return id;
}
int rmmsgq(const mqd_t mqid, const char* mqName) {
	if(mq_unlink(mqName) < 0) {
		perror("mq_unlink");
		return -1;
	}
	if(mq_close(mqid) < 0) {
		perror("mq_close error");
		return -1;
	} else	return 0;
}
int rmsem(const char* semName) {
	if(sem_unlink(semName) < 0) {
		printf("semaphore %s close error", semName);
		return -1;
	} else	return 0;
}
