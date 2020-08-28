
package sifive.blocks.iiicmaster


import chisel3._
import chisel3.util._

import freechips.rocketchip.util._

class I3CMaster_C1 extends Module{
	val io = IO(new Bundle{
		val configc 		= Input(new ConfigC)
		val statusc 		= Output(new StatusC)
		val dataregc 		= new DataregC
	
		val c2_input		= Output(new C2_input)
		val c2_output		= Input(new C2_output)
		val rtl_clk		= Output(Clock())
		val rtl_rst		= Output(Bool())

})

	val configregc_configEn = RegInit(false.B)
	val configregc_as 	= RegInit(0.U(2.W))	
	val configregc_abtR 	= RegInit(0.U(1.W))	
	val configregc_hep 	= RegInit(0.U(1.W))	
	val configregc_hdrM 	= RegInit(0.U(2.W))	
	val configregc_bdccc 	= RegInit(0.U(1.W))	
	val configregc_rw 	= RegInit(0.U(1.W))	
	val configregc_sdr 	= RegInit(0.U(1.W))	
	val configregc_ini 	= RegInit(0.U(1.W))	
	val configregc_asss 	= RegInit(0.U(1.W))	
	val configregc_srP 	= RegInit(0.U(1.W))	
	val configregc_s 	= RegInit(0.U(1.W))	
	val configregc_cccNccc 	= RegInit(0.U(1.W))	
	val configregc_entdaa 	= RegInit(0.U(1.W))	

	configregc_configEn 	:= io.configc.configEn 	
	configregc_as 		:= io.configc.as	
	configregc_abtR 	:= io.configc.abtR		
	configregc_hep 		:= io.configc.hep	
	configregc_hdrM 	:= io.configc.hdrM		
	configregc_bdccc 	:= io.configc.bdccc	
	configregc_rw 		:= io.configc.rw		
	configregc_sdr 		:= io.configc.sdr		
	configregc_ini 		:= io.configc.ini		
	configregc_asss 	:= io.configc.asss		
	configregc_srP 		:= io.configc.srP		
	configregc_s 		:= io.configc.s		
	configregc_cccNccc 	:= io.configc.cccNccc	
	configregc_entdaa 	:= io.configc.entdaa	

	val statusregc_statusEn = RegInit(false.B)
	val statusregc_an 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_ab 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_t 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_sss 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_mcs 	= RegInit(RegInit(0.U(2.W)))
	val statusregc_dsr 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_dst 	= RegInit(RegInit(0.U(1.W)))
	val statusregc_bc 	= RegInit(RegInit(0.U(2.W)))

	io.statusc.statusEn 	:= statusregc_statusEn
	io.statusc.an 		:= statusregc_an
	io.statusc.ab 		:= statusregc_ab
	io.statusc.t 		:= statusregc_t
	io.statusc.sss 		:= statusregc_sss
	io.statusc.mcs 		:= statusregc_mcs
	io.statusc.dsr 		:= statusregc_dsr
	io.statusc.dst 		:= statusregc_dst
	io.statusc.bc 		:= statusregc_bc

	val dataregRx_B_reg = RegInit(VecInit(Seq.fill(8)(0.U(8.W)))) 
	val dataregTx_B_reg = RegInit(VecInit(Seq.fill(4)(0.U(8.W)))) 
	dataregTx_B_reg := io.dataregc.dataregTx
	io.dataregc.dataregRx := dataregRx_B_reg
	
	val c2out_reg = RegInit(0.U.asTypeOf(new C2_output))
	io.c2_input := RegInit(0.U.asTypeOf(new C2_input))


	val rtl_rst_reg = RegInit(false.B)	
	io.rtl_rst := rtl_rst_reg
	
	val clockI3C = Pow2ClockDivider(4)
	io.rtl_clk := clockI3C
	
	/*
	*states for the fsm
	*/
	val initial :: i3c :: noti3c :: bw :: dw :: dr :: entdaa :: entdaarep :: privaterw :: privatew :: privater :: Nil = Enum(11)   	
	val stateReg = RegInit(initial)

