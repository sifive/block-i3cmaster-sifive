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
for(int i=0 ; i<100; i++)
{
  *(uint32_t *)(BASE_ADDRESS + I3CCONFIG) = 2;
}
}
int main(){
	slaveinfo();
	config();

}
