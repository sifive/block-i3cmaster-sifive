
package sifive.blocks.iiicmaster

import chisel3._
import chisel3.util._
import chisel3.experimental._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.diplomaticobjectmodel._
import freechips.rocketchip.diplomaticobjectmodel.model.{OMDevice,OMMemoryRegion,OMComponent,OMRegister}
import freechips.rocketchip.diplomaticobjectmodel.model._
import freechips.rocketchip.diplomaticobjectmodel.logicaltree._
import freechips.rocketchip.regmapper._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.interrupts._


class I3CMasterOutputControl  extends BlackBox(Map()){
	val io = IO(new Bundle(){
		val clk 		= Input(Clock())
    		val rst 		= Input(Bool())
		val prescale 		= Input(UInt(15.W))
		val start 		= Input(UInt(1.W))
    		val repeated_start 	= Input(UInt(1.W))
    		val stop 		= Input(UInt(1.W))
    		val acknack_H 		= Input(UInt(1.W))
    		val acknack_noH 	= Input(UInt(1.W))
    		val datatx 		= Input(UInt(1.W))
    		val datarx 		= Input(UInt(1.W))
  	
  
  		val data_in 		= Input(UInt(9.W))
    		val data_in_last 	= Input(UInt(1.W))

  		val data_out 		= Output(UInt(9.W))
    		val data_out_last 	= Output(UInt(1.W))
		val acknack_rcvd 	= Output(UInt(1.W))
    		val scl_i 		= Input(UInt(1.W))
    		val scl_o 		= Output(UInt(1.W))
    		val scl_en 		= Input(UInt(1.W))
    		val sda_i 		= Input(UInt(1.W))
    		val sda_o 		= Output(UInt(1.W))
    		val sda_en 		= Input(UInt(1.W))
		val sda_pin		= Analog(1.W)
		val scl_pin 		= Analog(1.W)
    		val busy 		= Output(UInt(1.W))
    		val bus_control 	= Output(UInt(1.W))
    		val bus_active 	= Output(UInt(1.W))

	})

}

class I3CMaster_C2(implicit p : Parameters) extends LazyModule{

	lazy val module = new LazyModuleImp(this){
		val rtl_outputcontrol 	= Module(new I3CMasterOutputControl())
		val i3cmaster_c1  	= Module(new I3CMaster_C1())
			
		rtl_outputcontrol.io.clk 		:= i3cmaster_c1.io.rtl_clk
		rtl_outputcontrol.io.rst 		:= i3cmaster_c1.io.rtl_rst
		rtl_outputcontrol.io.prescale 		:= i3cmaster_c1.io.c2_input.prescale
		rtl_outputcontrol.io.start 		:= i3cmaster_c1.io.c2_input.start_bit 
		rtl_outputcontrol.io.repeated_start 	:= i3cmaster_c1.io.c2_input.repeated_start_bit
		rtl_outputcontrol.io.stop 		:= i3cmaster_c1.io.c2_input.stop_bit
		rtl_outputcontrol.io.acknack_H 		:= i3cmaster_c1.io.c2_input.acknack_H_bit
		rtl_outputcontrol.io.acknack_noH 	:= i3cmaster_c1.io.c2_input.acknack_noH_bit
		rtl_outputcontrol.io.datatx 		:= i3cmaster_c1.io.c2_input.datatx_bit
		rtl_outputcontrol.io.datarx 		:= i3cmaster_c1.io.c2_input.datarx_bit
		rtl_outputcontrol.io.scl_en 		:= i3cmaster_c1.io.c2_input.scl_en_bit
		rtl_outputcontrol.io.sda_en 		:= i3cmaster_c1.io.c2_input.sda_en_bit
		rtl_outputcontrol.io.data_in 		:= i3cmaster_c1.io.c2_input.dataTx
		rtl_outputcontrol.io.data_in_last 	:= i3cmaster_c1.io.c2_input.dataTxLast

		i3cmaster_c1.io.c2_output.acknack_rcvd_bit := rtl_outputcontrol.io.acknack_rcvd
		i3cmaster_c1.io.c2_output.dataRx 	   := rtl_outputcontrol.io.data_out
		i3cmaster_c1.io.c2_output.dataRxLast 	   := rtl_outputcontrol.io.data_out_last
		i3cmaster_c1.io.c2_output.busy_bit 	   := rtl_outputcontrol.io.busy
		i3cmaster_c1.io.c2_output.bus_control_bit := rtl_outputcontrol.io.bus_control
		i3cmaster_c1.io.c2_output.bus_active_bit  := rtl_outputcontrol.io.bus_active
	}

}