	def updatedst():Unit={
		val enable = RegInit(true.B)
		val (countvalue, countwrap) = Counter(enable, 129)
		when (countvalue === 128.U){
    			statusregc_dst := 1.U
			enable := false.B
		}
		.otherwise{
    			statusregc_dst := 0.U
		}
	}
	def tbitcalculator(data: UInt):UInt = {
		val allone:Bool = data === "hff".U
		val allzero:Bool = data === 0.U
                val n = RegInit(0.U(1.W))
                when(allone || allzero){
                        n := 1.U
                }
                .otherwise{
                        n := data ^ 1.U
                }
                n
        }
	def datawithTbit(data:UInt):Unit={
		val tbit = tbitcalculator(data)
		io.c2_input.dataTx := Cat(data,tbit)
		updatedst()
	}
	def datawithTbitrep():Unit = {
		when(dataregTx_B_reg(0) =/= 0.U){
			datawithTbit(dataregTx_B_reg(0))
			when(dataregTx_B_reg(1) =/= 0.U){
				datawithTbit(dataregTx_B_reg(1))
				when(dataregTx_B_reg(2) =/= 0.U){
					datawithTbit(dataregTx_B_reg(2))
					when(dataregTx_B_reg(3) =/= 0.U){
						datawithTbit(dataregTx_B_reg(3))
					}
					.otherwise{
						dataregTx_B_reg(3) := 0.U
					}
				}
				.otherwise{
					dataregTx_B_reg(2) := 0.U
				}
			}
			.otherwise{
				dataregTx_B_reg(1) := 0.U
			}
		}
		.otherwise{
			dataregTx_B_reg(0) := 0.U 
		}
	}
	

	def readdata(data:UInt):Unit = {
		val enable = RegInit(true.B)
		val (countvalue,countwrap) = Counter(enable, 145)
		when(countvalue === 144.U){
			data := c2out_reg.dataRx(8,1)
			statusregc_t := c2out_reg.dataRx(0)
			enable := false.B
		}
		.otherwise{
			data := 0.U
			statusregc_t := 0.U
		}
	}
	def readdatarep():Unit = {
		readdata(dataregRx_B_reg(0))
		when(configregc_abtR === 1.U){
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
			}
		}
		.otherwise{
			readdata(dataregRx_B_reg(1))
			when(configregc_abtR === 1.U){
				when(configregc_srP === 0.U){
					io.c2_input.repeated_start_bit := 1.U
				}
				.elsewhen(configregc_srP === 1.U){
					io.c2_input.stop_bit := 1.U
				}
			}
			.otherwise{
				readdata(dataregRx_B_reg(2))
				when(configregc_abtR === 1.U){
					when(configregc_srP === 0.U){
						io.c2_input.repeated_start_bit := 1.U
					}
					.elsewhen(configregc_srP === 1.U){
						io.c2_input.stop_bit := 1.U
					}
				}		
				.otherwise{
					readdata(dataregRx_B_reg(3))
			
				}
			}
		}

