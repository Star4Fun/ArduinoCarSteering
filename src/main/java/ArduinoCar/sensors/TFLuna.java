package ArduinoCar.sensors;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

import ArduinoCar.MeasurementUnit;

public class TFLuna extends MeasurementUnit {

	static final int TFL_DEF_ADR = 0x10; // default I2C address = 16
	static final int TFL_DEF_FPS = 0x64; // default frame-rate = 100fps

	// - - - - Register Names and Numbers - - - -
	static final int TFL_DIST_LO = 0x00; // R Unit: cm
	static final int TFL_DIST_HI = 0x01; // R
	static final int TFL_FLUX_LO = 0x02; // R
	static final int TFL_FLUX_HI = 0x03; // R
	static final int TFL_TEMP_LO = 0x04; // R Unit: 0.01 Celsius
	static final int TFL_TEMP_HI = 0x05; // R
	static final int TFL_TICK_LO = 0x06; // R Timestamp
	static final int TFL_TICK_HI = 0x07; // R
	static final int TFL_ERR_LO = 0x08; // R
	static final int TFL_ERR_HI = 0x09; // R
	static final int TFL_VER_REV = 0x0A; // R
	static final int TFL_VER_MIN = 0x0B; // R
	static final int TFL_VER_MAJ = 0x0C; // R

	static final int TFL_SAVE_SETTINGS = 0x20; // W -- Write 0x01 to save
	static final int TFL_SOFT_RESET = 0x21; // W -- Write 0x02 to reboot.
	// Lidar not accessible during few seconds,
	// then register value resets automatically
	static final int TFL_SET_I2C_ADDR = 0x22; // W/R -- Range 0x08,0x77.
	// Must reboot to take effect.
	static final int TFL_SET_TRIG_MODE = 0x23; // W/R -- 0-continuous, 1-trigger
	static final int TFL_TRIGGER = 0x24; // W -- 1-trigger once
	static final int TFL_DISABLE = 0x25; // W/R -- 0-disable, 1-enable
	static final int TFL_FPS_LO = 0x26; // W/R -- lo byte
	static final int TFL_FPS_HI = 0x27; // W/R -- hi byte
	static final int TFL_SET_LO_PWR = 0x28; // W/R -- 0-normal, 1-low power
	static final int TFL_HARD_RESET = 0x29; // W -- 1-restore factory settings

	/////// FPS (Low Power Mode) ///////
	static final int FPS_1 = 0x01;
	static final int FPS_2 = 0x02;
	static final int FPS_3 = 0x03;
	static final int FPS_4 = 0x04;
	static final int FPS_5 = 0x05;
	static final int FPS_6 = 0x06;
	static final int FPS_7 = 0x07;
	static final int FPS_8 = 0x08;
	static final int FPS_9 = 0x09;
	static final int FPS_10 = 0x0A;

	////// FPS (High Power Mode) /////
	static final int FPS_35 = 0x23;
	static final int FPS_50 = 0x32;
	static final int FPS_100 = 0x64;
	static final int FPS_125 = 0x7D;
	static final int FPS_250 = 0xFA;

	// Error Status Condition definitions
	static final int TFL_READY = 0; // no error
	static final int TFL_SERIAL = 1; // serial timeout
	static final int TFL_HEADER = 2; // no header found
	static final int TFL_CHECKSUM = 3; // checksum doesn't match
	static final int TFL_TIMEOUT = 4; // I2C timeout
	static final int TFL_PASS = 5; // reply from some system commands
	static final int TFL_FAIL = 6; // "
	static final int TFL_I2CREAD = 7;
	static final int TFL_I2CWRITE = 8;
	static final int TFL_I2CLENGTH = 9;
	static final int TFL_WEAK = 10; // Signal Strength ≤ 100
	static final int TFL_STRONG = 11; // Signal Strength saturation
	static final int TFL_FLOOD = 12; // Ambient Light saturation
	static final int TFL_MEASURE = 13;
	static final int TFL_INVALID = 14; // Invalid operation sent to sendCommand()

