/*


 
#include <stdio.h>
#include <stdlib.h>
#include "metal/interrupt.h"
#include "metal/machine.h"
#include "metal/cpu.h"



#define	BASE_ADDRESS (uintptr_t)0x10000000
#define SLAVE0 0x0
#define SLAVE1 0x4
#define SLAVE2 0x8
#define SLAVE3 0xC
#define SLAVE4 0x10
#define SLAVE5 0x14
#define SLAVE6 0x18
#define SLAVE7 0x1C
#define SLAVE8 0x20
#define SLAVE9 0x24
#define SLAVE10 0x28
#define SLAVETYPE 0x0
#define STATADDR 0x8
#define I3CCONFIG 0x3C
#define CONFIG_DONE 0x0
#define LOAD_DONE 0x1
#define READWRITE 0x2
#define SLAVEID	0x3
#define I3C_MODE 0x7
#define CHIP_RESET 0x3E
#define BUS_RESET 0x3F


void slaveinfo() {

for(int i=0; i<100; i++){
  *(uint32_t *)(BASE_ADDRESS + SLAVE0 + SLAVETYPE) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE1 + SLAVETYPE) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE2 + SLAVETYPE) = 2;
  *(uint32_t *)(BASE_ADDRESS + SLAVE3 + SLAVETYPE) = 3;
  *(uint32_t *)(BASE_ADDRESS + SLAVE4 + SLAVETYPE) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE5 + SLAVETYPE) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE6 + SLAVETYPE) = 2;
  *(uint32_t *)(BASE_ADDRESS + SLAVE7 + SLAVETYPE) = 3;
  *(uint32_t *)(BASE_ADDRESS + SLAVE8 + SLAVETYPE) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE9 + SLAVETYPE) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE10 + SLAVETYPE) = 2;


  *(uint32_t *)(BASE_ADDRESS + SLAVE0 + STATADDR) = 127;
  *(uint32_t *)(BASE_ADDRESS + SLAVE1 + STATADDR) = 126;
  *(uint32_t *)(BASE_ADDRESS + SLAVE2 + STATADDR) = 125;
  *(uint32_t *)(BASE_ADDRESS + SLAVE3 + STATADDR) = 124;
  *(uint32_t *)(BASE_ADDRESS + SLAVE4 + STATADDR) = 123;
  *(uint32_t *)(BASE_ADDRESS + SLAVE5 + STATADDR) = 124;
  *(uint32_t *)(BASE_ADDRESS + SLAVE6 + STATADDR) = 123;
  *(uint32_t *)(BASE_ADDRESS + SLAVE7 + STATADDR) = 122;
  *(uint32_t *)(BASE_ADDRESS + SLAVE8 + STATADDR) = 121;
  *(uint32_t *)(BASE_ADDRESS + SLAVE9 + STATADDR) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE10 + STATADDR) = 0;
}
}

void config(){
for(int i=0 ; i<100; i++)
{
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + CONFIG_DONE) = 1;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + LOAD_DONE) = 1;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + READWRITE) = 1;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + SLAVEID) = 12;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + I3C_MODE) = 0;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + CHIP_RESET) = 0;
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG + BUS_RESET) = 0;
}
}


int main(){
	slaveinfo();
	config();

}

#include <stdio.h>
#include <stdlib.h>
#include "metal/interrupt.h"
#include "metal/machine.h"
#include "metal/cpu.h"



#define	BASE_ADDRESS (uintptr_t)0x10000000
#define SLAVE0 0x0
#define SLAVE1 0x4
#define SLAVE2 0x8
#define SLAVE3 0xC
#define SLAVE4 0x10
#define SLAVE5 0x14
#define SLAVE6 0x18
#define SLAVE7 0x1C
#define SLAVE8 0x20
#define SLAVE9 0x24
#define SLAVE10 0x28
#define I3CCONFIG 0x3C

void slaveinfo() {

for(int i=0; i<10000; i++){
  *(uint32_t *)(BASE_ADDRESS + SLAVE0) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE1) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE2) = 20;
  *(uint32_t *)(BASE_ADDRESS + SLAVE3) = 7;
  *(uint32_t *)(BASE_ADDRESS + SLAVE4) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE5) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE6) = 2;
  *(uint32_t *)(BASE_ADDRESS + SLAVE7) = 3;
  *(uint32_t *)(BASE_ADDRESS + SLAVE8) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE9) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE10) = 2;
}
}

void config(){
for(int i=0 ; i<100; i++)
{
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG) = 2;
}
}
int main(){
	slaveinfo();
	config();

}
*/
#include <stdio.h>
#include <stdlib.h>
#include "metal/interrupt.h"
#include "metal/machine.h"
#include "metal/cpu.h"



#define	BASE_ADDRESS (uintptr_t)0x10000000
#define SLAVE0 0x0
#define SLAVE1 0x4
#define SLAVE2 0x8
#define SLAVE3 0xC
#define SLAVE4 0x10
#define SLAVE5 0x14
#define SLAVE6 0x18
#define SLAVE7 0x1C
#define SLAVE8 0x20
#define SLAVE9 0x24
#define SLAVE10 0x28
#define I3CCONFIG 0x3C

void slaveinfo() {

for(int i=0; i<10; i++){
  *(uint32_t *)(BASE_ADDRESS + SLAVE0) = 30465;
  *(uint32_t *)(BASE_ADDRESS + SLAVE1) = 30466;
  *(uint32_t *)(BASE_ADDRESS + SLAVE2) = 2;
  *(uint32_t *)(BASE_ADDRESS + SLAVE3) = 3;
  *(uint32_t *)(BASE_ADDRESS + SLAVE4) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE5) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE6) = 2;
  *(uint32_t *)(BASE_ADDRESS + SLAVE7) = 3;
  *(uint32_t *)(BASE_ADDRESS + SLAVE8) = 0;
  *(uint32_t *)(BASE_ADDRESS + SLAVE9) = 1;
  *(uint32_t *)(BASE_ADDRESS + SLAVE10) = 2;
}
}

void config(){
for(int i=0 ; i<10; i++)
{
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG) = 3221225571;
}
}

int main(){
	slaveinfo();
	config();

}
