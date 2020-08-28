package sifive.blocks.iiicmaster

import chisel3._
import chisel3.util._

object DaaFSM {

        def typeofSlave(in: UInt):Bool = {
                val restrictedAddr = ((in==="h3E".U) || (in==="h5E".U) || (in==="h6E".U) || (in==="h76"                                      .U) || (in >= "h00".U && in <= "h07".U)
                                          || (in >= "h78".U && in <= "h7F".U))

                restrictedAddr

        }

	def lookUpTable(dcrval: UInt):UInt = {
		val slaveidx = RegInit(0.U(4.W))
		switch(dcrval){
			is("h21".U) { slaveidx := "h0".U }
			is("h22".U) { slaveidx := "h1".U }
 		        is("h02".U) { slaveidx := "h2".U }
 		        is("h24".U) { slaveidx := "h3".U }
                        is("h27".U) { slaveidx := "h4".U }
                        is("h63".U) { slaveidx := "h5".U }
                        is("h67".U) { slaveidx := "h6".U }
                        is("h81".U) { slaveidx := "h7".U }
                        is("hA1".U) { slaveidx := "h8".U }
                        is("hE2".U) { slaveidx := "h9".U }
                        is("h64".U) { slaveidx := "hA".U }


		}
		slaveidx

	}

	def mapDynamicAddr(pidDcr: UInt): UInt = {
		val dynAddr = WireDefault(0.U(7.W))
		switch(pidDcr){
			is("h020A0000100021".U) { dynAddr := "h13".U }
			is("h020A0000200022".U) { dynAddr := "h17".U }
			is("h020A0000300002".U) { dynAddr := "h1B".U }
			is("h020A0000400024".U) { dynAddr := "h1F".U }
			is("h020A0000500027".U) { dynAddr := "h23".U }
			is("h020A0000600063".U) { dynAddr := "h27".U }
			is("h020A0000700067".U) { dynAddr := "h2B".U }
			is("h020A0000800081".U) { dynAddr := "h2F".U }
			is("h020A00009000A1".U) { dynAddr := "h33".U }
			is("h020A0000A000E2".U) { dynAddr := "h37".U }
			is("h020A0000B00064".U) { dynAddr := "h3B".U }
		 //     is("h00000000000000".U) { dynAddr := "h30".U }		
		}
			
		dynAddr
	}

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

}


class IObundle extends Bundle {
 val tisrA	 = Output(new TisrA)
 val configA	 = Input(new ConfigA)
 val slvInfoA	 = Input(new SlaveInfoA) 
 val configC	 = Output(new ConfigC)
 val statusC	 = Input(new StatusC)
 val dataregC	 = Flipped(new DataregC)
 
 val daaEn	 = Input(UInt(3.W))
 val daaDone     = Output(Bool())
 val sdrfout	 = Output(Vec(11,new Sdrf()))

}

