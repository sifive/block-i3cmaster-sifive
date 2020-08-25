package sifive.blocks.i3cmaster

import chisel3._
import chisel3.util._
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
import freechips.rocketchip.util._

import chisel3.experimental._

case object I3CMasterKey extends Field[Seq[I3CMasterParams]]

case class I3CMasterParams(
 
	beatBytes:	  Int = 4,
	address:          BigInt
)



case class OMI3CMasterParams(
	i3cmaster: I3CMasterParams,
	memoryRegions: Seq[OMMemoryRegion],
	interrupts : Seq[OMInterrupt],
	_types: Seq[String] = Seq("OMI3CMaster","OMDevice","OMComponent","OMCompoundType") 
)extends OMDevice

class I3CMaster(params: I3CMasterParams)(implicit p: Parameters) extends LazyModule
{
	
  val device = new SimpleDevice("i3cmaster", Seq("sifive,i3cmaster0")) 

  val controlNode = TLRegisterNode(
		address = Seq(AddressSet(params.address, 0xffff)),
		device = device,
		beatBytes = params.beatBytes)


lazy val module = new LazyModuleImp(this){


 val mfsm		= Module(new MasterFSM)
 val blockc1		= Module(new I3CMaster_C1)
 val blockc2		= LazyModule(new I3CMaster_C2)
// Inputs to MasterFSM from Block C
// mfsm.io.statusC	:= WireDefault(0.U.asTypeOf(new StatusC))
   mfsm.io.statusC	<> blockc1.io.statusc
//  mfsm.io.configC := WireDefault(0.U.asTypeOf(new ConfigC))
//   mfsm.io.configC := RegInit(0.U.asTypeOf(new ConfigC))
   blockc1.io.configc   <> mfsm.io.configC
// mfsm.io.dataregC.dataregRx := VecInit(Seq.fill(8) {0.U(8.W)})
   mfsm.io.dataregC.dataregRx := blockc1.io.dataregc.dataregRx
   blockc1.io.dataregc.dataregTx :=  mfsm.io.dataregC.dataregTx
// c2 outputs 
   blockc1.io.c2_output := WireDefault(0.U.asTypeOf(new C2_output))
      
 mfsm.io.dataA.write_data := 0.U
 mfsm.io.configA.abort := 0.B

 val stataddr_slv0	= RegInit(0.U(7.W))
 val slv_typ0		= RegInit(0.U(2.W))

 val stataddr_slv1	= RegInit(0.U(7.W))
 val slv_typ1		= RegInit(0.U(2.W))

 val stataddr_slv2	= RegInit(0.U(7.W))
 val slv_typ2		= RegInit(0.U(2.W))

 val stataddr_slv3	= RegInit(0.U(7.W))
 val slv_typ3		= RegInit(0.U(2.W))

 val stataddr_slv4	= RegInit(0.U(7.W))
 val slv_typ4		= RegInit(0.U(2.W))

 val stataddr_slv5	= RegInit(0.U(7.W))
 val slv_typ5		= RegInit(0.U(2.W))

 val stataddr_slv6	= RegInit(0.U(7.W))
 val slv_typ6		= RegInit(0.U(2.W))

 val stataddr_slv7	= RegInit(0.U(7.W))
 val slv_typ7		= RegInit(0.U(2.W))

 val stataddr_slv8	= RegInit(0.U(7.W))
 val slv_typ8		= RegInit(0.U(2.W))

 val stataddr_slv9	= RegInit(0.U(7.W))
 val slv_typ9		= RegInit(0.U(2.W))

 val stataddr_slv10	= RegInit(0.U(7.W))
 val slv_typ10		= RegInit(0.U(2.W))

 val bus_reset          = RegInit(false.B)
 val chip_reset         = RegInit(false.B)
 val grpaddr_slv_en	= RegInit(0.U(11.W))
 val grpaddr_req	= RegInit(false.B)
 val mst_req_slvID	= RegInit(0.U(4.W))
 val mst_req		= RegInit(false.B)
 val set_hdr_mode	= RegInit(0.U(2.W))
 val i3c_mode		= RegInit(false.B)
 val slaveID		= RegInit(0.U(4.W))
 val readWrite		= RegInit(false.B)
 val load_done		= RegInit(false.B)
 val config_done	= RegInit(false.B)

 val total_num_slv	= RegInit(0.U(4.W))
 val num_i3c_slv_stataddr= RegInit(0.U(4.W))
 val num_i3c_slv_without_stataddr = RegInit(0.U(4.W))
 val err_detected	= RegInit(false.B)
 val collision_detected = RegInit(false.B)
 val status_grpaddr	= RegInit(false.B)
 val status_mst_req	= RegInit(false.B)
 val status_ibi		= RegInit(false.B)
 val status_hj		= RegInit(false.B)
 val read_valid		= RegInit(false.B)
 val slv_ack		= RegInit(false.B)
 val bus_busy		= RegInit(false.B)
 
 val sample = 0.U 
 
 val r_data	= RegInit(0.U(32.W))
 val w_data	= RegInit(0.U(32.W))
 mfsm.io.slvInfoA.statAddr0 		:= stataddr_slv0
 mfsm.io.slvInfoA.slvType0		:= slv_typ0

 mfsm.io.slvInfoA.statAddr1 		:= stataddr_slv1
 mfsm.io.slvInfoA.slvType1		:= slv_typ1

 mfsm.io.slvInfoA.statAddr2 		:= stataddr_slv2
 mfsm.io.slvInfoA.slvType2		:= slv_typ2

 mfsm.io.slvInfoA.statAddr3 		:= stataddr_slv3
 mfsm.io.slvInfoA.slvType3		:= slv_typ3

 mfsm.io.slvInfoA.statAddr4 		:= stataddr_slv4
 mfsm.io.slvInfoA.slvType4		:= slv_typ4

 mfsm.io.slvInfoA.statAddr5 		:= stataddr_slv5
 mfsm.io.slvInfoA.slvType5		:= slv_typ5

 mfsm.io.slvInfoA.statAddr6 		:= stataddr_slv6
 mfsm.io.slvInfoA.slvType6		:= slv_typ6

 mfsm.io.slvInfoA.statAddr7 		:= stataddr_slv7
 mfsm.io.slvInfoA.slvType7		:= slv_typ7

 mfsm.io.slvInfoA.statAddr8 		:= stataddr_slv8
 mfsm.io.slvInfoA.slvType8		:= slv_typ8

 mfsm.io.slvInfoA.statAddr9 		:= stataddr_slv9
 mfsm.io.slvInfoA.slvType9		:= slv_typ9

 mfsm.io.slvInfoA.statAddr10 		:= stataddr_slv10
 mfsm.io.slvInfoA.slvType10		:= slv_typ10

 //sending configuration to part-B

 mfsm.io.configA.bus_reset	:= bus_reset
 mfsm.io.configA.chip_reset	:= chip_reset
 mfsm.io.configA.i3c_mode	:= i3c_mode
 mfsm.io.configA.slaveId	:= slaveID
 mfsm.io.configA.readWrite	:= readWrite
 mfsm.io.configA.config_done	:= config_done
 mfsm.io.configA.load_done	:= load_done

 bus_busy 			:= mfsm.io.tisrA.busBusy
 err_detected			:= mfsm.io.tisrA.error
 collision_detected             := mfsm.io.tisrA.collision
 slv_ack			:= mfsm.io.tisrA.ack_slv
 read_valid			:= mfsm.io.tisrA.readValid
 


  val field = Seq(

      0x0 -> RegFieldGroup("Slave_Info_Register0", Some("Slave_Info_Register0"),
	 Seq(   RegField(2,slv_typ0,		RegFieldDesc("slv_typ0", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv0,	RegFieldDesc("stataddr_slv0", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0x4 -> RegFieldGroup("Slave_Info_Register1", Some("Slave_Info_Register1"),
	 Seq(   RegField(2,slv_typ1,		RegFieldDesc("slv_typ1", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv1,	RegFieldDesc("stataddr_slv1", "7 bit static address", reset=Some(0))) ,
		RegField(17))),

      0x8 -> RegFieldGroup("Slave_Info_Register2", Some("Slave_Info_Register2"),
	 Seq(   RegField(2,slv_typ2,		RegFieldDesc("slv_typ2", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv2,	RegFieldDesc("stataddr_slv2", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0xC -> RegFieldGroup("Slave_Info_Register3", Some("Slave_Info_Register3"),
	 Seq(   RegField(2,slv_typ3,		RegFieldDesc("slv_typ3", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv3,	RegFieldDesc("stataddr_slv3", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0x10 -> RegFieldGroup("Slave_Info_Register4", Some("Slave_Info_Register4"),
	 Seq(   RegField(2,slv_typ4,		RegFieldDesc("slv_typ4", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv4,	RegFieldDesc("stataddr_slv4", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0x14 -> RegFieldGroup("Slave_Info_Register5", Some("Slave_Info_Register5"),
	 Seq(   RegField(2,slv_typ5,		RegFieldDesc("slv_typ5", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv5,	RegFieldDesc("stataddr_slv5", "7 bit static address", reset=Some(0))) ,
		RegField(17))),

      0x18 -> RegFieldGroup("Slave_Info_Register6", Some("Slave_Info_Register6"),
	 Seq(   RegField(2,slv_typ6,		RegFieldDesc("slv_typ6", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv6,	RegFieldDesc("stataddr_slv6", "7 bit static address", reset=Some(0))) ,
		RegField(17))),

      0x1C -> RegFieldGroup("Slave_Info_Register7", Some("Slave_Info_Register7"),
	 Seq(   RegField(2,slv_typ7,		RegFieldDesc("slv_typ7", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv7,	RegFieldDesc("stataddr_slv7", "7 bit static address", reset=Some(0))) ,
		RegField(17))),

      0x20 -> RegFieldGroup("Slave_Info_Register8", Some("Slave_Info_Register8"),
	 Seq(   RegField(2,slv_typ8,		RegFieldDesc("slv_typ8", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv8,	RegFieldDesc("stataddr_slv8", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0x24 -> RegFieldGroup("Slave_Info_Register9", Some("Slave_Info_Register9"),
	 Seq(   RegField(2,slv_typ9,		RegFieldDesc("slv_typ9", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv9,	RegFieldDesc("stataddr_slv9", "7 bit static address", reset=Some(0))) ,
		RegField(17))),


      0x28 -> RegFieldGroup("Slave_Info_Register10", Some("Slave_Info_Register10"),
	 Seq(   RegField(2,slv_typ10,		RegFieldDesc("slv_typ10", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset=Some(0))), 
		RegField(6),
		RegField(7,stataddr_slv10,	RegFieldDesc("stataddr_slv10", "7 bit static address", reset=Some(0))) ,
		RegField(17))),



      0x3C -> RegFieldGroup("I3C_config_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(1,config_done, 	RegFieldDesc("config_done", "This is set when host has completed configuration the I3C Master")), 
		RegField(1,load_done,		RegFieldDesc("load_done","This bit is set when host has completed sending Slave info.")),
		RegField(1,readWrite,		RegFieldDesc("readWrite", "0 --> Write/1 --> Read")),
		RegField(4,slaveID,		RegFieldDesc("slaveID", "SlaveID of the currect slave", reset=Some(0))),
		RegField(1,i3c_mode,		RegFieldDesc("i3c_mode", "0 --> SDR/ 1 --> HDR" )),
		RegField(2,set_hdr_mode, 	RegFieldDesc("set_hdr_mode","2'b00:HDR-TSP,2'b01:HDR-TSL,2'b10:HDR-DDR,2'b11:HDRBulk Transport", reset=Some(0))),
		RegField(1,mst_req,		RegFieldDesc("mst_req", "To be set by the host to enable mastership request")),
		RegField(4,mst_req_slvID,	RegFieldDesc("mst_req_slvID","SlaveID of the slave that is offered mastership by the host", reset= Some(0xF))),
		RegField(2),
		RegField(1,grpaddr_req,		RegFieldDesc("grpaddr_req", "To be set by the host to enable group addressing ")),
		RegField(11,grpaddr_slv_en,	RegFieldDesc("grpaddr_slv_en", "Enable for corresponding slave to participate in Group Addressing", reset=Some(0))),
		RegField(1,chip_reset,		RegFieldDesc("chip_reset", "If set entire system is reset")),
		RegField(1,bus_reset,		RegFieldDesc("bus_reset","If set state is changed to DAA")))),

      0x40 -> RegFieldGroup("bus_status_reg", Some("Specifies the general status of I3C Master"),
	 Seq(   RegField.r(1,bus_busy, 		RegFieldDesc("bus_busy", "0 --> Can accept new req from host;1 --> busy in processing previous req" )), 
		RegField.r(1,slv_ack,		RegFieldDesc("slv_ack", "Previous request was served 0 --> unsuccessful/ 1 -->successful" )),
		RegField.r(1,read_valid,	RegFieldDesc("read_valid", "set by I3C Master when Read Data Register contains valid data requested by the Host" )),
		RegField.r(1,status_hj,		RegFieldDesc("status_hj", "Hotjoined 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_ibi,	RegFieldDesc("status_ibi", "In-band Interrupt req 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_mst_req,	RegFieldDesc("status_mst_req", "Mastership req 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_grpaddr,	RegFieldDesc("status_grpaddr", "Group Addressing req 0-->not occured ; 1-->occured" )),
		RegField.w1ToClear(1,collision_detected,sample,Some(RegFieldDesc("collision_detected", "0 --> Collision not detected; 1 --> Collision detected" ))),
		RegField.w1ToClear(1,err_detected,sample,Some(RegFieldDesc("err_detected", "0 -->No error detected; 1 --> Error detected" ))),
		RegField(11),
		RegField.r(4,num_i3c_slv_without_stataddr,RegFieldDesc("num_i3c_slv_without_stataddr", "Number of I3C slaves without static address", reset=Some(0))),
		RegField.r(4,num_i3c_slv_stataddr,RegFieldDesc("num_i3c_slv_stataddr", "Number of I3C slaves with static address", reset=Some(0))),
		RegField.r(4,total_num_slv,		RegFieldDesc("total_num_slv","Total number of slaves", reset=Some(0))))),

      0x70 -> RegFieldGroup("Read_data_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(32,r_data, 		RegFieldDesc("r_data", "It is used to store the data that is read from the slave", reset=Some(0))))),


      0x74 -> RegFieldGroup("Write_data_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(32,w_data,		 RegFieldDesc("w_data", "It is used to store the data that is to be written to the slave", reset=Some(0))))))

controlNode.regmap(field : _*)
}

  lazy val ltnode = new LogicalTreeNode(() => Some(device)) {
    def getOMComponents(resourceBindings: ResourceBindings, children: Seq[OMComponent] = Nil): Seq[OMComponent] = {
      val compname = device.describe(resourceBindings).name
      val regions = DiplomaticObjectModelAddressing.getOMMemoryRegions(compname, resourceBindings, Some(OMRegister.convert(module.field: _*)))
      val intr = DiplomaticObjectModelAddressing.describeGlobalInterrupts(compname, resourceBindings)
	Seq(OMI3CMasterParams(i3cmaster = params, memoryRegions = regions,interrupts = intr))
    }
  }

}

case class I3CMasterAttachParams(
 i3cmaster : I3CMasterParams,
 controlBus : TLBusWrapper
 
) 

object I3CMaster {

 val nextId = { var i = -1; () => { i += 1; i}}

 def attach(params : I3CMasterAttachParams)(implicit p: Parameters): I3CMaster = {
	val name = s"i3cmaster_${nextId()}"	
	val i3cmaster = LazyModule(new I3CMaster(params.i3cmaster.copy(beatBytes = params.controlBus.beatBytes)))
	i3cmaster.suggestName(name)


//params.controlBus.coupleTo(name) { i3cmaster.controlNode := TLFragmenter(params.controlBus) :=  TLWidthWidget(params.controlBus) := _ }
 
 	 params.controlBus.coupleTo(name) { i3cmaster.controlNode := TLWidthWidget(params.controlBus):= _ }
	i3cmaster

}
}

// BlockBReg.scala

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

// Bundles.scala

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
class ConfigC extends Bundle{
	val configEn	= Bool()
	val as 		= UInt(2.W)
	val abtR 	= UInt(1.W)
	val hep 	= UInt(1.W)
	val hdrM 	= UInt(2.W)
	val bdccc	= UInt(1.W)
	val rw		= UInt(1.W)
	val sdr		= UInt(1.W)
	val ini		= UInt(1.W)
	val asss	= UInt(1.W)
	val srP		= UInt(1.W)
	val s		= UInt(1.W)
	val cccNccc	= UInt(1.W)  
	val entdaa	= UInt(1.W)
}


class StatusC extends Bundle{
	val statusEn	= Bool()
	val an		= UInt(1.W)
	val ab		= UInt(1.W)
	val t 		= UInt(1.W)	
	val sss		= UInt(1.W)
	val mcs		= UInt(2.W)
	val dsr		= UInt(1.W)
	val dst		= UInt(1.W)
	val bc		= UInt(2.W)
}

class DataregC extends Bundle{	
	val dataregTx = Input(Vec(4, UInt(8.W)))
	val dataregRx = Output(Vec(8, UInt(8.W)))
}
/*
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
*/

// Sorting.scala

class Sorting extends Module {
val io = IO(new Bundle{
// val nsetdasa = Input(UInt(4.W))
 val unsort = Input(Vec(11,UInt(7.W)))
// val outunsort = Output(Vec(11,UInt(7.W)))
// val outsort = Output(Vec(11,UInt(7.W)))
//   val count_i = Output(UInt(4.W))
//   val count_p = Output(UInt(4.W))
     val sort = Output(Vec(11,UInt(7.W)))
     val load = Input(Bool())
     val sort_done = Output(Bool())
})

 
val initvalues = Seq.fill(11){ 127.U(7.W)}
val unsortReg = Reg(Vec(11,UInt()))
val sortReg = RegInit(VecInit(initvalues))
io.sort := unsortReg
io.sort_done := 0.B

val load_done = RegInit(0.U(1.W))
val count_iter = RegInit(0.U(4.W))
val count_pass = RegInit(0.U(4.W))

when(io.load && load_done === 0.U )
{

   unsortReg(10) := io.unsort(10)
   unsortReg(9) := io.unsort(9)
   unsortReg(8) := io.unsort(8)
   unsortReg(7) := io.unsort(7)
   unsortReg(6) := io.unsort(6)
   unsortReg(5) := io.unsort(5)
   unsortReg(4) := io.unsort(4)
   unsortReg(3) := io.unsort(3)
   unsortReg(2) := io.unsort(2)
   unsortReg(1) := io.unsort(1)
   unsortReg(0) := io.unsort(0)
   load_done := 1.U


}.elsewhen( count_pass < 10.U && load_done === 1.U && io.load )
{
    when( count_iter < 11.U ) 
    {
	 when(unsortReg(10) > unsortReg(9))
	{
	unsortReg(9) := unsortReg(8)
	unsortReg(8) := unsortReg(7)
	unsortReg(7) := unsortReg(6)
	unsortReg(6) := unsortReg(5)
	unsortReg(5) := unsortReg(4)
	unsortReg(4) := unsortReg(3)
	unsortReg(3) := unsortReg(2)
	unsortReg(2) := unsortReg(1)
	unsortReg(1) := unsortReg(0)
	unsortReg(0) := "hFF".U

	sortReg(10) := sortReg(9)
	sortReg(9) := sortReg(8)
	sortReg(8) := sortReg(7)
	sortReg(7) := sortReg(6)
	sortReg(6) := sortReg(5)
	sortReg(5) := sortReg(4)
	sortReg(4) := sortReg(3)
	sortReg(3) := sortReg(2)
	sortReg(2) := sortReg(1)
	sortReg(1) := sortReg(0)
	sortReg(0) := unsortReg(9)

	count_iter := count_iter + 1.U

	}.elsewhen(unsortReg(10) <= unsortReg(9))
	{
	unsortReg(10) := unsortReg(9)
	unsortReg(9) := unsortReg(8)
        unsortReg(8) := unsortReg(7)
        unsortReg(7) := unsortReg(6)
        unsortReg(6) := unsortReg(5)
        unsortReg(5) := unsortReg(4)
        unsortReg(4) := unsortReg(3)
        unsortReg(3) := unsortReg(2)
        unsortReg(2) := unsortReg(1)
        unsortReg(1) := unsortReg(0)
        unsortReg(0) := "hFF".U

	sortReg(10) := sortReg(9)
	sortReg(9) := sortReg(8)
	sortReg(8) := sortReg(7)
	sortReg(7) := sortReg(6)
	sortReg(6) := sortReg(5)
	sortReg(5) := sortReg(4)
	sortReg(4) := sortReg(3)
	sortReg(3) := sortReg(2)
	sortReg(2) := sortReg(1)
	sortReg(1) := sortReg(0)
	sortReg(0) := unsortReg(10)

	count_iter := count_iter + 1.U
	}

    }.elsewhen(count_iter === 11.U)
	{
		unsortReg := sortReg
		count_iter := 0.U
		count_pass := count_pass + 1.U

	}

}.elsewhen(count_pass === 10.U)
	{
		io.sort_done := 1.B
		io.sort := sortReg
	}


//io.sort := sortReg

//io.count_i := count_iter
//io.count_p := count_pass
//io.outsort := sortReg
//io.outunsort := unsortReg






}

// DaaFSM.scala

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
		//	is("h00000000000000".U) { dynAddr := "h30".U }
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


class FinalBundle extends Bundle {
 val tisrA	 = Output(new TisrA)
 val configA	 = Input(new ConfigA)
 val slvInfoA	 = Input(new SlaveInfoA) 
 val configC	 = Output(new ConfigC)
 val statusC	 = Input(new StatusC)
 val dataregC	 = Flipped(new DataregC)

 val daaEn	 = Input(UInt(3.W))
 val daaDone	 = Output(Bool())
 val sdrfout = Output(Vec(11,UInt(32.W)))

// For checking 
  val cdasa = Output(UInt(4.W))
  val caasa = Output(UInt(4.W))
  val cdaa = Output(UInt(4.W))
  val rinfo = Output(UInt(1.W))
  val unsortvec = Output(Vec(11,UInt(7.W)))
  val sortvecout = Output(Vec(11,UInt(7.W)))
  val st = Output(UInt(2.W))
  val sa = Output(UInt(7.W))
  val s = Output(UInt(9.W))
  val sortingdone = Output(Bool())
  val loadinfo = Output(Bool())
  val slaveinforeg = Output(Vec(11,UInt(9.W)))
  val i3cnum = Output(UInt(4.W))
  val en_ack7E = Output(Bool())
  val en_tb = Output(Bool())
  val en_hp = Output(Bool())
  val ret_ack7E = Output(Bool())
  val ret_tb = Output(Bool())
  val ret_hp = Output(Bool())
  val ret_value1 = Output(UInt(5.W))
  val ret_value2 = Output(UInt(5.W))
  val ret_value3 = Output(UInt(5.W))
  val bdraddr = Output(Bool())
  val codesent = Output(Bool())
  val hepreg = Output(Bool())

  val countvarout = Output(UInt(4.W))
  val countsortout = Output(UInt(4.W))
  val dasaout = Output(UInt(7.W))
  val sentStat = Output(Bool())
  val indexout = Output(UInt(4.W))
//  val sdrfout = Output(Vec(11,UInt(32.W)))
  val slvout = Output(UInt(9.W))
  val stypeout = Output(Vec(11,UInt(1.W)))
  val stadd = Output(UInt(7.W))
  val slcon = Output(UInt(2.W))
  val updatesdrfout = Output(Bool())
  val stateRegout = Output(UInt(4.W))
  val DAsuccessout = Output(UInt(4.W))
  val sent7eout = Output(Bool())
  val cdaaout = Output(UInt(4.W))
  val countcheck = Output(UInt(8.W))
  val pidout = Output(UInt(48.W))
  val bcrout = Output(UInt(8.W))
  val dcrout = Output(UInt(8.W))
  val read64bitsout = Output(UInt(8.W))
  val selectDA = Output(Bool())
  val sentDynOut = Output(Bool())

  val dynout = Output(UInt(7.W))
  val slaveindexout = Output(UInt(4.W))
  val countNACKout = Output(UInt(4.W))
  val collisioncountout = Output(UInt(3.W)) 
  val readslvidx = Output(Bool())
}

class DaaFSM extends Module {

val io = IO(new FinalBundle)


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
val sdrf	 = RegInit(VecInit(Seq.fill(11) {0.U(32.W)}))
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

// For checking
io.en_ack7E := 0.B
io.en_tb := 0.B
io.en_hp := 0.B
io.ret_ack7E := 0.B
io.ret_tb := 0.B
io.ret_hp := 0.B
io.ret_value1 := 0.U
io.ret_value2 := 0.U
io.ret_value3 := 0.U

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
	sdrf	 	 := VecInit(Seq.fill(11) {0.U(32.W)})
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
          			 	stype(countFor-1.U):= 1.U
          			 	countDASA := countDASA + 1.U
          			 	unsort(countFor-1.U) := slvReg(8,2)
					totalI3CSlvs := totalI3CSlvs + 1.U
        			}.otherwise {
					totalI3CSlvs := totalI3CSlvs + 1.U
          				stype(countFor-1.U) := 0.U
          				countAASA := countAASA + 1.U
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

		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U


		val en_ack_7E	    = stateReg === setaasa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E) = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	  = stateReg === setaasa && sentCode && sentBDAddr
		val (value2,tbit) = DaaFSM.tBitDelay(en_tbit)

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
			sentCode := 1.B

		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= setdasa
			countDASuccess	:= countAASA	
			io.configC.srP	:= 0.U
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}
		//For Checking
		io.en_ack7E := en_ack_7E
		io.en_tb := en_tbit
		io.en_hp := en_hep

		io.ret_ack7E := ack_7E
		io.ret_tb := tbit
		io.ret_hp := hepDone
	 	io.ret_value1 := value1
 		io.ret_value2 := value2
		io.ret_value3 := value3

	}

	is(setdasa){

		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U
	
		val en_ack = (stateReg === setdasa  && sentBDAddr && !sentCode && !hep) || (sentStatAddr)
                val (value1,ack) = DaaFSM.ackHandOff(en_ack)

                val en_tbit = stateReg === setdasa && sentCode && sentBDAddr && !sentStatAddr
                val (value2,tbit) = DaaFSM.tBitDelay(en_tbit)

                val en_hep = stateReg === setdasa && hep && sentBDAddr
                val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc := 1.U
                      //  io.configC.s	 := 1.U
                        io.configC.rw	 := 0.U
                        io.configC.cccNccc := 1.U
                        io.dataregC.dataregTx(0) := "hFC".U
                        sentBDAddr := 1.B			
	
		}.elsewhen(io.statusC.an === 0.U && ack && !sentStatAddr){
                        io.dataregC.dataregTx(0) := "h87".U
                        sentCode := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack && !sentStatAddr){
                        io.tisrA.errorM2 := 1.B
                        io.configC.hep 	 := 1.U
                        hep := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr := 0.B
                        sentCode   := 0.B
                        io.tisrA.errorM2 := 0.B
                        hep := 0.B
                }.elsewhen(countvar < countDASA && sentCode){
			when(io.statusC.dst === 1.U && tbit){
                       		io.configC.srP := 0.U
                		io.dataregC.dataregTx(0) := Cat(sortvec(countsort),0.U)
         	               	sentStatAddr   := 1.B	        
         			       		
				   
                	}.elsewhen(io.statusC.an === 0.U && ack){
				io.dataregC.dataregTx(0) := Cat(dasaSA,0.U)
                                countsort := countsort - 1.U
                                dasaSA	  := dasaSA + 1.U
                                countvar  := countvar + 1.U
                                countDASuccess := countDASuccess + 1.U
                                sentStatAddr   := 0.B
	
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
		//For Checking
		io.en_ack7E := en_ack
		io.en_tb := en_tbit
		io.en_hp := en_hep

		io.ret_ack7E := ack
		io.ret_tb := tbit
		io.ret_hp := hepDone
	 	io.ret_value1 := value1
 		io.ret_value2 := value2
		io.ret_value3 := value3
	
	}
	
	is(updateSDRF){
		io.configC.configEn := 0.B
		when(index < 12.U){
			slvalueReg := slvInfoReg(index)
	             	when(slvalueReg(1,0)==="b11".U){
				when(stype(index-1.U)===1.U){  //SETDASA			
				
					when(slvalueReg(8,2) === sortvec(10)){
						sdrf(index-1.U):= "h00000088".U

					}.elsewhen(slvalueReg(8,2) === sortvec(9)){
						sdrf(index-1.U):= "h00000089".U

					}.elsewhen(slvalueReg(8,2) === sortvec(8)){
						sdrf(index-1.U):= "h0000008A".U

					}.elsewhen(slvalueReg(8,2) === sortvec(7)){
						sdrf(index-1.U):= "h0000008B".U

					}.elsewhen(slvalueReg(8,2) === sortvec(6)){
						sdrf(index-1.U):= "h0000008C".U
					
					}.elsewhen(slvalueReg(8,2) === sortvec(5)){

						sdrf(index-1.U):= "h0000008D".U	
					}.elsewhen(slvalueReg(8,2) === sortvec(4)){

						sdrf(index-1.U):= "h0000008E".U
					}.elsewhen(slvalueReg(8,2) === sortvec(3)){

						sdrf(index-1.U):= "h0000008F".U
					}.elsewhen(slvalueReg(8,2) === sortvec(2)){

						sdrf(index-1.U):= "h00000090".U
					}.elsewhen(slvalueReg(8,2) === sortvec(1)){

						sdrf(index-1.U):= "h00000091".U
					}.elsewhen(slvalueReg(8,2) === sortvec(0)){
						sdrf(index-1.U):= "h00000092".U
					}
				  
	
				}.otherwise{   //SETAASA

					sdrf(index-1.U) := Cat("h000000".U,1.U,slvalueReg(8,2))

				}

			}	
			
			index := index + 1.U
			
		}.elsewhen(index === 12.U){
			updatesdrf := 1.B

		}


		when(updatesdrf){
			index	 := 0.U
			stateReg := entdaa

		}

	}

	is(entdaa){
		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U

		val en_ack_7E	    = stateReg === entdaa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E) = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	  = stateReg === entdaa && sentCode && sentBDAddr
		val (value2,tbit) = DaaFSM.tBitDelay(en_tbit)

		val en_hep	     = stateReg === entdaa && hep && sentBDAddr
		val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc := 1.U
			io.configC.s	 := 1.U
	//		io.configC.rw	 := 0.U
			io.configC.cccNccc := 1.U
			io.dataregC.dataregTx(0) := "hFC".U
			sentBDAddr	 := 1.B
	
		}.elsewhen(io.statusC.an === 0.U && ack_7E){
			io.dataregC.dataregTx(0) := "h07".U
			io.configC.entdaa := 1.U
			sentCode := 1.B
			
		
		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= send7Eread	
			io.configC.srP	:= 0.U
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}
		//For Checking
		io.en_ack7E := en_ack_7E
		io.en_tb := en_tbit
		io.en_hp := en_hep

		io.ret_ack7E := ack_7E
		io.ret_tb := tbit
		io.ret_hp := hepDone
	 	io.ret_value1 := value1
 		io.ret_value2 := value2
		io.ret_value3 := value3

	}


	is(send7Eread){
		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U
		val en_ack = stateReg === send7Eread && sent7eRead
                val (value1,ack_DA) = DaaFSM.ackWithoutHandOff(en_ack)
		when(!sent7eRead){
			io.configC.srP 	:= 0.U
			io.configC.rw 	:= 1.U
			io.configC.cccNccc := 0.U
			io.dataregC.dataregTx(0) := "hFD".U
			sent7eRead 	:= 1.B
		}.elsewhen(io.statusC.an === 0.U && ack_DA && countDaa < countEntDAA){
			stateReg   	:= read64  
			sent7eRead	:= 0.B
		}.elsewhen(io.statusC.an === 1.U && ack_DA && countDaa === countEntDAA){
			stateReg 	:= stopCD
			sent7eRead 	:= 0.B
		}				
	//For Checking
	io.en_ack7E := en_ack
	io.ret_ack7E := ack_DA
	io.ret_value1 := value1
	}

	is(read64){
		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U
		
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
                val en_ack = stateReg === updateDaaSDRF && sentDynAddr
		val (value1,ack_DA) = DaaFSM.ackWithoutHandOff(en_ack)
		when(!readSlvIdx){
			slaveIndex := DaaFSM.lookUpTable(dcr)
			readSlvIdx := 1.B
		}.elsewhen(io.statusC.an === 0.U && ack_DA && readSlvIdx){

			sdrf(slaveIndex) := Cat("h0000".U,bcr,"b11".U,dynamicAddr)
		//	dynamicAddr	 := dynamicAddr + 4.U			
			countDASuccess	 := countDASuccess + 1.U
			stateReg	 := send7Eread
			readSlvIdx	 := 0.B
			sentDynAddr	 := 0.B
			countDaa	:= countDaa + 1.U
		}.elsewhen(io.statusC.an === 1.U && ack_DA && readSlvIdx){
			sentDynAddr	 := 0.B
			readSlvIdx	 := 0.B
			when(countNACK === 0.U){
				countNACK := countNACK + 1.U
			 	stateReg  := send7Eread
				
			}.elsewhen(countNACK === 1.U){
				io.tisrA.error 	 := 1.B
				stateReg := initialization
	//			stateReg := start  In main I3C master FSM
				
			} 


		}
	//For Checking
	io.en_ack7E := en_ack
	io.ret_ack7E := ack_DA
	io.ret_value1 := value1

	}

	is(stopCD){
		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U
		
		io.configC.srP := 1.U
			
		when(countDASuccess === totalI3CSlvs){
			io.daaDone	 := 1.B
			io.tisrA.da_assigned := countDASuccess
			stateReg	 := initialization
			//	stateReg := start  In main I3C master FSM

		}.otherwise{
			when(collisionCount < 3.U){
				collisionCount 	:= collisionCount + 1.U
				stateReg	:= rstdaa
			}.otherwise{
				io.tisrA.collision := 1.B
				stateReg := initialization
			//	stateReg := start  In main I3C master FSM
			}
		

		}

	}

	
	is(rstdaa){
		io.configC.configEn := 1.B
		io.configC.sdr	:= 1.U
		io.configC.ini  := 0.U
		val en_ack_7E	    = stateReg === rstdaa  && sentBDAddr && !sentCode && !hep
		val (value1,ack_7E) = DaaFSM.ackHandOff(en_ack_7E)

		val en_tbit	  = stateReg === rstdaa && sentCode && sentBDAddr
		val (value2,tbit) = DaaFSM.tBitDelay(en_tbit)

		val en_hep	     = stateReg === rstdaa && hep && sentBDAddr
		val (value3,hepDone) = DaaFSM.hdrExitPattern(en_hep)

		when(!sentBDAddr){
			io.configC.bdccc := 1.U
			io.configC.s	 := 1.U
			io.configC.rw	 := 0.U
			io.configC.cccNccc := 1.U
			io.dataregC.dataregTx(0) := "hFC".U
			sentBDAddr	 := 1.B
	
		}.elsewhen(io.statusC.an === 0.U && ack_7E){
			io.dataregC.dataregTx(0) := "h06".U
			sentCode := 1.B

		}.elsewhen(io.statusC.an === 1.U && ack_7E){
			io.tisrA.errorM2 := 1.B
			io.configC.hep	 := 1.U
			hep := 1.B
		}.elsewhen(io.statusC.dst === 1.U && hepDone){
			sentBDAddr	 := 0.B
			sentCode  	 := 0.B
			io.tisrA.errorM2 := 0.B	
			hep := 0.B
		}
	
		when(io.statusC.dst === 1.U && tbit){
			stateReg 	:= setaasa	
			sentBDAddr 	:= 0.B
			sentCode 	:= 0.B	   
		}
		//For Checking
		io.en_ack7E := en_ack_7E
		io.en_tb := en_tbit
		io.en_hp := en_hep

		io.ret_ack7E := ack_7E
		io.ret_tb := tbit
		io.ret_hp := hepDone
	 	io.ret_value1 := value1
 		io.ret_value2 := value2
		io.ret_value3 := value3

		

	}


	

} // Switch
} // elsewhen
} // daaEn
	      io.cdasa := countDASA
              io.caasa := countAASA
	      io.cdaa  := countEntDAA
	      io.unsortvec := unsort
              io.sortvecout := sortvec
              io.rinfo := readSlaveInfo
              io.st := slvReg(1,0)
              io.sa := slvReg(8,2)
              io.s := slvReg
	      io.sortingdone := readSortOrder	      
	      io.loadinfo := loadSlaveInfo
	      io.slaveinforeg := slvInfoReg	
	      io.i3cnum := totalI3CSlvs
	      io.bdraddr := sentBDAddr
	      io.codesent := sentCode
	      io.hepreg := hep

	      io.countsortout := countsort
    	      io.countvarout := countvar
	      io.dasaout := dasaSA
	      io.sentStat := sentStatAddr

	      io.sdrfout := sdrf
	      io.indexout := index
	      io.slvout := slvalueReg
	      io.stadd := slvalueReg(8,2)
	      io.slcon := slvalueReg(1,0)
	      io.stypeout := stype
	      io.updatesdrfout := updatesdrf
  	      io.stateRegout := stateReg
	      io.DAsuccessout := countDASuccess

	      io.sent7eout := sent7eRead
	      io.cdaaout := countDaa
	      io.countcheck := 0.U
	      io.pidout := pid
	      io.bcrout := bcr
	      io.dcrout := dcr
	      io.dynout := dynamicAddr
	      io.read64bitsout := rec64bits
	      io.selectDA := selectedDA
	      io.sentDynOut := sentDynAddr
	      io.countNACKout := countNACK
	      io.slaveindexout := slaveIndex
	      io.collisioncountout := collisionCount
	      io.readslvidx := readSlvIdx

} // Module

// MasterFSM.scala
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
// For checking
  val stateval = Output(UInt(3.W))
  val sdrfout = Output(Vec(11,UInt(32.W)))
  val daassign = Output(Bool())
  val en_ack7E = Output(Bool())
  val en_tb = Output(Bool())
  val en_hp = Output(Bool())
  val en_acknooff = Output(Bool())
  val ret_ack7E = Output(Bool())
  val ret_tb = Output(Bool())
  val ret_hp = Output(Bool())
  val ret_nohoff = Output(Bool())
  val ret_value1 = Output(UInt(5.W))
  val ret_value2 = Output(UInt(5.W))
  val ret_value3 = Output(UInt(5.W))
  val ret_value4 = Output(UInt(5.W))

  val bdraddr = Output(Bool())  
  val recdata = Output(Bool())
  val ldslvid = Output(Bool())
  val slvreg = Output(UInt(32.W))
  val slvid = Output(UInt(4.W))
  val sentda = Output(Bool())
  val enableread = Output(Bool())
  val countbyte = Output(UInt(3.W))
  val readout = Output(UInt(32.W))
  val writeout = Output(UInt(32.W))
  val enablewrite = Output(Bool())
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
val sdrf	 = RegInit(VecInit(Seq.fill(11){0.U(32.W)}))
val slaveReg	 = RegInit(0.U(32.W)) 
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

//For checking
io.en_ack7E := 0.B
io.en_tb := 0.B
io.en_hp := 0.B
io.en_acknooff := 0.B
io.ret_ack7E := 0.B
io.ret_tb := 0.B
io.ret_hp := 0.B
io.ret_nohoff := 0.B
io.ret_value1 := 0.U
io.ret_value2 := 0.U
io.ret_value3 := 0.U
io.ret_value4 := 0.U



// FSM
val start :: daa :: sdrread :: sdrwrite :: Nil = Enum(4)

val stateReg = RegInit(start)

//SDR_READ Transition
val condA_sdrRead = (io.configA.readWrite) && (!io.configA.i3c_mode)

//SDR_Write Transition
val condB_sdrWrite = (!io.configA.readWrite) && (!io.configA.i3c_mode) 

when(io.configA.bus_reset){
	daAssigned := 0.B
	stateReg := start
}.otherwise{


switch(stateReg) {
	is(start){
				tisrReg1.busBusy := 1.B
		 
		 when (io.configA.config_done) {
			when(!daAssigned && io.configA.load_done){		
				stateReg 	:= daa 
		//		tisrReg1.busBusy := 1.B
		// Comment above line for proper functionality.
			}.elsewhen (condA_sdrRead){ 

				stateReg 	:= sdrread 
		   	}.elsewhen (condB_sdrWrite) {

				stateReg 	:= sdrwrite 
			}
		}
	}

	is(daa){

		tisrReg1.busBusy := 1.B
		io.tisrA.tisrEn := 1.B			
		val daafsm = Module(new DaaFSM)

		daafsm.io.slvInfoA 	<> io.slvInfoA
		daafsm.io.configA 	<> io.configA
		daafsm.io.statusC 	<> io.statusC
	 	io.configC 		<> daafsm.io.configC
		daafsm.io.dataregC.dataregRx := io.dataregC.dataregRx
		io.dataregC.dataregTx 	     := daafsm.io.dataregC.dataregTx

//		daafsm.io.dataregC <> io.dataregC
//		io.dataregC <> daafsm.io.dataregC			
	 

		tisrReg1.da_assigned 	:= daafsm.io.tisrA.da_assigned
		val errorDaa 		= daafsm.io.tisrA.error
		val collisionDaa 	= daafsm.io.tisrA.collision
		tisrReg2.errorM2 	:= daafsm.io.tisrA.errorM2	
		sdrf		 	:= daafsm.io.sdrfout	
		daafsm.io.daaEn 	:= stateReg
		val daa_done 		= daafsm.io.daaDone

		when(daa_done){ 
			stateReg := start
			tisrReg1.busBusy := 0.B
			daAssigned := 1.B
		}.elsewhen(errorDaa || collisionDaa){
			stateReg := start
			tisrReg1.busBusy := 0.B 
			tisrReg1.collision := collisionDaa
			tisrReg1.error := errorDaa
		}

	}
	
	is(sdrread){
	
		io.configC.configEn := 1.B
		io.configC.ini 	:= 0.U
		io.configC.sdr 	:= 1.U
		io.tisrA.tisrEn := 1.B			
		
		val en_ack_7E       = stateReg === sdrread  && sentBDAddr && !sentDa && !hep
                val (value1,ack_7E) = MasterFSM.ackHandOff(en_ack_7E)

         	val en_ackNoOff       = stateReg === sdrread && sentDa && sentBDAddr && !enRead
                val (value2,ackRead) = MasterFSM.ackWithoutHandOff(en_ackNoOff)
	
	        val en_tbit       = stateReg === sdrread && sentDa && sentBDAddr && enRead && !recData
                val (value3,tbitRec) = MasterFSM.tBitRecDelay(en_tbit)

                val en_hep           = stateReg === sdrread && hep && sentBDAddr
                val (value4,hepDone) = MasterFSM.hdrExitPattern(en_hep)

		when(!loadSlaveId){
			tisrReg1.busBusy := 1.B
                        slaveID := io.configA.slaveId
                        slaveReg := sdrf(slaveID)
			loadSlaveId := 1.B			
				

		}

		when(!sentBDAddr && loadSlaveId){
                        when(slaveReg(7) === 0.U) {
                                tisrReg1.error := 1.B			
			}.otherwise{
		
	                        io.configC.s     := 1.U
	                        io.configC.rw    := 1.U
	                        io.configC.cccNccc := 0.U
 	                        io.dataregC.dataregTx(0) := "hFC".U
	                        sentBDAddr       := 1.B
			}
                }.elsewhen(io.statusC.an === 0.U && ack_7E){
                	io.configC.srP := 0.U
		        io.dataregC.dataregTx(0) := Cat(slaveReg(6,0),1.U)
                        sentDa := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack_7E){
                        tisrReg2.errorM2 := 1.B
                        io.configC.hep   := 1.U
                        hep := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr       := 0.B
                        sentDa         := 0.B
                        tisrReg2.errorM2 := 0.B
                        hep := 0.B
                }

		when(io.statusC.an === 0.U && ackRead){
			enRead := 1.B	

		}
		when(tbitRec && countByte < 4.U ){

			when(io.statusC.t === 1.U && io.configA.abort){
				io.configC.abtR := 1.U
		
			}.elsewhen(io.statusC.t === 1.U && !io.configA.abort){
				countByte := countByte + 1.U

			}.elsewhen(io.statusC.t === 0.U){
				when(io.statusC.dsr === 1.U && io.statusC.ab === 0.U){
					io.configC.srP 	:= 1.U
					readData := Cat(io.dataregC.dataregRx(0),io.dataregC.dataregRx(1),io.dataregC.dataregRx(2),io.dataregC.dataregRx(3))
					countByte := countByte + 1.U
					recData := 1.B
				}.otherwise{
				//Go to start here--> Received Incomplete Data
					io.configC.srP := 1.U
					tisrReg1.error := 1.B
					stateReg := start
					sentBDAddr := 0.B
					sentDa := 0.B
					loadSlaveId := 0.B
					enRead := 0.B			
					countByte := 0.U
					tisrReg1.busBusy := 0.B
				}
			}
		}.elsewhen(recData && countByte === 4.U){
			io.dataA.read_data := readData
			tisrReg1.readValid := 1.B
			tisrReg1.ack_slv := 1.B
			sentBDAddr := 0.B
			sentDa := 0.B
			countByte := 0.U
			loadSlaveId := 0.B
			enRead := 0.B
			recData := 0.B
			stateReg := start 
			tisrReg1.busBusy := 0.B
		}
	//For Checking
                io.en_ack7E := en_ack_7E
                io.en_tb := en_tbit
                io.en_hp := en_hep
		io.en_acknooff := en_ackNoOff

                io.ret_ack7E := ack_7E
                io.ret_tb := tbitRec
                io.ret_hp := hepDone
		io.ret_nohoff := ackRead

                io.ret_value1 := value1
                io.ret_value2 := value2
                io.ret_value3 := value3 
		io.ret_value4 := value4		

	} 




	is(sdrwrite){

		io.configC.configEn := 1.B
		io.configC.ini 	:= 0.U
		io.configC.sdr 	:= 1.U
		io.tisrA.tisrEn := 1.B			
	
		val en_ack       = stateReg === sdrwrite  && sentBDAddr  && !hep && !enWrite
                val (value1,ack) = MasterFSM.ackHandOff(en_ack)

	        val en_tbit       = stateReg === sdrwrite  && sentBDAddr && sentDa && enWrite
                val (value2,tbit) = MasterFSM.tBitDelay(en_tbit)

                val en_hep           = stateReg === sdrwrite && hep && sentBDAddr
                val (value3,hepDone) = MasterFSM.hdrExitPattern(en_hep)

		when(!loadSlaveId){
			tisrReg1.busBusy := 1.B
                        slaveID := io.configA.slaveId
                        slaveReg := sdrf(slaveID)
			loadSlaveId := 1.B			
			writeData := io.dataA.write_data
		}

		when(!sentBDAddr && loadSlaveId){
                        when(slaveReg(7) === 0.U) {
                                tisrReg1.error := 1.B			
			}.otherwise{
		
	                        io.configC.s     := 1.U
	                        io.configC.rw    := 1.U
	                        io.configC.cccNccc := 0.U
 	                        io.dataregC.dataregTx(0) := "hFC".U
	                        sentBDAddr       := 1.B
			}
                }.elsewhen(io.statusC.an === 0.U && ack && !sentDa){
                	io.configC.srP := 0.U
		        io.dataregC.dataregTx(0) := Cat(slaveReg(6,0),1.U)
                        sentDa := 1.B

                }.elsewhen(io.statusC.an === 1.U && ack && !sentDa){
                        tisrReg2.errorM2 := 1.B
                        io.configC.hep   := 1.U
                        hep := 1.B
                }.elsewhen(io.statusC.dst === 1.U && hepDone){
                        sentBDAddr       := 0.B
                        sentDa         := 0.B
                        tisrReg2.errorM2 := 0.B
                        hep := 0.B
                }.elsewhen(sentBDAddr && sentDa && !hep && ack){
			io.dataregC.dataregTx(0) := writeData(31,24)
			io.dataregC.dataregTx(1) := writeData(23,16)
			io.dataregC.dataregTx(2) := writeData(15,8)
			io.dataregC.dataregTx(3) := writeData(7,0)
			io.configC.srP := 1.U
			enWrite := 1.B
		
			
		}
		when(io.statusC.dst === 1.U && tbit && enWrite){
			countByte := countByte + 1.U
			when(countByte === 3.U){
				tisrReg1.ack_slv := 1.B
				countByte := 0.U
				sentBDAddr := 0.B
				sentDa := 0.B
				loadSlaveId := 0.B
				enWrite := 0.B
				stateReg := start
				tisrReg1.busBusy := 0.B
			}


		}

		//For Checking
                io.en_ack7E := en_ack
                io.en_tb := en_tbit
                io.en_hp := en_hep

                io.ret_ack7E := ack
                io.ret_tb := tbit
                io.ret_hp := hepDone

                io.ret_value1 := value1
                io.ret_value2 := value2
                io.ret_value3 := value3
              


	}
} // Switch

} // Otherwise

		io.stateval := stateReg
		io.sdrfout := sdrf
		io.daassign := daAssigned

		io.bdraddr := sentBDAddr
		io.ldslvid := loadSlaveId
		io.slvreg := slaveReg
		io.slvid := slaveID
		io.sentda := sentDa
		io.enableread := enRead
		io.countbyte := countByte
		io.recdata := recData
		io.readout := readData
		io.writeout := writeData
		io.enablewrite := enWrite
} // Module


//Block-C

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


