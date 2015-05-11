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
#include <sys/stat.h>
#include <semaphore.h>
#include <mqueue.h>

#define MSGQ_PERM	0777
#define MAX_MSG		10
#define MSG_SIZE	1024

#define SEM_PERM	0777
#define SEM_FLAG	O_CREAT | O_RDWR

sem_t* getsem(const char* semName);
int getmsgq(const char* msgqName);
int rmmsgq(const mqd_t mqid);
int rmsem(const char* semName);

#endif
