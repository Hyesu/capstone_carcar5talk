/**************************************************
*
*   CarTalk System
*   
*   Kookmin University Capstone Project
*   Team. Carcar5talk
*   20123381 Shin Hyesu
*
**************************************************/
#ifndef __IPC_C__
#define __IPC_C__

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#include <sys/stat.h>
#include <mqueue.h>

#define MSGQ_NAME	"CarTalk_MsgQ"
#define MSGQ_PERM	0644
#define MAX_MSG		1024
#define MSG_SIZE	4096

#define SEM_KEY		(key_t) 0x81
#define SEM_PERM	0600
#define SEM_FLAG	IPC_CREAT | IPC_EXCL

typedef union _semun {
	int val;
	struct semid_ds* buf;
	ushort* array;
} semun;

int getsem(const int numSem);
int getmsgq();
int rmipc(const int semid, const mqd_t mqid, const int numSem);

#endif