class DaaFSM extends Module {

val io = IO(new IObundle)


// Initialization of Outputs:
io.configC	 := WireDefault(0.U.asTypeOf(new ConfigC))
io.tisrA	 := WireDefault(0.U.asTypeOf(new TisrA))
io.dataregC.dataregTx := VecInit(Seq.fill(4) {0.U(8.W)})
io.daaDone 	 := 0.B

// state-0 (Initialization) variables
val slvInfoReg	 = RegInit(VecInit(Seq.fill(11) {0.U(9.W)}))
val stype	 = RegInit(VecInit(Seq.fill(11) {0.U(1.W)}))
val countAASA	 = RegInit(0.U(4.W))
val countDASA	 = RegInit(0.U(4.W))
val countEntDAA	 = RegInit(0.U(4.W))
val countFor	 = RegInit(0.U(4.W))
val loadSlaveInfo = RegInit(0.B)
val readSlaveInfo = RegInit(0.B)
val readSortOrder = RegInit(0.B)
val unsort	 = RegInit(VecInit(Seq.fill(11) {127.U(7.W)}))
val slvReg	 = RegInit(0.U(9.W))
val sortvec	 = Reg(Vec(11,UInt(7.W)))
val totalI3CSlvs = RegInit(0.U(4.W))

// Broadcast CCC Variables 
val sentBDAddr	 = RegInit(false.B)
val sentCode	 = RegInit(false.B)
val hep		 = RegInit(false.B)

// state-2 (setdasa) variables
val countsort	    = RegInit("hA".U(4.W))
val countvar	    = RegInit(0.U(4.W))
val dasaSA	    = RegInit("h08".U(7.W))
val sentStatAddr    = RegInit(0.B)
val countDASuccess  = RegInit(0.U(4.W))

//state-3 (updateSDRF) variables
val index	 = RegInit(0.U(4.W))
val updatesdrf	 = RegInit(0.B)
val sdrf	 = Reg(Vec(11,new Sdrf()))
val slvalueReg	 = RegInit(0.U(9.W))

// state -5 (send7Eread) variables
val sent7eRead	 = RegInit(0.B)
val countDaa	 = RegInit(0.U(4.W))

// state-6 (read64) variables
val sentDynAddr    = RegInit(0.B)
val selectedDA	   = RegInit(0.B)
val rec64bits 	   = RegInit(0.B)

//state-7 (updateDaaSDRF) variables
val slaveIndex	= RegInit(0.U(4.W))
val readSlvIdx 	= RegInit(0.B)
val pid 	= RegInit(0.U(48.W))
val bcr 	= RegInit(0.U(8.W))
val dcr 	= RegInit(0.U(8.W))
val dynamicAddr = RegInit(0.U(7.W))

//state-8 (stopCD)  variables 
val countNACK	   = RegInit(0.U(4.W))
val collisionCount = RegInit(0.U(3.W))


val initialization :: setaasa :: setdasa :: updateSDRF :: entdaa :: send7Eread :: read64 :: updateDaaSDRF :: stopCD ::  rstdaa  :: Nil = Enum(10)

val stateReg = RegInit(initialization)


when(io.daaEn === 1.U){
when(io.configA.bus_reset || io.configA.chip_reset){
	stateReg	 := initialization
	io.configC	 := WireDefault(0.U.asTypeOf(new ConfigC))
	io.tisrA	 := WireDefault(0.U.asTypeOf(new TisrA))
	io.dataregC.dataregTx := VecInit(Seq.fill(4) {0.U(8.W)})
	io.daaDone 	 := 0.B

// state-0 (Initialization) variables
 	slvInfoReg	 := VecInit(Seq.fill(11) {0.U(9.W)})
	stype	 	 := VecInit(Seq.fill(11) {0.U(1.W)})
	countAASA	 := 0.U
	countDASA	 := 0.U
	countFor	 := 0.U
	loadSlaveInfo 	 := 0.B
	readSlaveInfo    := 0.B
	readSortOrder 	 := 0.B
	unsort	 	 := VecInit(Seq.fill(11) {127.U(7.W)})
	slvReg	 	 := 0.U
	sortvec	 	 := VecInit(Seq.fill(11) {0.U(7.W)})
	totalI3CSlvs 	 := 0.U

// Broadcast CCC Variables 
	sentBDAddr	 := false.B
	sentCode	 := false.B
	hep		 := false.B

// state-2 (setdasa) variables
	countsort	 := "hA".U
	countvar	 := 0.U
	dasaSA	    	 := "h08".U
	sentStatAddr     := 0.B
	countDASuccess   := 0.U

//state-3 (updateSDRF) variables
	index	 	 := 0.U
	updatesdrf	 := 0.B
//	sdrf	 	 := VecInit(Seq.fill(11) {0.U(32.W)})
	slvalueReg	 := 0.U

// state -5 (send7Eread) variables
	sent7eRead	 := 0.B

// state-6 (read64) variables
	sentDynAddr    	 := 0.B
	rec64bits 	 := 0.B

//state-7 (updateDaaSDRF) variables
	slaveIndex	 := 0.U
	readSlvIdx 	 := 0.B
	pid 		 := 0.U
	bcr 		 := 0.U
	dcr 		 := 0.U
	dynamicAddr 	 := "h13".U

//state-8 (stopCD)  variables 
	countNACK	 := 0.U
	collisionCount   := 0.U


}.otherwise {

switch(stateReg) {
	
	is(initialization){
		
		io.configC.configEn := 0.B
		when(io.configA.load_done && !loadSlaveInfo){
			slvInfoReg(0) := Cat(io.slvInfoA.statAddr0,io.slvInfoA.slvType0)
			slvInfoReg(1) := Cat(io.slvInfoA.statAddr1,io.slvInfoA.slvType1)
			slvInfoReg(2) := Cat(io.slvInfoA.statAddr2,io.slvInfoA.slvType2)
			slvInfoReg(3) := Cat(io.slvInfoA.statAddr3,io.slvInfoA.slvType3)
			slvInfoReg(4) := Cat(io.slvInfoA.statAddr4,io.slvInfoA.slvType4)
			slvInfoReg(5) := Cat(io.slvInfoA.statAddr5,io.slvInfoA.slvType5)
			slvInfoReg(6) := Cat(io.slvInfoA.statAddr6,io.slvInfoA.slvType6)
			slvInfoReg(7) := Cat(io.slvInfoA.statAddr7,io.slvInfoA.slvType7)
			slvInfoReg(8) := Cat(io.slvInfoA.statAddr8,io.slvInfoA.slvType8)
			slvInfoReg(9) := Cat(io.slvInfoA.statAddr9,io.slvInfoA.slvType9)
			slvInfoReg(10) := Cat(io.slvInfoA.statAddr10,io.slvInfoA.slvType10)
			loadSlaveInfo := 1.B
		}	
		when(countFor < 12.U && loadSlaveInfo){
			slvReg := slvInfoReg(countFor)

			when(slvReg(1,0)==="b11".U) {

		        	when(DaaFSM.typeofSlave(slvReg(8,2))){
          			 	stype(countFor-1.U)	:= 1.U
          			 	countDASA 		:= countDASA + 1.U
          			 	unsort(countFor-1.U) 	:= slvReg(8,2)
					totalI3CSlvs 		:= totalI3CSlvs + 1.U
        			}.otherwise {
					totalI3CSlvs 		:= totalI3CSlvs + 1.U
          				stype(countFor-1.U) 	:= 0.U
          				countAASA 		:= countAASA + 1.U
				}
			}.elsewhen(slvReg(1,0) === "b10".U){
				totalI3CSlvs := totalI3CSlvs + 1.U
				countEntDAA  := countEntDAA + 1.U	
			}
			countFor := countFor + 1.U

		}.elsewhen(countFor === 12.U) {

			readSlaveInfo := 1.B
       		 }


		val sortDaa	   = Module(new Sorting()) 
		sortDaa.io.unsort := unsort
		sortDaa.io.load	  := readSlaveInfo
		sortvec     	  := sortDaa.io.sort
		readSortOrder	  := sortDaa.io.sort_done
	
		
		val daaCondA = !(countAASA.orR) & readSlaveInfo & readSortOrder
		val daaCondB =  readSlaveInfo & readSortOrder
		
		when(daaCondA){
			stateReg := setdasa 
	
		}.elsewhen(daaCondB){
		  	stateReg := setaasa
	
		}


	}

	is(setaasa){

		io.configC.configEn 	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U


		val en_ack_7E	     = stateReg === setaasa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E)  = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	     = stateReg === setaasa && sentCode && sentBDAddr
		val (value2,tbit)    = DaaFSM.tBitDelay(en_tbit)

		val en_hep	     = stateReg === setaasa && hep && sentBDAddr
		val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc := 1.U
			io.configC.s	 := 1.U
			io.configC.rw	 := 0.U
			io.configC.cccNccc := 1.U
			io.dataregC.dataregTx(0) := "hFC".U
			sentBDAddr	 := 1.B
	
		}.elsewhen(io.statusC.an === 0.U && ack_7E){
			io.dataregC.dataregTx(0) := "h29".U
			sentCode 	:= 1.B

		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep 		 := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep 		 := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= setdasa
			countDASuccess	:= countAASA	
			io.configC.srP	:= 0.U
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}

	}

	is(setdasa){

		io.configC.configEn 	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U
	
		val en_ack	  = (stateReg === setdasa  && sentBDAddr && !sentCode && !hep) || (sentStatAddr)
                val (value1,ack)  = DaaFSM.ackHandOff(en_ack)

                val en_tbit	  = stateReg === setdasa && sentCode && sentBDAddr && !sentStatAddr
                val (value2,tbit) = DaaFSM.tBitDelay(en_tbit)

                val en_hep	  = stateReg === setdasa && hep && sentBDAddr
                val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc   := 1.U
                      //io.configC.s	   := 1.U
                        io.configC.rw	   := 0.U
                        io.configC.cccNccc := 1.U
                        io.dataregC.dataregTx(0) := "hFC".U
                        sentBDAddr	   := 1.B			
	
		}.elsewhen(io.statusC.an === 0.U && ack && !sentStatAddr){
                        io.dataregC.dataregTx(0) := "h87".U
                        sentCode		 := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack && !sentStatAddr){
                        io.tisrA.errorM2 := 1.B
                        io.configC.hep 	 := 1.U
                        hep		 := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr	 := 0.B
                        sentCode  	 := 0.B
                        io.tisrA.errorM2 := 0.B
                        hep		 := 0.B
                }.elsewhen(countvar < countDASA && sentCode){
			when(io.statusC.dst === 1.U && tbit){
                       		io.configC.srP		 := 0.U
                		io.dataregC.dataregTx(0) := Cat(sortvec(countsort),0.U)
         	               	sentStatAddr  		 := 1.B	        
         			       		
				   
                	}.elsewhen(io.statusC.an === 0.U && ack){
				io.dataregC.dataregTx(0) := Cat(dasaSA,0.U)
                                countsort	 := countsort - 1.U
                                dasaSA	 	 := dasaSA + 1.U
                                countvar 	 := countvar + 1.U
                                countDASuccess	 := countDASuccess + 1.U
                                sentStatAddr  	 := 0.B
	
			}			
		}.elsewhen(countvar >= countDASA && io.statusC.dst === 1.U && tbit){
                        stateReg 	:= updateSDRF
                        io.configC.srP  := 1.U
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B
			sentStatAddr 	:= 0.B
			countsort 	:= "hA".U
			dasaSA 		:= "h08".U
			countvar 	:= 0.U
                }
	
	}
	
