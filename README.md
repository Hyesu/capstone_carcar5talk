# capstone_carcar5talk
Kookmin Universary 2015 capstone team


According to the statistics of 2013 traffic accident, the 72.1% of traffic accident occurs between cars. The accidents between cars are classified into two categories; the collision, the rollover. The reason of traffic accident is mostly caused by nonfulfillment of safe driving. The factor that makes nonfulfillment of safe driving is divided into the outside factor of car and the inside factor of car. The example of outside factor of car is to drive in bad weather or in dark. The examples of inside factor of car are intentional reckless driving of some drivers, inexperienced driving and the carelessness about blind spot. The latest traffic accident, 100-car collision of yeongjong bridge, shows extreme case that drivers couldn't have secured a clear view of front side by the outside factor of car. Therefore the existing car system is not good enough and there may be situation that the driver couldn't have secured a clear view in driving. *CarTalk* System helps secure driving despite in situation that the drive couldn't have secured a clear view in driving.


This project, called 'CarTalk System', has purpose for helping driver to drive securely by providing driver with information of car environment. *CarTalk System* provides driver with some information about position and relative velocity of perimetric cars. Driver can maintain safety distance with perimetric cars because *CarTalk System* has driver know position of perimetric cars. And by *CarTalk System*, driver can know relative velocity of perimetric cars so can easily do picky driving; lane changing. Moreover, *CarTalk System* warns accident event of the front to driver, so driver can know information of car environment widely.

---

본 프로젝트(이하 CarTalk 시스템)는 운전자에게 차량 환경 정보를 제공함으로써 안전 주행을 할 수 있도록 돕는 것을 목표로 한다. CarTalk 시스템이 운전자에게 제공하는 차량 환경 정보는 주변 차량에 대한 위치 정보와 주변 차량과 내 차량 간의 상대 속도 정보이다. CarTalk 시스템을 통해 운전자는 주변 차량의 위치를 알 수 있으므로, 주변 차량과의 안전 거리를 확보할 수 있다. 또한 CarTalk 시스템을 통해 운전자는 주변 차량이 내 차량에게 빠르게 접근 중인지 등의 상대 속도 정보를 알 수 있으므로, 차선 변경과 같은 특정 주행을 쉽게 할 수 있다. 뿐만 아니라 CarTalk 시스템은 운전자에게 전방 사고 발생과 같은 이벤트를 경고함으로써, 운전자가 알 수 없는 차량 환경 정보를 알 수 있게 한다.  
CarTalk 시스템은 차량 환경 정보를 얻기 위하여 GPS 모듈, 3축 가속도 센서, 무선 네트워크 모듈, 블루투스 모듈이 부착되어 있는 라즈베리파이를 사용한다. CarTalk 시스템은 일정 시간마다 주변 차량에게 위치 정보와 주행 방향 정보를 요청한다. 주변 차량은 GPS 모듈을 통해 위치 정보와 주행 방향 정보를 계산하여 응답을 하게 되는데, 이때 요청과 응답과 관련된 통신을 위해 무선 네트워크 모듈이 이용된다. 주변 차량으로부터 응답을 받은 CarTalk 시스템은 블루투스 모듈을 통해 스마트폰에 출력할 정보를 전송하고, 스마트폰의 HUD(Head Up Display)를 담당하는 어플리케이션은 블루투스 모듈을 통해 받은 정보를 이용하여 사용자가 이해하기 쉽도록 주변 차량 정보를 HUD에 그래픽 출력한다. 만약 CarTalk 시스템이 장착된 차량이 충돌 또는 추돌 사고를 겪는 경우 3축 가속도 센서는 비정상 값을 계산하게 되고, 그 결과를 주변 차량에게 브로드캐스팅함으로써 사고 발생 여부를 전달한다.
