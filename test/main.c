#include <stdio.h>
#include <stdlib.h>
#include "metal/interrupt.h"
#include "metal/machine.h"
#include "metal/cpu.h"



#define	BASE_ADDRESS (uintptr_t)0x10000000
#define REGISTER1 0x0
#define REGISTER2 0x4


void r1_value() {
  *(uint32_t *)(BASE_ADDRESS + REGISTER1) = 2;
}

void r2_value() {
  *(uint32_t *)(BASE_ADDRESS + REGISTER2) = 1;
}

int main(){
	r1_value();
	r2_value();	

}