	is(updateSDRF){
		io.configC.configEn := 0.B
		when(index < 12.U){
			slvalueReg := slvInfoReg(index)
	             	when(slvalueReg(1,0)==="b11".U){
				when(stype(index-1.U)===1.U){  //SETDASA			
				
					when(slvalueReg(8,2) === sortvec(10)){
						sdrf(index-1.U).dynamicAddr := "h8".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(9)){
						sdrf(index-1.U).dynamicAddr := "h9".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(8)){
						sdrf(index-1.U).dynamicAddr := "hA".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(7)){
						sdrf(index-1.U).dynamicAddr := "hB".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(6)){
						sdrf(index-1.U).dynamicAddr := "hC".U
						sdrf(index-1.U).valid 	    := 1.B
					
					}.elsewhen(slvalueReg(8,2) === sortvec(5)){
						sdrf(index-1.U).dynamicAddr := "hD".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(4)){
						sdrf(index-1.U).dynamicAddr := "hE".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(3)){
						sdrf(index-1.U).dynamicAddr := "hF".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(2)){
						sdrf(index-1.U).dynamicAddr := "h10".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(1)){
						sdrf(index-1.U).dynamicAddr := "h11".U
						sdrf(index-1.U).valid 	    := 1.B

					}.elsewhen(slvalueReg(8,2) === sortvec(0)){
						sdrf(index-1.U).dynamicAddr := "h12".U
						sdrf(index-1.U).valid 	    := 1.B
					}
				  
	
				}.otherwise{   //SETAASA

					sdrf(index-1.U).dynamicAddr := slvalueReg(8,2)
					sdrf(index-1.U).valid 	    := 1.B

				}

			}	
			
			index	   := index + 1.U
			
		}.elsewhen(index === 12.U){
			updatesdrf := 1.B

		}


		when(updatesdrf){
			index	 := 0.U
			stateReg := entdaa

		}

	}