	int tfStatus = 0;
	
	I2C tfLuna;

	int flux, dist, temp;
	
	byte[] dataArray;

	public TFLuna(Context pi4j) {
		super(pi4j);
		I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("TCA9534").provider("pigpio-i2c").bus(1).device(TFL_DEF_ADR)
				.build();
		tfLuna = pi4j.create(i2cConfig);
		int[] i = this.Get_Firmware_Version(0x10);
		for(int ii: i) {
			System.out.println(ii);
		}
		
		// TODO Auto-generated constructor stub
	}

	public Data getData(int addr) {
		int dist, flux, temp;
		tfStatus = TFL_READY; // clear status of any error condition
		dataArray = new byte[6];
		boolean result = false;

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// Step 1 - Use the `Wire` function `readReg` to fill the six byte
		// `dataArray` from the contiguous sequence of registers `TFL_DIST_LO`
		// to `TFL_TEMP_HI` that declared in the header file 'TFLI2C.h`.
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		for (int reg = TFL_DIST_LO; reg <= TFL_TEMP_HI; reg++) {
			tfLuna.readRegister(reg);
			byte b;
			
			if ((b = tfLuna.readRegisterByte(reg)) == -1) {

			} else {
				dataArray[reg] = b;
			}
		}
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// Step 2 - Shift data from read array into the three variables
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		dist = dataArray[0] + (dataArray[1] << 8);
		flux = dataArray[2] + (dataArray[3] << 8);
		temp = dataArray[4] + (dataArray[5] << 8);
		
		/*
		 * // Convert temperature from hundredths // of a degree to a whole number temp
		 * = int16_t( temp / 100); // Then convert Celsius to degrees Fahrenheit temp =
		 * uint8_t( temp * 9 / 5) + 32;
		 */

		// - - Evaluate Abnormal Data Values - -
		// Signal strength <= 100
		if (flux < 100) {
			tfStatus = TFL_WEAK;
			result = false;
		}
		// Signal Strength saturation
		else if (flux == 0xFFFF) {
			tfStatus = TFL_STRONG;
			result = false;
		} else {
			tfStatus = TFL_READY;
			result = true;
		}
		return new Data(result, flux, dist, temp);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - -
//	              EXPLICIT COMMANDS
	// - - - - - - - - - - - - - - - - - - - - - - - - - -

	// = = GET DEVICE TIME (in milliseconds) = = =
	// Pass back time as an unsigned 16-bit variable
	public int[] Get_Time(int tim, int adr)
	{
	    // Recast the address of the unsigned integer `tim`
	    // as a pointer to an unsigned byte `p_tim`...
//	    uint8_t * p_tim = (uint8_t *) &tim;
		int[] p_tim = new int[2];
		
		int reg = readReg(TFL_TICK_LO, adr);
	    // ... then address the pointer as an array.
	    if(reg== -1) {
	    	return new int[] {-1};
	    } else {
	    	p_tim[0] = reg;  // Read into `tim` array
	    }
		reg = readReg(TFL_TICK_HI, adr);
	    if(reg == -1) {
	    	return new int[] {-1};
	    } else {
	    	p_tim[1] = reg;  // Read into `tim` array
	    }
	    return p_tim;
	}

	// = = GET PRODUCTION CODE (Serial Number) = = =
	// When you pass an array as a parameter to a function
	// it decays into a pointer to the first element of the array.
	// The 14 byte array variable `tfCode` declared in the example
	// sketch decays to the array pointer `p_cod`.
	public int[] Get_Prod_Code(int p_cod, int adr) {
		int[] n = new int[14];
		for (int i = 0; i < 14; ++i) {
			int reg = this.readReg(0x10 + i, adr);
			if (reg == -1) {
				return new int[] {-1};
			} else {
				n[i] = reg; // Read into product code array
			}
		}
		return n;
	}

	// = = = = GET FIRMWARE VERSION = = = =
	// The 3 byte array variable `tfVer` declared in the
	// example sketch decays to the array pointer `p_ver`.
	public int[] Get_Firmware_Version(int adr)
	{
		int[] ver = new int[3];
	    for (int i = 0; i < 3; ++i)
	    {
	    	int reg = readReg((0x0A + i), adr);
	      if(reg == -1) {
	    	  return new int[]{-1, -1, -1};
	      } else {
	    	  ver[i] = reg;  // Read into version array
	      }
	    }
	    return ver;
	}

	//  = = = = =    SAVE SETTINGS   = = = = =
	public boolean Save_Settings(int adr)
	{
	    return( writeReg( TFL_SAVE_SETTINGS, adr, 1));
	}

	// = = = = SOFT (SYSTEM) RESET = = = =
	public boolean Soft_Reset(int adr)
	{
	    return( writeReg( TFL_SOFT_RESET, adr, 2));
	}

	// = = = = = = SET I2C ADDRESS = = = = = =
	// Range: 0x08, 0x77. Must reboot to take effect.
	public boolean Set_I2C_Addr(int adrNew, int adr)
	{
	    return( writeReg( TFL_SET_I2C_ADDR, adr, adrNew));
	}

	// = = = = = SET ENABLE = = = = =
	public boolean Set_Enable(int adr)
	{
	    return( writeReg( TFL_DISABLE, adr, 1));
	}

	// = = = = = SET DISABLE = = = = =
	public boolean Set_Disable(int adr)
	{
	    return( writeReg( TFL_DISABLE, adr, 0));
	}

	// = = = = = = SET FRAME RATE = = = = = =
	public boolean Set_Frame_Rate(int[] frm, int adr)
	{
	    // Recast the address of the unsigned integer `frm`
	    // as a pointer to an unsigned byte `p_frm` ...
//	    uint8_t * p_frm = (uint8_t *) &frm;

	    // ... then address the pointer as an array.
	    if( !writeReg( ( TFL_FPS_LO), adr, frm[ 0])) return false;
	    if( !writeReg( ( TFL_FPS_HI), adr, frm[ 1])) return false;
	    return true;
	}

	//  = = = = = =    GET FRAME RATE   = = = = = =
	public boolean Get_Frame_Rate( int[] frm, int adr)
	{
//	    uint8_t * p_frm = (uint8_t *) &frm;
		int reg = readReg(TFL_FPS_LO, adr);
		if(reg != -1) {
			return false;
		} else {
			frm[0] = reg;
		}
		reg = readReg(TFL_FPS_HI, adr);
		if(reg != -1) {
			return false;
		} else {
			frm[1] = reg;
		}
		return true;
	}

	//  = = = =   HARD RESET to Factory Defaults  = = = =
	public boolean Hard_Reset(int adr)
	{
	    return( writeReg( TFL_HARD_RESET, adr, 1));
	}

	// = = = = = = SET CONTINUOUS MODE = = = = = =
	// Sample LiDAR chip continuously at Frame Rate
	public boolean Set_Cont_Mode(int adr)
	{
	    return( writeReg( TFL_SET_TRIG_MODE, adr, 0));
	}

	// = = = = = = SET TRIGGER MODE = = = = = =
	// Device will sample only once when triggered
	public boolean Set_Trig_Mode(int adr)
	{
	    return( writeReg( TFL_SET_TRIG_MODE, adr, 1));
	}

	// = = = = = = SET TRIGGER = = = = = =
	// Trigger device to sample once
	public boolean Set_Trigger(int adr)
	{
	    return( writeReg( TFL_TRIGGER, adr, 1));
	}
	//
	// = = = = = = = = = = = = = = = = = = = = = = = =

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	       READ OR WRITE A GIVEN REGISTER OF THE SLAVE DEVICE
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public int readReg(int nmbr, int addr)
	{
//	  Wire.beginTransmission( addr);
//	  Wire.write( nmbr);
//
//	  if( Wire.endTransmission() != 0)  // If write error...
//	  {
//	    tfStatus = TFL_I2CWRITE;        // then set status code...
//	    return false;                   // and return `false`.
//	  }
//	  // Request 1 byte from the device
//	  // and release bus when finished.p
//	    if( Wire.peek() == -1)            // If read error...
//	    {
//	      tfStatus = TFL_I2CREAD;         // then set status code.
//	      return false;
//	    }
//	  regReply = ( uint8_t)Wire.read();   // Read the received data...
//	  return true;
		return tfLuna.readRegister(addr);
	}

	public boolean writeReg( int nmbr, int addr, int data)
	{
		int i = tfLuna.writeRegister(nmbr, data);
		return i == 0 ? true : false;
//	  Wire.beginTransmission( addr);
//	  Wire.write( nmbr);
//	  Wire.write( data);
//	  if( Wire.endTransmission( true) != 0)  // If write error...
//	  {
//	    tfStatus = TFL_I2CWRITE;        // then set status code...
//	    return false;                   // and return `false`.
//	  }
//	  else return true;
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// - - - - - The following is for testing purposes - - - -
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	// Called by either `printFrame()` or `printReply()`
	// Print status condition either `READY` or error type
	public void printStatus()
	{
		System.out.print("Status: ");
	    if( tfStatus == TFL_READY)          System.out.print( "READY");
	    else if( tfStatus == TFL_SERIAL)    System.out.print( "SERIAL");
	    else if( tfStatus == TFL_HEADER)    System.out.print( "HEADER");
	    else if( tfStatus == TFL_CHECKSUM)  System.out.print( "CHECKSUM");
	    else if( tfStatus == TFL_TIMEOUT)   System.out.print( "TIMEOUT");
	    else if( tfStatus == TFL_PASS)      System.out.print( "PASS");
	    else if( tfStatus == TFL_FAIL)      System.out.print( "FAIL");
	    else if( tfStatus == TFL_I2CREAD)   System.out.print( "I2C-READ");
	    else if( tfStatus == TFL_I2CWRITE)  System.out.print( "I2C-WRITE");
	    else if( tfStatus == TFL_I2CLENGTH) System.out.print( "I2C-LENGTH");
	    else if( tfStatus == TFL_WEAK)      System.out.print( "Signal weak");
	    else if( tfStatus == TFL_STRONG)    System.out.print( "Signal strong");
	    else if( tfStatus == TFL_FLOOD)     System.out.print( "Ambient light");
	    else if( tfStatus == TFL_INVALID)   System.out.print( "No Command");
	    else System.out.print( "OTHER");
	}

	// Print error type and HEX values
	// of each byte in the data frame
	public void printDataArray()
	{
	    printStatus();
	    // Print the Hex value of each byte of data
	    System.out.print(" Data:");
	    for(int i = 0; i < 6; i++)
	    {
	      System.out.print(" ");
	      System.out.print( dataArray[ i] < 16 ? "0" : "");
//	      System.out.print( dataArray[ i], HEX);
	    }
	    System.out.println();
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	@Override
	public void exit() {
		tfLuna.close();
	}

	@Override
	public double getDistance(I2C vl53, DistanceUnit unit) {
		Data data = getData(TFL_DEF_ADR);
		return (!data.isError() ? 0 : data.getDist());
	}
	
	@Override
	public double getDistance(DistanceUnit unit) {
		return getDistance(tfLuna, unit);
	}

	class Data {
		
		final int flux, dist, temp;
		final boolean error;
		
		public Data(boolean error, int flux, int dist, int temp) {
			this.error = error;
			this.flux = flux;
			this.dist = dist;
			this.temp = temp;
		}
		
		public boolean isError() {
			return error;
		}
		
		public int getDist() {
			return dist;
		}
		
		public int getFlux() {
			return flux;
		}
		
		public int getTemp() {
			return temp;
		}
	}
	
}
