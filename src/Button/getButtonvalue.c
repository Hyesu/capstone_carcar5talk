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
int getButtonvalue();

int main(int argc, char** argv) {
	if(init() < 0) {
		perror("init error");
		exit(0);
	}

	while(1) {
		if(getButtonvalue() < 0) {
			perror("get Button value error");
			exit(0);
		}
		delay(DELAY);
	}
	return 0;
}

int init() {
	wiringPiSetup();
	pinMode(BUTTON, INPUT);

	return 0;
}
int getButtonvalue() {
	printf("%s\n", digitalRead(BUTTON) != 1 ? "TRUE" : "FALSE");
	return 0;
}