	is(entdaa){
		io.configC.configEn 	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U

		val en_ack_7E	     = stateReg === entdaa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E)  = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	     = stateReg === entdaa && sentCode && sentBDAddr
		val (value2,tbit)    = DaaFSM.tBitDelay(en_tbit)

		val en_hep	     = stateReg === entdaa && hep && sentBDAddr
		val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc   := 1.U
			io.configC.s	   := 1.U
	//		io.configC.rw	   := 0.U
			io.configC.cccNccc := 1.U
			io.dataregC.dataregTx(0) := "hFC".U
			sentBDAddr	   := 1.B
	
		}.elsewhen(io.statusC.an === 0.U && ack_7E){
			io.dataregC.dataregTx(0) := "h07".U
			sentCode	 := 1.B

		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep		 := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep		 := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= send7Eread	
			io.configC.srP	:= 0.U
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}

	}


	is(send7Eread){
		io.configC.configEn	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini	  	:= 0.U

		val en_ack	    = stateReg === send7Eread && sent7eRead
                val (value1,ack_DA) = DaaFSM.ackWithoutHandOff(en_ack)
	
		when(!sent7eRead){
			io.configC.srP 	   := 0.U
			io.configC.rw 	   := 1.U
			io.configC.cccNccc := 0.U
			io.dataregC.dataregTx(0) := "hFD".U
			sent7eRead 	   := 1.B
		}.elsewhen(io.statusC.an === 0.U && ack_DA && countDaa < countEntDAA){
			stateReg   	:= read64  
			sent7eRead	:= 0.B
		}.elsewhen(io.statusC.an === 1.U && ack_DA && countDaa === countEntDAA){
			stateReg 	:= stopCD
			sent7eRead 	:= 0.B
		}				
	}

	is(read64){
		io.configC.configEn 	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U
		
		when(io.statusC.ab === 1.U && io.statusC.dsr === 1.U && !rec64bits){
			pid	  := Cat(io.dataregC.dataregRx(0),io.dataregC.dataregRx(1),io.dataregC.dataregRx(2),io.dataregC.dataregRx(3),io.dataregC.dataregRx(4),io.dataregC.dataregRx(5))
			bcr	  := io.dataregC.dataregRx(6)
			dcr	  := io.dataregC.dataregRx(7)
			rec64bits := 1.B
		}.elsewhen(!selectedDA && rec64bits && stateReg === read64){
			dynamicAddr := DaaFSM.mapDynamicAddr(Cat(pid,dcr))
			selectedDA  := 1.B
		}.elsewhen(selectedDA && stateReg === read64){
			io.dataregC.dataregTx(0) := Cat(dynamicAddr,(!(dynamicAddr.xorR)).asUInt)
			stateReg	:= updateDaaSDRF
			sentDynAddr	:= 1.B
			rec64bits	:= 0.B
			selectedDA	:= 0.B 
		}	

	}

	is(updateDaaSDRF){
		io.configC.configEn := 0.B
         
	        val en_ack	    = stateReg === updateDaaSDRF && sentDynAddr
		val (value1,ack_DA) = DaaFSM.ackWithoutHandOff(en_ack)
		when(!readSlvIdx){
			slaveIndex := DaaFSM.lookUpTable(dcr)
			readSlvIdx := 1.B
		}.elsewhen(io.statusC.an === 0.U && ack_DA && readSlvIdx){

			sdrf(slaveIndex).dynamicAddr	 := dynamicAddr
			sdrf(slaveIndex).valid		 := 1.B
			sdrf(slaveIndex).bcr_load	 := 1.B
			sdrf(slaveIndex).bcr		 := bcr
			dynamicAddr	 := dynamicAddr + 4.U			
			countDASuccess	 := countDASuccess + 1.U
			stateReg	 := send7Eread
			readSlvIdx	 := 0.B
			sentDynAddr	 := 0.B
			countDaa	 := countDaa + 1.U
		}.elsewhen(io.statusC.an === 1.U && ack_DA && readSlvIdx){
			sentDynAddr	 := 0.B
			readSlvIdx	 := 0.B
			when(countNACK === 0.U){
				countNACK := countNACK + 1.U
			 	stateReg  := send7Eread
				
			}.elsewhen(countNACK === 1.U){
				io.tisrA.error 	 := 1.B
				stateReg	 := initialization
				
			} 


		}
	}

	is(stopCD){
		io.configC.configEn	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U
		
		io.configC.srP 		:= 1.U
	
		when(countDASuccess === totalI3CSlvs){
			io.daaDone	 := 1.B
			io.tisrA.da_assigned := countDASuccess
			stateReg	 := initialization

		}.otherwise{
			when(collisionCount < 3.U){
				collisionCount 	:= collisionCount + 1.U
				stateReg	:= rstdaa
			}.otherwise{
				io.tisrA.collision := 1.B
				stateReg	   := initialization
			}
		

		}

	}

	
	is(rstdaa){
		io.configC.configEn 	:= 1.B
		io.configC.sdr		:= 1.U
		io.configC.ini  	:= 0.U

		val en_ack_7E	     = stateReg === rstdaa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E)  = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	     = stateReg === rstdaa && sentCode && sentBDAddr
		val (value2,tbit)    = DaaFSM.tBitDelay(en_tbit)

		val en_hep	     = stateReg === rstdaa && hep && sentBDAddr
		val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc   := 1.U
			io.configC.s	   := 1.U
			io.configC.rw	   := 0.U
			io.configC.cccNccc := 1.U
			io.dataregC.dataregTx(0) := "hFC".U
			sentBDAddr	   := 1.B
	
		}.elsewhen(io.statusC.an === 0.U && ack_7E){
			io.dataregC.dataregTx(0) := "h06".U
			sentCode 		 := 1.B

		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep 		 := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep 		 := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= setaasa	
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}
	

	}


	

} // Switch
} // elsewhen
} // daaEn

	      io.sdrfout := sdrf



} // Module

