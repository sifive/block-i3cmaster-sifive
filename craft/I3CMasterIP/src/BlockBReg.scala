package sifive.blocks.iiicmaster

import chisel3._
import chisel3.util._

class Tisr1 extends Bundle {
  val readValid	 = Bool()
  val status_GA	 = Bool()
  val pending_RN = Bool()
  val status_IBI = Bool()
  val accept_ADB = Bool()
  val payload_IBI = Bool()
  val ack_IBI	 = Bool()
  val hdrMode	 = UInt(2.W)
  val hdrInvalid = Bool()
  val hdr_BT	 = Bool()
  val hdr_DDR	 = Bool()
  val hdr_TSL	 = Bool()
  val hdr_TSP	 = Bool()
  val slaveMasterReq = Bool()
  val da_assigned = UInt(4.W)
  val error	 = Bool()
  val collision	 = Bool() 
  val ibi = Bool()
  val hotJoin = Bool()
  val ack_slv = Bool()
  val busBusy = Bool()
}

class Tisr2 extends Bundle {
  val errorM0 = Bool()
  val errorM2 = Bool()
  val errorM3 = Bool()
  val masterReqToPrevMaster = Bool()
  val accept_MasterReq	 = Bool()
  val nextMasterID	 = UInt(4.W)
  val presentMasterID	 = UInt(4.W)
}

class Sdrf extends Bundle {
  val dynamicAddr	= UInt(7.W)
  val valid 		= Bool()
  val bcr_load 		= Bool()
  val bcr		= UInt(8.W)
  val grpAddrCaps	= UInt(2.W)
  val ibi_prn 		= Bool()
  val getStatus_DB 	= Bool()
  val getCaps_DB 	= Bool()
  val hdr_BT 		= Bool()
  val hdr_DDR 		= Bool()
  val hdr_TSL 		= Bool()
  val hdr_TSP 		= Bool()
}