		statusregc_dsr := 1.U
		statusregc_ab := 0.U
	}
	def controlackHdelay():Unit={
		val enablecounter = RegInit(true.B)
		val (countvalue, countwrap) = Counter(enablecounter, 129)
		when(countvalue === 128.U){
			io.c2_input.acknack_H_bit := 1.U
			enablecounter := false.B		
		}
		.otherwise{
			io.c2_input.acknack_H_bit := 0.U
		}		

	}
	def updateackB():Unit={
		val enablecounter = RegInit(true.B)
		val (countvalue, countwrap) = Counter(enablecounter, 17)
		when(countvalue === 16.U){
			statusregc_an := c2out_reg.acknack_rcvd_bit
			enablecounter := false.B
		}
		.otherwise{
			statusregc_an := 0.U
		}
	}
	def controlacknoHdelay():Unit={
		val enablecounter = RegInit(true.B)
		val (countvalue, countwrap) = Counter(enablecounter, 129)
		when(countvalue === 128.U){
			io.c2_input.acknack_noH_bit := 1.U
			enablecounter := false.B		
		}
		.otherwise{
			io.c2_input.acknack_noH_bit := 0.U
		}		

	}
	
	
	def broadcastwriteCCC():Unit = {
		when(configregc_s === 1.U){
			io.c2_input.start_bit := 1.U
		}
			when(dataregTx_B_reg(0) =/= 0.U){
				io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
				controlackHdelay()
				updateackB()
				dataregTx_B_reg(0) := 0.U
				datawithTbitrep()
			}
			.otherwise{
				dataregTx_B_reg(0) := 0.U
				dataregTx_B_reg(1) := 0.U
				dataregTx_B_reg(2) := 0.U
				dataregTx_B_reg(3) := 0.U
			}
	}
	def directRW(rw :Bool):Unit = {
		when(configregc_s === 1.U){
			io.c2_input.start_bit := 1.U
		}
			when(dataregTx_B_reg(0) =/= 0.U){
				io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
				controlackHdelay()
				updateackB()
				dataregTx_B_reg(0) := 0.U
				when(dataregTx_B_reg(0) =/= 0.U){
					datawithTbit(dataregTx_B_reg(0))
					dataregTx_B_reg(0) := 0.U
					when(configregc_srP === 0.U){
						io.c2_input.repeated_start_bit := 1.U
						when(dataregTx_B_reg(0) =/= 0.U){
							io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
							when(!rw){
								controlackHdelay()
								updateackB()
								dataregTx_B_reg(0) := 0.U
								datawithTbitrep()
							}
							.otherwise{
								controlacknoHdelay()
								updateackB()
								dataregTx_B_reg(0) := 0.U
								readdatarep()
							}		
						}
						.otherwise{
							//delay
						}
					}
					.otherwise{
						//delay
					}
						
				}
				.otherwise{
					//delay
				}
			}
			.otherwise{
				//delay
			}
	}
	def privateRW(rw :Bool):Unit = {
		when(configregc_s === 1.U){
			io.c2_input.start_bit := 1.U
		}
			when(dataregTx_B_reg(0) =/= 0.U){
				io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
				controlackHdelay()
				updateackB()
				dataregTx_B_reg(0) := 0.U
				when(configregc_srP === 0.U){
					io.c2_input.repeated_start_bit := 1.U
					when(dataregTx_B_reg(0) =/= 0.U){
						io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
						when(!rw){
							controlackHdelay()
							updateackB()
							dataregTx_B_reg(0) := 0.U
							datawithTbitrep()
							}
						.otherwise{
							controlacknoHdelay()
							updateackB()
							dataregTx_B_reg(0) := 0.U
							readdatarep()
							}		
					}
					.otherwise{
						//delay
					}
				}
				.otherwise{
					//delay
				}
						
			}
			.otherwise{
				//delay
			}
	}
	def slaveinfo():Unit = {
		readdata(dataregRx_B_reg(0))
		when(configregc_abtR === 1.U){
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
			}
		}
		.otherwise{
			readdata(dataregRx_B_reg(1))
			when(configregc_abtR === 1.U){
				when(configregc_srP === 0.U){
					io.c2_input.repeated_start_bit := 1.U
				}
				.elsewhen(configregc_srP === 1.U){
					io.c2_input.stop_bit := 1.U
				}
			}
			.otherwise{
				readdata(dataregRx_B_reg(2))
				when(configregc_abtR === 1.U){
					when(configregc_srP === 0.U){
						io.c2_input.repeated_start_bit := 1.U
					}
					.elsewhen(configregc_srP === 1.U){
						io.c2_input.stop_bit := 1.U
					}
				}		
				.otherwise{
					readdata(dataregRx_B_reg(3))
					when(configregc_abtR === 1.U){
						when(configregc_srP === 0.U){
							io.c2_input.repeated_start_bit := 1.U
						}
						.elsewhen(configregc_srP === 1.U){
							io.c2_input.stop_bit := 1.U
						}
				
					}
					.otherwise{
						readdata(dataregRx_B_reg(4))
						when(configregc_abtR === 1.U){
							when(configregc_srP === 0.U){
								io.c2_input.repeated_start_bit := 1.U
							}
							.elsewhen(configregc_srP === 1.U){
								io.c2_input.stop_bit := 1.U
							}
						}
						.otherwise{
							readdata(dataregRx_B_reg(5))
							when(configregc_abtR === 1.U){
								when(configregc_srP === 0.U){
									io.c2_input.repeated_start_bit := 1.U
								}
								.elsewhen(configregc_srP === 1.U){
									io.c2_input.stop_bit := 1.U
								}
							}		
							.otherwise{
								readdata(dataregRx_B_reg(6))
								when(configregc_abtR === 1.U){
									when(configregc_srP === 0.U){
										io.c2_input.repeated_start_bit := 1.U
									}
									.elsewhen(configregc_srP === 1.U){
										io.c2_input.stop_bit := 1.U
									}
								}
								.otherwise{
								readdata(dataregRx_B_reg(7))
								}
							}
						}
					}
				}
			}
		}
		statusregc_dsr := 1.U
		statusregc_ab := 1.U
	}
	def entdaarepitition():Unit = {		
		when(configregc_srP === 0.U){
			io.c2_input.repeated_start_bit := 1.U
		
			when(dataregTx_B_reg(0) =/= 0.U){
				io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
				controlackHdelay()
				updateackB()
				dataregTx_B_reg(0) := 0.U
				slaveinfo()
				//delay
				when(dataregTx_B_reg(0) =/= 0.U){
					io.c2_input.dataTx := Cat(dataregTx_B_reg(0), 0.U)
					controlackHdelay()
					updateackB()
				}
				.otherwise{
					//delay
				}
			}
			.otherwise{
				//delay
			}
		}
		.otherwise{
			//delay
		}
	}
	def entdaaprocess():Unit = {
		when(configregc_s === 1.U){
			io.c2_input.start_bit := 1.U
		}
		when(dataregTx_B_reg(0) =/= 0.U){
			io.c2_input.dataTx := Cat(dataregTx_B_reg(0),0.U)
			controlackHdelay()
			updateackB()
			dataregTx_B_reg(0) := 0.U
			when(dataregTx_B_reg(0) =/= 0.U){
				datawithTbit(dataregTx_B_reg(0))
				dataregTx_B_reg(0) := 0.U
			}
			.otherwise{
				//delay
			}
			}
		.otherwise{
			//delay
		}
	}
	
	switch (stateReg){
		is (initial){
			when(configregc_configEn){
				when(configregc_ini === 0.U){
					stateReg := i3c
				}
				.otherwise{
					stateReg := noti3c
				}
			}
			.otherwise{
				//delay
			}
		}
		
		is (i3c){
			when(configregc_sdr === 1.U){
				statusregc_statusEn := true.B
				when(configregc_cccNccc === 1.U){
					when(configregc_bdccc === 1.U && configregc_rw === 0.U){
						stateReg := bw			
					}
					.elsewhen(configregc_bdccc === 0.U && configregc_rw === 0.U){
						stateReg := dw
					}
					.elsewhen(configregc_bdccc === 0.U && configregc_rw === 1.U){
						stateReg := dr
					}
					.otherwise{
						//delay
					}
				}
				.elsewhen(configregc_cccNccc === 0.U && configregc_entdaa === 0.U){
					stateReg := privaterw
				}
				.elsewhen(configregc_cccNccc === 0.U && configregc_entdaa === 1.U){
					stateReg := entdaa
				}
				.otherwise{
					//delay
				}
			}
		}
		
		is (noti3c){
		}

		is (bw){
			broadcastwriteCCC()
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
				stateReg := bw
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}

		is (dw){
			directRW(false.B)
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
				stateReg := dw
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}

		is (dr){
			directRW(true.B)
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
				stateReg := dr
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}

		is (privaterw){
			when(configregc_rw === 0.U){
				stateReg := privatew
			}
			.elsewhen(configregc_rw === 1.U){
				stateReg := privater
			}
			.otherwise{
			//delay
			}

		}

		is (privatew){
			privateRW(false.B)
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
				stateReg := privatew
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}

		is (privater){
			privateRW(true.B)
			when(configregc_srP === 0.U){
				io.c2_input.repeated_start_bit := 1.U
				stateReg := privater
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}

		is (entdaa){
			entdaaprocess()
			stateReg := entdaarep
					
		}
		
		
		is (entdaarep){
			entdaarepitition()
			when(configregc_srP === 0.U){
				stateReg := entdaarep
			}
			.elsewhen(configregc_srP === 1.U){
				io.c2_input.stop_bit := 1.U
				stateReg := initial
			}
			.otherwise{
				//delay
			}
		}
		

	}
	 
}	
