

package sifive.blocks.i3cmaster

import chisel3._
import chisel3.util._

import freechips.rocketchip.config._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system._
import freechips.rocketchip.devices.debug.Debug
import freechips.rocketchip.devices.tilelink.{TLError, DevNullParams}
import freechips.rocketchip.diplomaticobjectmodel.ConstructOM
import freechips.rocketchip.diplomaticobjectmodel.logicaltree._

import sifive.skeleton._

class I3CMasterConfig0 extends Config((site,here,up) => {
 case I3CMasterKey => Seq(I3CMasterParams(
 
	address		= 0x10010000
))
}) 



class TestSocDUT(harness: LazyScope)(implicit p: Parameters) extends SkeletonDUT(harness) with Attachable
{
  val I3CMasterParams = p(I3CMasterKey)(0)
  val i3cmaster = LazyModule(new I3CMaster(I3CMasterParams)) 
  pbus.coupleTo("i3cmaster"){ i3cmaster.controlNode := TLFragmenter(I3CMasterParams.beatBytes,p(CacheBlockBytes)) := TLWidthWidget(pbus) := _ }
  LogicalModuleTree.add(attachParams.parentNode, i3cmaster.ltnode)
}

class TestSocHarness()(implicit p: Parameters) extends LazyModule with LazyScope
{
  val dut = LazyModule(new TestSocDUT(this))
  lazy val module = new LazyModuleImp(this) {
    ConstructOM.constructOM()
    Debug.tieoffDebug(dut.module.debug)
  }
}
