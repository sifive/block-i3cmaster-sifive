package sifive.blocks.iiicmaster

import chisel3._

class Sorting extends Module {
val io = IO(new Bundle{
     val unsort	 = Input(Vec(11,UInt(7.W)))
     val sort	 = Output(Vec(11,UInt(7.W)))
     val load	 = Input(Bool())
     val sort_done = Output(Bool())
})

 
val initvalues	 = Seq.fill(11){ 127.U(7.W)}
val unsortReg	 = Reg(Vec(11,UInt()))
val sortReg	 = RegInit(VecInit(initvalues))
io.sort		 := unsortReg
io.sort_done	 := 0.B

val load_done	 = RegInit(0.U(1.W))
val count_iter	 = RegInit(0.U(4.W))
val count_pass	 = RegInit(0.U(4.W))

when(io.load && load_done === 0.U )
{

   unsortReg(10) := io.unsort(10)
   unsortReg(9)	 := io.unsort(9)
   unsortReg(8)	 := io.unsort(8)
   unsortReg(7)	 := io.unsort(7)
   unsortReg(6)	 := io.unsort(6)
   unsortReg(5)	 := io.unsort(5)
   unsortReg(4)	 := io.unsort(4)
   unsortReg(3)	 := io.unsort(3)
   unsortReg(2)	 := io.unsort(2)
   unsortReg(1)	 := io.unsort(1)
   unsortReg(0)	 := io.unsort(0)
   load_done	 := 1.U


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
	sortReg(9)  := sortReg(8)
	sortReg(8)  := sortReg(7)
	sortReg(7)  := sortReg(6)
	sortReg(6)  := sortReg(5)
	sortReg(5)  := sortReg(4)
	sortReg(4)  := sortReg(3)
	sortReg(3)  := sortReg(2)
	sortReg(2)  := sortReg(1)
	sortReg(1)  := sortReg(0)
	sortReg(0)  := unsortReg(9)

	count_iter  := count_iter + 1.U

	}.elsewhen(unsortReg(10) <= unsortReg(9))
	{
	unsortReg(10) := unsortReg(9)
	unsortReg(9)  := unsortReg(8)
        unsortReg(8)  := unsortReg(7)
        unsortReg(7)  := unsortReg(6)
        unsortReg(6)  := unsortReg(5)
        unsortReg(5)  := unsortReg(4)
        unsortReg(4)  := unsortReg(3)
        unsortReg(3)  := unsortReg(2)
        unsortReg(2)  := unsortReg(1)
        unsortReg(1)  := unsortReg(0)
        unsortReg(0)  := "hFF".U

	sortReg(10) := sortReg(9)
	sortReg(9)  := sortReg(8)
	sortReg(8)  := sortReg(7)
	sortReg(7)  := sortReg(6)
	sortReg(6)  := sortReg(5)
	sortReg(5)  := sortReg(4)
	sortReg(4)  := sortReg(3)
	sortReg(3)  := sortReg(2)
	sortReg(2)  := sortReg(1)
	sortReg(1)  := sortReg(0)
	sortReg(0)  := unsortReg(10)

	count_iter  := count_iter + 1.U
	}

    }.elsewhen(count_iter === 11.U)
	{
		unsortReg  := sortReg
		count_iter := 0.U
		count_pass := count_pass + 1.U

	}

}.elsewhen(count_pass === 10.U)
	{
		io.sort_done	:= 1.B
		io.sort 	:= sortReg
	}









}



