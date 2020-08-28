package sifive.blocks.iiicmaster

import chisel3._
import chisel3.util._

object MasterFSM {
	def ackHandOff(en: Bool): (UInt,Bool) = {

                val (value,wrap) = Counter(en,3)
                (value,wrap)
        }


        def ackWithoutHandOff(en: Bool): (UInt,Bool) = {

                val (value,wrap) = Counter(en,3)
                (value,wrap)
        }

        def tBitDelay(en: Bool): (UInt,Bool) = {

                val (value,wrap) = Counter(en,3)
                (value,wrap)
        }

	def hdrExitPattern(en: Bool): (UInt,Bool) = {

                val (value,wrap) = Counter(en,3)
               (value, wrap)
        }

        def tBitRecDelay(en: Bool): (UInt,Bool) = {

                val (value,wrap) = Counter(en,3)
                (value,wrap)
        }

}

class InOuts extends Bundle {
   val tisrA	 = Output(new TisrA)
   val configA	 = Input(new ConfigA)
   val dataA	 = new DataA
   val slvInfoA  = Input(new SlaveInfoA)
   val configC	 = Output(new ConfigC)
   val statusC	 = Input(new StatusC)
   val dataregC	 = Flipped(new DataregC)

}


class MasterFSM extends Module{
 val io = IO(new InOuts)


// Initialization of Tisrs
val tisrReg1 = RegInit(0.U.asTypeOf(new Tisr1))
val tisrReg2 = RegInit(0.U.asTypeOf(new Tisr2))

//Assignment of Output from Tisr registers
io.tisrA.tisrEn		:= 0.U
io.tisrA.da_assigned	:= tisrReg1.da_assigned
io.tisrA.error 		:= tisrReg1.error
io.tisrA.collision 	:= tisrReg1.collision
io.tisrA.ack_slv 	:= tisrReg1.ack_slv
io.tisrA.busBusy 	:= tisrReg1.busBusy
io.tisrA.readValid	:= tisrReg1.readValid
io.tisrA.errorM2 	:= tisrReg2.errorM2

//Initialization of Other Outputs
io.dataA.read_data	:= 0.U
io.configC 		:= WireDefault(0.U.asTypeOf(new ConfigC))
io.dataregC.dataregTx 	:= VecInit(Seq.fill(4) {0.U(8.W)})

//Initialization of registers.
val daAssigned	 = RegInit(false.B)
val sdrf         = Reg(Vec(11,new Sdrf()))
val slaveID	 = RegInit(0.U(4.W))

// Counter variables 
val sentBDAddr	 = RegInit(false.B)
val sentDa	 = RegInit(false.B)
val hep          = RegInit(false.B)

// sdrread (state-2) variables
val enRead 	= RegInit(0.B)
val loadSlaveId = RegInit(0.B)
val countByte 	= RegInit(0.U(3.W))
val readData 	= RegInit(0.U(32.W))
val recData 	= RegInit(0.B)

//sdrwrite (state-3) variables
val enWrite 	= RegInit(0.B)
val writeData 	= RegInit(0.U(32.W))


// FSM
val start :: daa :: sdrread :: sdrwrite :: Nil = Enum(4)

val stateReg	   = RegInit(start)

//SDR_READ Transition
val condA_sdrRead  = (io.configA.readWrite) && (!io.configA.i3c_mode)

//SDR_Write Transition
val condB_sdrWrite = (!io.configA.readWrite) && (!io.configA.i3c_mode) 

when(io.configA.bus_reset){
	daAssigned 	:= 0.B
	stateReg 	:= start
}.otherwise{


switch(stateReg) {
	is(start){
		 
		 when (io.configA.config_done) {
			when(!daAssigned && io.configA.load_done){		
				stateReg 	:= daa 

			}.elsewhen (condA_sdrRead){ 

				stateReg 	:= sdrread 
		   	}.elsewhen (condB_sdrWrite) {

				stateReg 	:= sdrwrite 
			}
		}
	}

	is(daa){

		tisrReg1.busBusy	 := 1.B
		io.tisrA.tisrEn		 := 1.B			
		val daafsm		 = Module(new DaaFSM)

		daafsm.io.slvInfoA 	<> io.slvInfoA
		daafsm.io.configA 	<> io.configA
		daafsm.io.statusC 	<> io.statusC
	 	io.configC 		<> daafsm.io.configC
		daafsm.io.dataregC.dataregRx := io.dataregC.dataregRx
		io.dataregC.dataregTx 	     := daafsm.io.dataregC.dataregTx

//		daafsm.io.dataregC <> io.dataregC
//		io.dataregC <> daafsm.io.dataregC			
	 	
		sdrf 			:= daafsm.io.sdrfout		
		tisrReg1.da_assigned 	:= daafsm.io.tisrA.da_assigned
		val errorDaa 		= daafsm.io.tisrA.error
		val collisionDaa 	= daafsm.io.tisrA.collision
		tisrReg2.errorM2 	:= daafsm.io.tisrA.errorM2		
		daafsm.io.daaEn 	:= stateReg
		val daa_done 		= daafsm.io.daaDone

		when(daa_done){ 
			stateReg 	 := start
			tisrReg1.busBusy := 0.B
			daAssigned	 := 1.B
		}.elsewhen(errorDaa || collisionDaa){
			stateReg	   := start
			tisrReg1.busBusy   := 0.B 
			tisrReg1.collision := collisionDaa
			tisrReg1.error 	   := errorDaa
		}

	}
	
	is(sdrread){
	
		io.configC.configEn	 := 1.B
		io.configC.ini 		 := 0.U
		io.configC.sdr 		 := 1.U
		io.tisrA.tisrEn  	 := 1.B			
		
		val en_ack_7E        = stateReg === sdrread  && sentBDAddr && !sentDa && !hep
                val (value1,ack_7E)  = MasterFSM.ackHandOff(en_ack_7E)

         	val en_ackNoOff      = stateReg === sdrread && sentDa && sentBDAddr && !enRead
                val (value2,ackRead) = MasterFSM.ackWithoutHandOff(en_ackNoOff)
	
	        val en_tbit          = stateReg === sdrread && sentDa && sentBDAddr && enRead && !recData
                val (value3,tbitRec) = MasterFSM.tBitRecDelay(en_tbit)

                val en_hep           = stateReg === sdrread && hep && sentBDAddr
                val (value4,hepDone) = MasterFSM.hdrExitPattern(en_hep)

		when(!loadSlaveId){
			tisrReg1.busBusy := 1.B
                        slaveID		 := io.configA.slaveId
			loadSlaveId	 := 1.B			
				

		}

		when(!sentBDAddr && loadSlaveId){
                        when(sdrf(slaveID).valid === 0.U) {
                                tisrReg1.error := 1.B			
			}.otherwise{
		
	                        io.configC.s       := 1.U
	                        io.configC.rw      := 1.U
	                        io.configC.cccNccc := 0.U
 	                        io.dataregC.dataregTx(0) := "hFC".U
	                        sentBDAddr         := 1.B
			}
                }.elsewhen(io.statusC.an === 0.U && ack_7E){
                	io.configC.srP 		 := 0.U
		        io.dataregC.dataregTx(0) := Cat(sdrf(slaveID).dynamicAddr,1.U)
                        sentDa			 := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack_7E){
                        tisrReg2.errorM2 := 1.B
                        io.configC.hep   := 1.U
                        hep 		 := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr       := 0.B
                        sentDa         	 := 0.B
                        tisrReg2.errorM2 := 0.B
                        hep		 := 0.B
                }

		when(io.statusC.an === 0.U && ackRead){
			enRead		 := 1.B	

		}
		when(tbitRec && countByte < 4.U ){

			when(io.statusC.t === 1.U && io.configA.abort){
				io.configC.abtR := 1.U
		
			}.elsewhen(io.statusC.t === 1.U && !io.configA.abort){
				countByte 	:= countByte + 1.U

			}.elsewhen(io.statusC.t === 0.U){
				when(io.statusC.dsr === 1.U && io.statusC.ab === 0.U){
					io.configC.srP 	 := 1.U
					readData	 := Cat(io.dataregC.dataregRx(0),io.dataregC.dataregRx(1),io.dataregC.dataregRx(2),io.dataregC.dataregRx(3))
					countByte 	 := countByte + 1.U
					recData  	 := 1.B
				}.otherwise{
				//Go to start here--> Received Incomplete Data
					io.configC.srP 	 := 1.U
					tisrReg1.error 	 := 1.B
					stateReg 	 := start
					sentBDAddr 	 := 0.B
					sentDa 		 := 0.B
					loadSlaveId 	 := 0.B
					enRead 		 := 0.B			
					countByte 	 := 0.U
					tisrReg1.busBusy := 0.B
				}
			}
		}.elsewhen(recData && countByte === 4.U){
			io.dataA.read_data 	:= readData
			tisrReg1.readValid 	:= 1.B
			tisrReg1.ack_slv 	:= 1.B
			sentBDAddr 		:= 0.B
			sentDa 			:= 0.B
			countByte 		:= 0.U
			loadSlaveId 		:= 0.B
			enRead 			:= 0.B
			recData 		:= 0.B
			stateReg 	  	:= start 
			tisrReg1.busBusy  	:= 0.B
		}
	} 


	is(sdrwrite){

		io.configC.configEn 	:= 1.B
		io.configC.ini 		:= 0.U
		io.configC.sdr 		:= 1.U
		io.tisrA.tisrEn 	:= 1.B			
	
		val en_ack           = stateReg === sdrwrite  && sentBDAddr  && !hep && !enWrite
                val (value1,ack)     = MasterFSM.ackHandOff(en_ack)

	        val en_tbit          = stateReg === sdrwrite  && sentBDAddr && sentDa && enWrite
                val (value2,tbit)    = MasterFSM.tBitDelay(en_tbit)

                val en_hep           = stateReg === sdrwrite && hep && sentBDAddr
                val (value3,hepDone) = MasterFSM.hdrExitPattern(en_hep)

		when(!loadSlaveId){
			tisrReg1.busBusy := 1.B
                        slaveID 	 := io.configA.slaveId
			loadSlaveId	 := 1.B			
			writeData	 := io.dataA.write_data
		}

		when(!sentBDAddr && loadSlaveId){
                          when(sdrf(slaveID).valid  === 0.U) {
                                tisrReg1.error := 1.B			
			}.otherwise{
		
	                        io.configC.s       := 1.U
	                        io.configC.rw      := 1.U
	                        io.configC.cccNccc := 0.U
 	                        io.dataregC.dataregTx(0) := "hFC".U
	                        sentBDAddr         := 1.B
			}
                }.elsewhen(io.statusC.an === 0.U && ack && !sentDa){
                	io.configC.srP		 := 0.U
		        io.dataregC.dataregTx(0) := Cat(sdrf(slaveID).dynamicAddr,0.U)
                        sentDa			 := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack && !sentDa){
                        tisrReg2.errorM2 := 1.B
                        io.configC.hep   := 1.U
                        hep 		 := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr       := 0.B
                        sentDa           := 0.B
                        tisrReg2.errorM2 := 0.B
                        hep		 := 0.B
                }.elsewhen(sentBDAddr && sentDa && !hep && ack){
			io.dataregC.dataregTx(0) := writeData(31,24)
			io.dataregC.dataregTx(1) := writeData(23,16)
			io.dataregC.dataregTx(2) := writeData(15,8)
			io.dataregC.dataregTx(3) := writeData(7,0)
			io.configC.srP		 := 1.U
			enWrite			 := 1.B
		
			
		}
		when(io.statusC.dst === 1.U && tbit && enWrite){
			countByte 	:= countByte + 1.U
			when(countByte === 3.U){
				tisrReg1.ack_slv := 1.B
				countByte	 := 0.U
				sentBDAddr	 := 0.B
				sentDa		 := 0.B
				loadSlaveId	 := 0.B
				enWrite		 := 0.B
				stateReg	 := start
				tisrReg1.busBusy := 0.B
			}


		}
	}
} // Switch

} // Otherwise

} // Module

