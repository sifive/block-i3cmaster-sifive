`timescale 1ns / 1ps

/*
 I2C master
*/
module I3CMasterOutputControl (
    input  wire        clk,
    input  wire        rst,
    inout  wire 	   sda_pin,
    inout  wire 	   scl_pin,
  
/*
* Configuration
*/
    	input  wire [15:0] prescale,
    	input  wire        stop_on_idle,
    	input  wire	   start,
    	input  wire	   repeated_start,
    	input  wire	   stop,
    	input  wire	   acknack_H,
    	input  wire	   acknack_noH,
    	input  wire	   datatx,
    	input  wire	   datarx,
  	
  
  	input  wire [8:0]  data_in,
    	input  wire        data_in_last,

  	output wire [8:0]  data_out,
    	output wire        data_out_last,
	output wire        acknack_rcvd,
/*
 *I3C interface
*/
    	input  wire        scl_i,
    	output wire        scl_o,
   	input wire        scl_en,
    	input  wire        sda_i,
    	output wire        sda_o,
    	input wire        sda_en,

/*
* Status
*/
    	output wire        busy,
    	output wire        bus_control,
    	output wire        bus_active
    
);



localparam [3:0]
    STATE_INITIAL 		= 4'd0,
    STATE_START 		= 4'd1,
    STATE_REPEATED_START	= 4'd2,
    STATE_Tx_1 			= 4'd3,
    STATE_Tx_2 			= 4'd4,
    STATE_Rx 			= 4'd5,
    STATE_ACKNACK_H 		= 4'd6,
    STATE_ACKNACK_noH 		= 4'd7,
    STATE_STOP 			= 4'd8;
    

  reg [3:0] state_reg = STATE_INITIAL, state_next;
 
  localparam [4:0]
    PHY_STATE_INITIAL 		= 5'd0,
    PHY_STATE_ACTIVE 		= 5'd1,
    PHY_STATE_REPEATED_START_1 	= 5'd2,
    PHY_STATE_REPEATED_START_2 	= 5'd3,
    PHY_STATE_START_1 		= 5'd4,
    PHY_STATE_START_2 		= 5'd5,
    PHY_STATE_WRITE_BIT_1 	= 5'd6,
    PHY_STATE_WRITE_BIT_2 	= 5'd7,
    PHY_STATE_WRITE_BIT_3 	= 5'd8,
    PHY_STATE_READ_BIT_1 	= 5'd9,
    PHY_STATE_READ_BIT_2 	= 5'd10,
    PHY_STATE_READ_BIT_3 	= 5'd11,
    PHY_STATE_READ_BIT_4 	= 5'd12,
    PHY_STATE_ACKNACK_H_1 	= 5'd13,
    PHY_STATE_ACKNACK_H_2	= 5'd14,
    PHY_STATE_ACKNACK_H_3	= 5'd15,
    PHY_STATE_STOP_1 		= 5'd16,
    PHY_STATE_STOP_2 		= 5'd17,
    PHY_STATE_STOP_3 		= 5'd18;

  reg [4:0] phy_state_reg = PHY_STATE_INITIAL, phy_state_next;

  reg phy_start_bit;
  reg phy_repeated_start_bit;
  reg phy_stop_bit;
  reg phy_write_bit;
  reg phy_read_bit;
  reg phy_release_bus;
  reg phy_acknackH_bit;

  reg phy_tx_data;

  reg phy_rx_data_reg 	= 1'b0, phy_rx_data_next;
  reg [8:0] data_reg  	= 9'd0, data_next;
  reg last_reg 		= 1'b0, last_next;

  reg [16:0] delay_reg 	 = 16'd0, delay_next;
  reg delay_scl_reg 	 = 1'b0, delay_scl_next;
  reg delay_sda_reg 	 = 1'b0, delay_sda_next;

  reg [3:0] bit_count_reg = 4'd0, bit_count_next;
  reg [8:0] data_out_reg  = 9'd0, data_out_next;
  reg data_out_last_reg   = 1'b0, data_out_last_next;

  reg scl_i_reg = 1'b1;
  reg sda_i_reg = 1'b1;

  reg scl_o_reg = 1'b1, scl_o_next;
  reg sda_o_reg = 1'b1, sda_o_next;
  
  reg last_scl_i_reg = 1'b1;
  reg last_sda_i_reg = 1'b1;

  reg busy_reg 		= 1'b0;
  reg bus_active_reg 	= 1'b0;
  reg bus_control_reg 	= 1'b0, bus_control_next;
  reg acknack_rcvd_reg 	= 1'b0, acknack_rcvd_next;

  assign data_out = data_out_reg;

  assign data_out_last = data_out_last_reg;

  assign scl_o = scl_o_reg;
  assign sda_o = sda_o_reg;

  assign busy 		= busy_reg;
  assign bus_active 	= bus_active_reg;
  assign bus_control 	= bus_control_reg;
  assign acknack_rcvd 	= acknack_rcvd_reg;

  wire scl_posedge = scl_i_reg & ~last_scl_i_reg;
  wire scl_negedge = ~scl_i_reg & last_scl_i_reg;
  wire sda_posedge = sda_i_reg & ~last_sda_i_reg;
  wire sda_negedge = ~sda_i_reg & last_sda_i_reg;

  wire start_bit = sda_negedge & scl_i_reg;
  wire stop_bit = sda_posedge & scl_i_reg;

always @* begin
    state_next = STATE_INITIAL;

    phy_start_bit 		= 1'b0;
    phy_repeated_start_bit	= 1'b0;
    phy_stop_bit 		= 1'b0;
    phy_write_bit 		= 1'b0;
    phy_read_bit 		= 1'b0;
    phy_tx_data 		= 1'b0;
    phy_release_bus 		= 1'b0;
    phy_acknackH_bit 		= 1'b0;


    data_next = data_reg;
    last_next = last_reg;

    bit_count_next = bit_count_reg;


    data_out_next 	= data_out_reg;
    data_out_last_next 	= data_out_last_reg;

    acknack_rcvd_next = 1'b0;
  			
  	// generate delays
  	if (phy_state_reg != PHY_STATE_INITIAL && phy_state_reg != PHY_STATE_ACTIVE) begin
		// wait for phy operation
  		state_next = state_reg;
 	end else begin
		// process states
  		case(state_reg)
			STATE_INITIAL: begin
  				 if(start == 1'b1) begin
  			         	state_next = STATE_START;
				 end
				 else if(repeated_start == 1'b1)begin
  					 state_next = STATE_REPEATED_START;
				 end                                                                                                                                                    		else if(stop == 1'b1)begin
 					state_next = STATE_STOP;		                                                                                                                        end		                                                                                                                                                        else if(acknack_H == 1'b1)begin
  			                state_next = STATE_ACKNACK_H;
  			        end
				else if(acknack_noH == 1'b1)begin
					state_next = STATE_ACKNACK_noH;
				end
				else if(datatx == 1'b1)begin
					state_next = STATE_Tx_1;
				end
				
				else if(datarx == 1'b1)begin
					state_next = STATE_Rx;
				end
				
				else begin
					state_next = STATE_INITIAL;
				end
			end
			STATE_START: begin
				// send start bit
				  phy_start_bit = 1'b1;
				  bit_count_next = 4'd9;
				  state_next = STATE_INITIAL;
			end
			STATE_REPEATED_START: begin
				//send repeated start bit
				phy_repeated_start_bit = 1'b1;
				state_next = STATE_INITIAL;
			end
			STATE_Tx_1: begin
				//data_in_ready_next=1'b1;
					if(datatx == 1'b1)begin
						//got data,start write
						data_next = data_in;
						last_next = data_in_last;
						bit_count_next = 4'd9;
						//data_in_ready_next = 1'b0;
						state_next = STATE_Tx_2;
					end
					else begin
					//wait for data
					state_next = STATE_Tx_1;
					end
			end
			STATE_Tx_2:begin
				//send data
				bit_count_next = bit_count_reg - 1;
				if(bit_count_reg > 0) begin
					//write data bit
					phy_write_bit = 1'b1;
					phy_tx_data = data_reg[bit_count_reg-1];
					state_next = STATE_Tx_2;
				end		
				else begin
					state_next = STATE_INITIAL;
				end
			end
			STATE_Rx: begin
				//read data
				bit_count_next = bit_count_reg - 1;
				data_next = {data_reg[7:0], phy_rx_data_reg};
				if(bit_count_reg > 0) begin
					//read next bit
					phy_read_bit = 1'b1;
					state_next = STATE_Rx;
				end
				else begin
					state_next = STATE_INITIAL;
				end
			end
			STATE_ACKNACK_noH: begin
				phy_read_bit = 1'b1;
				acknack_rcvd_next = phy_rx_data_reg;
			end
			STATE_ACKNACK_H: begin
				phy_acknackH_bit = 1'b1;
				state_next = STATE_INITIAL;
			end
			STATE_STOP: begin
				//send stop bit
				phy_stop_bit = 1'b1;
				state_next = STATE_INITIAL;
			end
		endcase
	end
end

always @(*) begin
    phy_state_next = PHY_STATE_INITIAL;

    phy_rx_data_next = phy_rx_data_reg;
    

    delay_next = delay_reg;
    delay_scl_next = delay_scl_reg;
    delay_sda_next = delay_sda_reg;

    scl_o_next = scl_o_reg;
    sda_o_next = sda_o_reg;

    bus_control_next = bus_control_reg;
	if (phy_release_bus) begin
 
        sda_o_next = 1'b1;
        scl_o_next = 1'b1;
        delay_scl_next = 1'b0;
        delay_sda_next = 1'b0;
        delay_next = 1'b0;
        phy_state_next = PHY_STATE_INITIAL;
    end else if (delay_scl_reg) begin
        /*
 * 	// wait for SCL to match command
 * 		*/
        delay_scl_next = scl_o_reg & ~scl_i_reg;
        phy_state_next = phy_state_reg;
    end else if (delay_sda_reg) begin
        /*
 * 	// wait for SDA to match command
 * 	        */
	delay_sda_next = sda_o_reg & ~sda_i_reg;
        phy_state_next = phy_state_reg;
    end else if (delay_reg > 0) begin
        /*
 * 	// time delay
 * 	        */
	delay_next = delay_reg - 1;
        phy_state_next = phy_state_reg;
    end else begin
        case (phy_state_reg)
            PHY_STATE_INITIAL: begin
                sda_o_next = 1'b1;
                scl_o_next = 1'b1;
		phy_state_next = PHY_STATE_ACTIVE;
            end
            PHY_STATE_ACTIVE: begin
               /*
 * 	       // bus active
 * 	       		*/
                if (phy_start_bit == 1'b1) begin
                    sda_o_next = 1'b0;
                    delay_next = prescale;
                    phy_state_next = PHY_STATE_START_1;
                end 
		else if(phy_repeated_start_bit == 1'b1) begin
			sda_o_next = 1'b0;
			delay_next = prescale;
			phy_state_next = PHY_STATE_REPEATED_START_1;
		end else if (phy_write_bit == 1'b1) begin
                    sda_o_next = phy_tx_data;
                    delay_next = prescale;
                    phy_state_next = PHY_STATE_WRITE_BIT_1;
                end else if (phy_read_bit == 1'b1) begin
                    sda_o_next = 1'b1;
                    delay_next = prescale;
                    phy_state_next = PHY_STATE_READ_BIT_1;
                end else if (phy_stop_bit == 1'b1) begin
                    sda_o_next = 1'b0;
                    delay_next = prescale;
                    phy_state_next = PHY_STATE_STOP_1;
                end else if (phy_acknackH_bit == 1'b1) begin
		    //scl_o_next = 1'b0;
                    sda_o_next = 1'b0;
                    delay_next = prescale;
                    phy_state_next = PHY_STATE_ACKNACK_H_1;
                end else begin
                    phy_state_next = PHY_STATE_ACTIVE;
                end
            end

            PHY_STATE_REPEATED_START_1: begin
		   // generate repeated start bit
                scl_o_next = 1'b1;
                delay_scl_next = 1'b1;
                delay_next = prescale;
                phy_state_next = PHY_STATE_REPEATED_START_2;
            end
            PHY_STATE_REPEATED_START_2: begin
		// generate repeated start bit
                sda_o_next = 1'b0;
                delay_next = prescale;
                phy_state_next = PHY_STATE_ACTIVE;
            end
            PHY_STATE_START_1: begin
		// generate start bit
                scl_o_next = 1'b0;
                delay_next = prescale;
                phy_state_next = PHY_STATE_START_2;
            end
            PHY_STATE_START_2: begin
		// generate start bit
                bus_control_next = 1'b1;
                phy_state_next = PHY_STATE_ACTIVE;
            end
            PHY_STATE_WRITE_BIT_1: begin
		// write bit
                scl_o_next = 1'b1;
                delay_scl_next = 1'b1;
                delay_next = prescale << 1;
                phy_state_next = PHY_STATE_WRITE_BIT_2;
            end
            PHY_STATE_WRITE_BIT_2: begin
		// write bit
                scl_o_next = 1'b0;
                delay_next = prescale;
                phy_state_next = PHY_STATE_WRITE_BIT_3;
            end
            PHY_STATE_WRITE_BIT_3: begin
               // write bit
                phy_state_next = PHY_STATE_ACTIVE;
            end
            PHY_STATE_READ_BIT_1: begin
		// read bit
                scl_o_next = 1'b1;
                delay_scl_next = 1'b1;
                delay_next = prescale;
                phy_state_next = PHY_STATE_READ_BIT_2;
            end
            PHY_STATE_READ_BIT_2: begin
		// read bit
                phy_rx_data_next = sda_i_reg;
                delay_next = prescale;
                phy_state_next = PHY_STATE_READ_BIT_3;
            end
            PHY_STATE_READ_BIT_3: begin
		// read bit
                scl_o_next = 1'b0;
                delay_next = prescale;
                phy_state_next = PHY_STATE_READ_BIT_4;
            end
            PHY_STATE_READ_BIT_4: begin
		// read bit
                phy_state_next = PHY_STATE_ACTIVE;
            end
            PHY_STATE_ACKNACK_H_1:begin
                sda_o_next = 1'bz;
                scl_o_next = 1'b0;
                phy_state_next = PHY_STATE_ACKNACK_H_2;
            end
	    PHY_STATE_ACKNACK_H_2: begin
		sda_o_next = phy_rx_data_next;
                acknack_rcvd_next = sda_o_next;
                phy_state_next = PHY_STATE_ACKNACK_H_3;
	    end
	    PHY_STATE_ACKNACK_H_3: begin
	   	scl_o_next = 1'b1;
		phy_state_next = PHY_STATE_ACTIVE;
	    end
            PHY_STATE_STOP_1: begin
		// stop bit
                scl_o_next = 1'b1;
                delay_scl_next = 1'b1;
                delay_next = prescale;
                phy_state_next = PHY_STATE_STOP_2;
            end
            PHY_STATE_STOP_2: begin
		// stop bit
                sda_o_next = 1'b1;
                delay_next = prescale;
                phy_state_next = PHY_STATE_STOP_3;
            end
            PHY_STATE_STOP_3: begin
		// stop bit
                bus_control_next = 1'b0;
                phy_state_next = PHY_STATE_INITIAL;
            end
        endcase
    end
end
	

always @(posedge clk) begin
    if (rst) begin
        state_reg <= STATE_INITIAL;
        phy_state_reg <= PHY_STATE_INITIAL;
        delay_reg <= 16'd0;
        delay_scl_reg <= 1'b0;
        delay_sda_reg <= 1'b0;
        scl_o_reg <= 1'b1;
        sda_o_reg <= 1'b1;
        busy_reg <= 1'b0;
        bus_active_reg <= 1'b0;
        bus_control_reg <= 1'b0;
        acknack_rcvd_reg <= 1'b0;
    end
    else begin
        state_reg <= state_next;
        phy_state_reg <= phy_state_next;

        delay_reg <= delay_next;
        delay_scl_reg <= delay_scl_next;
        delay_sda_reg <= delay_sda_next;


        scl_o_reg <= scl_o_next;
        sda_o_reg <= sda_o_next;

	busy_reg <= !(state_reg == STATE_INITIAL) || !(phy_state_reg == PHY_STATE_INITIAL || phy_state_reg == PHY_STATE_ACTIVE);
	if (start_bit) begin
            bus_active_reg <= 1'b1;
        end else if (stop_bit) begin
            bus_active_reg <= 1'b0;
        end else begin
            bus_active_reg <= bus_active_reg;
        end

        bus_control_reg <= bus_control_next;
        acknack_rcvd_reg <= acknack_rcvd_next;
    end

    phy_rx_data_reg <= phy_rx_data_next;

    data_reg <= data_next;
    last_reg <= last_next;


    bit_count_reg <= bit_count_next;

    data_out_reg <= data_out_next;
    data_out_last_reg <= data_out_last_next;

    scl_i_reg <= scl_i;
    sda_i_reg <= sda_i;
    last_scl_i_reg <= scl_i_reg;
   last_sda_i_reg <= sda_i_reg;
end
  
  assign scl_i = scl_pin;
  assign scl_pin = scl_en ? scl_o : 1'bz;
  assign sda_i = sda_pin;
  assign sda_pin = sda_en ? sda_o : 1'bz;


endmodule


