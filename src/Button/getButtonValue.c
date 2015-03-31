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
void printAccident(int isAccident);

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
	}
	return 0;
}

int init() {
	wiringPiSetup();
	pinMode(BUTTON, INPUT);
	pinMode(LED_YELLOW, OUTPUT);
	return 0;
}
int getButtonvalue() {
	printAccident(!digitalRead(BUTTON));
	return 0;
}
void printAccident(int isAccident) {
	if(isAccident) {
		digitalWrite(LED_YELLOW, HIGH);
		delay(DELAY);
	}
	digitalWrite(LED_YELLOW, LOW);
}
