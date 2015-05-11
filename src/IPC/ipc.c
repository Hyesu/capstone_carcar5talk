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

//debug
sem_unlink(semName);

	if((id = sem_open(semName, SEM_FLAG | O_EXCL, SEM_PERM, 1)) == NULL) {
		perror("sem_open error");
		return (sem_t*) SEM_FAILED;
	}

	if(sem_init(id, 1, 1) < 0) {
		perror("sem_init");
		return (sem_t*) SEM_FAILED;
	}
	return id;
}
mqd_t getmsgq(const char* msgqName) {
	mqd_t id;
	struct mq_attr attr;
	attr.mq_flags = O_NONBLOCK;
	attr.mq_maxmsg = MAX_MSG;
	attr.mq_msgsize = MSG_SIZE;
	attr.mq_curmsgs = 0;

//debug
mq_unlink(msgqName);
	
	if((id = mq_open(msgqName, O_CREAT | O_RDWR, MSGQ_PERM, &attr)) < 0) {
		perror("mq_open error");
		return -1;
	}
	return id;
}
int rmmsgq(const mqd_t mqid) {
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
