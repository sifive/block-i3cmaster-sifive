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


case object I3CMasterKey extends Field[Seq[I3CMasterParams]]

case class I3CMasterParams(
	address:          BigInt,
	beatBytes:	  Int = 4
  
							
)
/*
class I3CPin extends Bundle{
	val in = Bool(INPUT)
	val out = Bool(OUTPUT)
}


class I3CPort extends Bundle{
	val scl = new I3CPin
	val sda = new I3CPin
}
*/

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
		address = Seq(AddressSet(params.address, 0xfff))
		device = device,
		beatBytes = params.beatBytes)


lazy val module = new LazyModuleImp(this){



val r1 = RegInit(0.U(2.W))

val r2 = RegInit(0.U(2.W))


val field = Seq (
	0x0 -> RegFieldGroup("Register1",Some("First Register"),
	Seq(RegField(2,r1),
	RegField(30))),

	0x4 -> RegFieldGroup("Register2",Some("Second Register"),
	Seq(RegField(2,r2),
	RegField(30)))

)


controlNode.regmap(field : _*)
}
/*
lazy val module = new LazyModuleImp(this){


 val i3cIO		=IO(I3CPort)  //Check this

 val mfsm		= Module(new MasterFSM)

 val stataddr_slv       = RegInit(0.U(7.W))
 val slv_typ            = RegInit(0.U(2.W))
 

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
 val i3c_mode		= RegInit(false.B))
 val slaveID		= RegInit(0.U(4.W))
 val r/~w		= RegInit(false.B))
 val config_done	= RegInit(false.B)

 val num_i2c_slv	= RegInit(0.U(4.W))
 val num_i3c_slv_stataddr= RegInit(0.U(4.W))
 val num_i3c_slv_without_stataddr = RegInit(0.U(4.W))
 val err_detected	= RegInit(false.B)
 val collision_detected = RegInit(false.B)
 val status_grpaddr	= RegInit(false.B)
 val status_mst_req	= RegInit(false.B)
 val status_ibi		= RegInit(false.B)
 val status_hj		= RegInit(false.B)
 val slv_ack		= RegInit(false.B)
 val bus_type		= RegInit(false.B)
 val bus_busy		= RegInit(false.B)


 val num_aa		= RegInit(0.U(4.W))
 val num_slv		= RegInit(0.U(4.W))
 val m3			= RegInit(false.B)
 val m2			= RegInit(false.B)
 val m0			= RegInit(false.B)
 val s5			= RegInit(false.B)
 val s4			= RegInit(false.B)
 val s3			= RegInit(false.B)
 val s2			= RegInit(false.B)
 val s1			= RegInit(false.B)
 val s0			= RegInit(false.B)	


 val status_hdr_bulk_mode = RegInit(false.B)
 val status_hdr-ddr	= RegInit(false.B)
 val status_hdr-tsl     = RegInit(false.B)
 val status_hdr-tsp     = RegInit(false.B)
 val status_hdr		= RegInit(0.U(2.W))
 val hdr_transfer_invalid= RegInit(false.B)

 val next_mstID		= RegInit(0.U(4.W))
 val present_mstID	= RegInit(0.U(4.W))
 val mstship_return_previous_mst= RegInit(false.B)
 val acc_req		= RegInit(false.B)
 val req_src		= RegInit(0.U(1.W))

 val pending_read_notify= RegInit(false.B)
 val status_interrupt   = RegInit(false.B)
 val accpt_addnal_databyte= RegInit(false.B)
 val bcr[2]		= RegInit(false.B)
 val ibi_ack		= RegInit(false.B)

 val status_grpaddr_10  = RegInit(0.U(1.W))
 val status_grpaddr_9	= RegInit(0.U(1.W))
 val status_grpaddr_8 	= RegInit(0.U(1.W))
 val status_grpaddr_7 	= RegInit(0.U(1.W))
 val status_grpaddr_6 	= RegInit(0.U(1.W))
 val status_grpaddr_5 	= RegInit(0.U(1.W))
 val status_grpaddr_4	= RegInit(0.U(1.W))
 val status_grpaddr_3	= RegInit(0.U(1.W))
 val status_grpaddr_2	= RegInit(0.U(1.W))
 val status_grpaddr_1	= RegInit(0.U(1.W))
 val status_grpaddr_0 	= RegInit(0.U(1.W))

 val r_data		= RegInit(0.U(32.W))
 val w_data		= RegInit(0.U(32.W))
 
 val i3cSTcount 	= RegInit(0.U(4.W))
 val i3cWSTcount	= RegInit(0.U(4.W))

 def i3c_STcounter()
{
}

def i3c_ATcounter()
{
}
 //sending configuration to part-B

 mfsm.io.configA.bus_reset	:= bus_reset
 mfsm.io.configA.chip_reset	:= chip_reset
 mfsm.io.configA.slaveId	:= slaveID
 mfsm.io.configA.readWrite	:= r/~w
 mfsm.io.configA.config_done	:= config_done
 mfsm.io.configA.numI3C_staticAddr := i3cSTcount
 mfsm.io.configA.numI3C_without_staticAddr := i3cWSTcount
 mfsm.io.configA.totalI3C_slaves :=


  val fields = Seq(

      0x0 -> RegFieldGroup("Slave_Info_Register0", Some("Slave_Info_Register0"),
	 Seq(   RegField(2,slv_typ0,		RegFieldDesc("slv_typ0", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type0 = 0 || slv_typ = 1)RegField(7,stataddr_slv0,	RegFieldDesc("stataddr_slv0", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x4 -> RegFieldGroup("Slave_Info_Register1", Some("Slave Info Register1"),
	 Seq(   RegField(2,slv_typ1,		RegFieldDesc("slv_typ1", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type1 = 0 || slv_typ1 = 1)RegField(7,stataddr_slv1,	RegFieldDesc("stataddr_slv1", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x8 -> RegFieldGroup("Slave_Info_Register2", Some("Slave Info Register2"),
	 Seq(   RegField(2,slv_typ2,		RegFieldDesc("slv_typ2", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type2 = 0 || slv_typ2 = 1)RegField(7,stataddr_slv2,	RegFieldDesc("stataddr_slv2", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0xC -> RegFieldGroup("Slave_Info_Register3", Some("Slave Info Register3"),
	 Seq(   RegField(2,slv_typ3,		RegFieldDesc("slv_typ3", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type3 = 0 || slv_typ3 = 1)RegField(7,stataddr_slv3,	RegFieldDesc("stataddr_slv3", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x10 -> RegFieldGroup("Slave_Info_Register4", Some("Slave Info Register4"),
	 Seq(   RegField(2,slv_typ4,		RegFieldDesc("slv_typ4", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type4 = 0 || slv_typ4 = 1)RegField(7,stataddr_slv4,	RegFieldDesc("stataddr_slv4", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x14 -> RegFieldGroup("Slave_Info_Register5", Some("Slave Info Register5"),
	 Seq(   RegField(2,slv_typ5,		RegFieldDesc("slv_typ5", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type5 = 0 || slv_typ5 = 1)RegField(7,stataddr_slv5,	RegFieldDesc("stataddr_slv5", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x18 -> RegFieldGroup("Slave_Info_Register6", Some("Slave Info Register6"),
	 Seq(   RegField(2,slv_typ6,		RegFieldDesc("slv_typ6", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type6 = 0 || slv_typ6 = 1)RegField(7,stataddr_slv6,	RegFieldDesc("stataddr_slv6", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x1C -> RegFieldGroup("Slave_Info_Register7", Some("Slave Info Register7"),
	 Seq(   RegField(2,slv_typ7,		RegFieldDesc("slv_typ7", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type7 = 0 || slv_typ7 = 1)RegField(7,stataddr_slv7,	RegFieldDesc("stataddr_slv7", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x20 -> RegFieldGroup("Slave_Info_Register8", Some("Slave Info Register8"),
	 Seq(   RegField(2,slv_typ8,		RegFieldDesc("slv_typ8", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type8 = 0 || slv_typ8 = 1)RegField(7,stataddr_slv8,	RegFieldDesc("stataddr_slv8", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x24 -> RegFieldGroup("Slave_Info_Register9", Some("Slave Info Register9"),
	 Seq(   RegField(2,slv_typ9,		RegFieldDesc("slv_typ9", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type9 = 0 || slv_typ9 = 1)RegField(7,stataddr_slv9,	RegFieldDesc("stataddr_slv9", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),

      0x28 -> RegFieldGroup("Slave_Info_Register10", Some("Slave Info Register10"),
	 Seq(   RegField(2,slv_typ10,		RegFieldDesc("slv_typ10", "2'b00:No slave, 2'b01:I2C slave, 2'b10:I3C slave without static address, 2'b11:I3C slave with static address", reset= 0)), 
		RegField(6,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		(if (slv_type10 = 0 || slv_typ10 = 1)RegField(7,stataddr_slv10,	RegFieldDesc("stataddr_slv10", "7 bit static address", reset=0) else 
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
		RegField(17,rsvd,		RegFieldDesc("Reserved","Reserved")))),


      0x3C -> RegFieldGroup("I3C_config_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(1,config_done, 	RegFieldDesc("config_done", "This is set when host has completed configuration the I3C Master",reset = false.B)), 
		RegField(1,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		RegField(1,r/~w,		RegFieldDesc("r/~w", "0 --> Write/1 --> Read", reset= false.B)),
		RegField(4,slaveID,		RegFieldDesc("slaveID", "SlaveID of the currect slave", reset= 0)),
		RegField(1,i3c_mode,		RegFieldDesc("i3c_mode", "0 --> SDR/ 1 --> HDR", reset=false.B)),
(if (i3c_mode)	RegField(2,set_hdr_mode, 	RegFieldDesc("set_hdr_mode","2'b00:HDR-TSP,2'b01:HDR-TSL,2'b10:HDR-DDR,2'b11:HDRBulk Transport", reset=0)) 
		else RegField(2)),
		RegField(1,mst_req,		RegFieldDesc("mst_req", "To be set by the host to enable mastership request", reset= false.B)),
(if (mst_req)	RegField(4,mst_req_slvID,	RegFieldDesc("mst_req_slvID","SlaveID of the slave that is offered mastership by the host", reset= 0xF))
		else RegField(4)),
		RegField(2,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		RegField(1,grpaddr_req,	RegFieldDesc(""grpaddr_req, "To be set by the host to enable group addressing ", reset =false.B)),
(if (grpaddr_req)RegField(11,grpaddr_slv_en,	RegFieldDesc("grpaddr_slv_en", "Enable for corresponding slave to participate in Group Addressing", reset= 0)) 
		else RegField(11)),
		RegField(1,chip_reset,	RegFieldDesc("chip_reset", "If set entire system is reset", reset= false.B)),
		RegField(1,bus_reset,		RegFieldDesc("bus_reset","If set state is changed to DAA", reset= false.B)))),

      0x40 -> RegFieldGroup("bus_status_reg", Some("Specifies the general status of I3C Master"),
	 Seq(   RegField.r(1,bus_busy, 		RegFieldDesc("bus_busy", "0 --> Can accept new req from host;1 --> busy in processing previous req", reset=false.B)), 
		RegField.r(1,bus_type,		RegFieldDesc("bus_type", "0 --> I3C ; 1 --> I2C", reset=false.B)),
		RegField.r(1,slv_ack,		RegFieldDesc("slv_ack", "Previous request was served 0 --> unsuccessful/ 1 -->successful", reset=false.B)),
	//	RegField.r(1,status_hdr,		RegFieldDesc("status_hdr", "Hotjoined 0-->not occured ; 1-->occured", reset=false.B)),
	//	RegField.r(1,status_ibi,		RegFieldDesc("status_ibi", "In-band Interrupt req 0-->not occured ; 1-->occured", reset=false.B)),
	//	RegField.r(1,status_mst_req,	RegFieldDesc("status_mst_req", "Mastership req 0-->not occured ; 1-->occured", reset=false.B)),
	//	RegField.r(1,status_grpaddr,	RegFieldDesc("status_grpaddr", "Group Addressing req 0-->not occured ; 1-->occured", reset=false.B)),
		RegField.w1c(1,collision_detected,  RegFieldDesc("collision_detected", "0 --> Collision not detected; 1 --> Collision detected", reset=false.B)),
		RegField.w1c(1,err_detected,	RegFieldDesc("err_detected", "0 -->No error detected; 1 --> Error detected", reset=false.B)),
		RegField(11,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		RegField.r(4,num_i3c_slv_without_stataddr,RegFieldDesc("num_i3c_slv_without_stataddr", "Number of I3C slaves without static address", reset=0)),
		RegField.r(4,num_i3c_slv_stataddr,RegFieldDesc("num_i3c_slv_stataddr", "Number of I3C slaves with static address", reset=0)),
		RegField.r(4,num_i2c_slv,		RegFieldDesc("num_i2c_slv","Number of I2C slaves", reset=0)))),

      0x50 -> RegFieldGroup("Error_status_reg", Some("Specifies the fields specific to Error Condition"),
	 Seq(	RegField(6,rsvd,		RegFieldDesc("Reserved,"Reserved")), 
		RegField(1,m0,			RegFieldDesc("m0", "set when m0  error  occurs", reset=false.B)),
		RegField(1,m2,			RegFieldDesc("m2", "set when m2  error  occurs", reset=false.B)),
		RegField(1,m3,			RegFieldDesc("m3", "set when m3  error  occurs", reset=false.B)),
		RegField(5,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		RegField(4,num_slvs,		RegFieldDesc("num_slvs", "Total number of slaves", reset=0)),
		RegField(4,num_aa,		RegFieldDesc("num_aa", "Total number of addresses issued", reset=0)),
		RegField(7,rsvd,		RegFieldDesc("Reserved","Reserved")))),
/*
      0x54 -> RegFieldGroup("hdr_status_reg", Some("Specifies the fields specific to HDR Mode"),
	 Seq(   RegField(1,hdr_transfer_invalid,RegFieldDesc("hdr_transfer_invalid", "set when current mode is not support by the slave", reset=false.B)),
		RegField(2,status_hdr,		RegFieldDesc("status_hdr", "specifies the current type of HDR mode 2'b00:HDR-TSP,2'b01:HDR-TSL,2'b10:HDR-DDR,2'b11:HDRBulk Transport", reset=0)),
		RegField(1,status_hdr-tsp,	RegFieldDesc("status_hdr-tsp", "Set the slave supports HDR_TSP", reset=false.B)),
		RegField(1,status_hdr-tsl,	RegFieldDesc("status_hdr-tsl", "Set the slave supports HDR_TSL", reset=false.B)),
		RegField(1,status_hdr-ddr,	RegFieldDesc("status_hdr-ddr", "Set the slave supports HDR_DDR", reset=false.B)),
		RegField(1,status_hdr_bulk_mode,RegFieldDesc("status_hdr_bulk_mode","Set the slave supports HDR Bulk Transport", reset=false.B))
		RegField(25,rsvd, 		RegFieldDesc("Reserved", "Reserved")))),

      0x58 -> RegFieldGroup("master_status_reg", Some("Specifies field specific to Mastership Request"),
	 Seq(   RegField.r(1,req_src,		RegFieldDesc("req_src", "", reset=)),
		RegField.r(1,acc_req,		RegFieldDesc("acc_req", "", reset=)),
		RegField.r(1,mstship_return_previous_mst,RegFieldDesc("mstship_return_previous_mst", "", reset=)),
		RegField.r(5,rsvd,		RegFieldDesc("Reserved",  "Reserved")),
		RegField.r(4,present_mstID,	RegFieldDesc("present_mstID", "", reset=)),
		RegField.r(4,next_mstID,	RegFieldDesc("next_mstID", "", reset=)),
		RegField.r(16,rsvd, 		RegFieldDesc("Reserved", "Reserved")))),

      0x5C -> RegFieldGroup("ibi_status_reg", Some("Specifies fields specific to IBI"),
	 Seq(   RegField(1,ibi_ack,		RegFieldDesc("ibi_ack", "", reset=)),
		RegField(1,bcr[2],		RegFieldDesc("bcr[2]", "", reset=)),
		RegField(1,accpt_addnal_databyte,RegFieldDesc("accpt_addnal_databyte", "", reset=)),
		RegField(1,status_interrupt,	RegFieldDesc("status_interrupt", "", reset=)),
		RegField(1,pending_read_notify,	RegFieldDesc("pending_read_notify", "", reset=)),
		RegField(27,rsvd, 		RegFieldDesc("Reserved", "Reserved")))),

      0x5E -> RegFieldGroup("Group_Addressing_status_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(1,status_grpaddr_0,	RegFieldDesc("status_grpaddr_0", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_1,	RegFieldDesc("status_grpaddr_1", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_2,	RegFieldDesc("status_grpaddr_2", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_3,	RegFieldDesc("status_grpaddr_3", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_4,	RegFieldDesc("status_grpaddr_4", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_5,	RegFieldDesc("status_grpaddr_5", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_6,	RegFieldDesc("status_grpaddr_6", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_7,	RegFieldDesc("status_grpaddr_7", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_8,	RegFieldDesc("status_grpaddr_8", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_9,	RegFieldDesc("status_grpaddr_9", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(1,status_grpaddr_10,	RegFieldDesc("status_grpaddr_10", "0-->not included in group addressing/1-->included in group addressing", reset=0)),
		RegField(20,rsvd, 		RegFieldDesc("Reserved", "Reserved")))),
*/
      0x70 -> RegFieldGroup("Read_data_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(32,r_data, 		RegFieldDesc("r_data", "It is used to store the data that is read from the slave", reset=0)))),


      0x74 -> RegFieldGroup("Write_data_reg", Some("I3C Configuration Register"),
	 Seq(   RegField(32,w_data,		 RegFieldDesc("w_data", "It is used to store the data that is to be written to the slave", reset=0)))))


 controlNode.remap(fields: _*)
}
*/
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
