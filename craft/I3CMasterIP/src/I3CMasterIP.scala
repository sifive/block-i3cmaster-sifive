
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
		RegField(1,bus_reset,		RegFieldDesc("bus_reset","If set state is changed to DAA"))))

      0x40 -> RegFieldGroup("bus_status_reg", Some("Specifies the general status of I3C Master"),
	 Seq(   RegField.r(1,bus_busy, 		RegFieldDesc("bus_busy", "0 --> Can accept new req from host;1 --> busy in processing previous req" )), 
		RegField.r(1,slv_ack,		RegFieldDesc("slv_ack", "Previous request was served 0 --> unsuccessful/ 1 -->successful" )),
		RegField.r(1,read_valid,	RegFieldDesc("read_valid", "set by I3C Master when Read Data Register contains valid data requested by the Host" )),
		RegField.r(1,status_hj,		RegFieldDesc("status_hj", "Hotjoined 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_ibi,	RegFieldDesc("status_ibi", "In-band Interrupt req 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_mst_req,	RegFieldDesc("status_mst_req", "Mastership req 0-->not occured ; 1-->occured" )),
		RegField.r(1,status_grpaddr,	RegFieldDesc("status_grpaddr", "Group Addressing req 0-->not occured ; 1-->occured" )),
		RegField.w1c(1,collision_detected,  RegFieldDesc("collision_detected", "0 --> Collision not detected; 1 --> Collision detected" )),
		RegField.w1c(1,err_detected,	RegFieldDesc("err_detected", "0 -->No error detected; 1 --> Error detected" )),
		RegField(11),
		RegField.r(4,num_i3c_slv_without_stataddr,RegFieldDesc("num_i3c_slv_without_stataddr", "Number of I3C slaves without static address", reset=Some(0))),
		RegField.r(4,num_i3c_slv_stataddr,RegFieldDesc("num_i3c_slv_stataddr", "Number of I3C slaves with static address", reset=Some(0))),
		RegField.r(4,total_num_slv,		RegFieldDesc("total_num_slv","Total number of slaves", reset=Some(0)))))


)

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
