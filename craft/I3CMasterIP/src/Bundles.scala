package sifive.blocks.iiicmaster

import chisel3._
import chisel3.util._

class TisrA extends Bundle {
 val tisrEn	 = Bool()
 val da_assigned = UInt(4.W)
 val error	 = Bool()
 val collision   = Bool()
 val ack_slv     = Bool()
 val busBusy	 = Bool()
 val errorM2	 = Bool()
 val readValid	 = Bool()
}

class ConfigA extends Bundle {
 val bus_reset	 = Bool()
 val chip_reset	 = Bool()
 val i3c_mode	 = Bool()
 val slaveId	 = UInt(4.W)
 val readWrite	 = Bool()
 val config_done = Bool()
 val load_done	 = Bool()
 val abort 	 = Bool() // Should be reviewed by Block A 
}

class DataA extends Bundle {
 val read_data = Output(UInt(32.W))
 val write_data = Input(UInt(32.W))
}

class SlaveInfoA extends Bundle {

 val statAddr0 = UInt(7.W)
 val slvType0  = UInt(2.W)

 val statAddr1 = UInt(7.W)
 val slvType1  = UInt(2.W)

 val statAddr2 = UInt(7.W)
 val slvType2  = UInt(2.W)

 val statAddr3 = UInt(7.W)
 val slvType3  = UInt(2.W)

 val statAddr4 = UInt(7.W)
 val slvType4  = UInt(2.W)

 val statAddr5 = UInt(7.W)
 val slvType5  = UInt(2.W)

 val statAddr6 = UInt(7.W)
 val slvType6  = UInt(2.W)

 val statAddr7 = UInt(7.W)
 val slvType7  = UInt(2.W)

 val statAddr8 = UInt(7.W)
 val slvType8  = UInt(2.W)

 val statAddr9 = UInt(7.W)
 val slvType9  = UInt(2.W)

 val statAddr10 = UInt(7.W)
 val slvType10  = UInt(2.W)
 
}
class ConfigC extends Bundle {
	val configEn	= Bool()
        val as          = UInt(2.W)
        val abtR        = UInt(1.W)
        val hep         = UInt(1.W)
        val hdrM        = UInt(2.W)
        val bdccc       = UInt(1.W)
        val rw          = UInt(1.W)
        val sdr         = UInt(1.W)
        val ini         = UInt(1.W)
        val asss        = UInt(1.W)
        val srP         = UInt(1.W)
        val s           = UInt(1.W)
        val cccNccc     = UInt(1.W)
        val entdaa      = UInt(1.W)
}


class StatusC extends Bundle {
	val statusEn	= Bool()
        val an          = UInt(1.W)
        val ab          = UInt(1.W)
        val t          = UInt(1.W)
        val sss         = UInt(1.W)
        val mcs         = UInt(2.W)
        val dsr         = UInt(1.W)
        val dst         = UInt(1.W)
        val bc          = UInt(2.W)
}

class DataregC extends Bundle {
        val dataregTx = Input(Vec(4, UInt(8.W)))
        val dataregRx = Output(Vec(8, UInt(8.W)))
}



class C2_input extends Bundle{
	val prescale		= UInt(15.W)
	val start_bit		= UInt(1.W)
	val repeated_start_bit	= UInt(1.W)
	val stop_bit 		= UInt(1.W)
	val acknack_H_bit	= UInt(1.W)
	val acknack_noH_bit	= UInt(1.W)
	val datatx_bit		= UInt(1.W)
	val datarx_bit		= UInt(1.W)
	val scl_en_bit		= UInt(1.W)
	val sda_en_bit		= UInt(1.W)
	val dataTx		= UInt(9.W)
	val dataTxLast		= UInt(1.W)
}

class C2_output extends Bundle{
	val acknack_rcvd_bit	= UInt(1.W)
	val dataRx		= UInt(9.W)
	val dataRxLast		= UInt(1.W)
	val busy_bit		= UInt(1.W)
	val bus_control_bit	= UInt(1.W)
	val bus_active_bit	= UInt(1.W)
}

