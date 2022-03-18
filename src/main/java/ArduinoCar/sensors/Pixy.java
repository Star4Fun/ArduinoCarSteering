package ArduinoCar.sensors;

import java.nio.ByteBuffer;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

public class Pixy {

	final int PIXY_INITIAL_ARRAYSIZE = 30;
	final int PIXY_MAXIMUM_ARRAYSIZE = 130;
	final int PIXY_START_WORD        = 0xaa55;
	final int PIXY_START_WORD_CC     = 0xaa56;
	final int PIXY_START_WORDX       = 0x55aa;
	final int PIXY_MAX_SIGNATURE     = 7;
	final int PIXY_DEFAULT_ARGVAL    = 0xffff;

	// Pixy x-y position values
	final int PIXY_MIN_X              = 0;
	final int PIXY_MAX_X              = 319;
	final int PIXY_MIN_Y              = 0;
	final int PIXY_MAX_Y              = 199;

	// RC-servo values
	final int PIXY_RCS_MIN_POS        = 0;
	final int PIXY_RCS_MAX_POS        = 1000;
	final float PIXY_RCS_CENTER_POS   = ((float)(PIXY_RCS_MAX_POS-PIXY_RCS_MIN_POS)/2.0f);

	 
	enum BlockType
	{
		NORMAL_BLOCK,
		CC_BLOCK
	};

//	class Block 
//	{
//	  // print block structure!
//	  void print()
//	  {
//	    int i, j;
//	    char[] buf = new char[128];
//	    char[] sig = new char[6];
//	    int d;
//		boolean flag;	
//	    if (signature>PIXY_MAX_SIGNATURE) // color code! (CC)
//		{
//	      // convert signature number to an octal string
//	      for (i=12, j=0, flag=false; i>=0; i-=3)
//	      {
//	        d = (signature>>i)&0x07;
//	        if (d>0 && !flag) {
//	          flag = true;
//	        }
//	        if (flag) {
//	          sig[j++] = intToCharArray(d) + '0';
//	        }
//	      }
//	      sig[j] = '\0';	
//	      sprintf(buf, "CC block! sig: %s (%d decimal) x: %d y: %d width: %d height: %d angle %d\n", sig, signature, x, y, width, height, angle);
//	    }			
//		else // regular block.  Note, angle is always zero, so no need to print
//	      sprintf(buf, "sig: %d x: %d y: %d width: %d height: %d\n", signature, x, y, width, height);		
//	    Serial.print(buf); 
//	  }
//	  int signature;
//	  int x;
//	  int y;
//	  int width;
//	  int height;
//	  int angle;
//	};
	
	I2C pixy;
	
	public Pixy() {
		Context pi4j = Pi4J.newAutoContext();
		I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("TCA9534").provider("pigpio-i2c").bus(1).device(0x54).build();
		pixy = pi4j.create(i2cConfig);
		for(int i = 0; i < 6; i++) {
			setServos((short)i, (short)i);
			if(i == 5) {
				i = 0;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte[] shortToByteArray(short i) {
		System.out.println(i);
//		byte[] b = ByteBuffer.allocate(2).putInt(i).array();
//		System.out.println(b.length);
		return new byte[] {
		        (byte)((i >> 8) & 0xff),
		        (byte)((i >> 0) & 0xff),
		    };
	}
	
	public int setBrightness(byte brightness)
	{
	  byte[] outBuf = new byte[3];
	   
	  outBuf[0] = (byte)0x00;
	  outBuf[1] = (byte)0xfe; 
	  outBuf[2] = (byte)brightness;
	  
	  return pixy.write(outBuf, 3);
	}

	public int setLED(byte r, byte g, byte b)
	{
	  byte[] outBuf = new byte[5];
	  
	  outBuf[0] = (byte)0x00;
	  outBuf[1] = (byte)0xfd; 
	  outBuf[2] = r;
	  outBuf[3] = g;
	  outBuf[4] = b;
	  
	  return pixy.write(outBuf, 5);
	}
	
	/**
	 * 
	 * @param s0 length 2 -> double byte
	 * @param s1 length 2 -> double byte
	 * @return
	 */
	public int setServos(short s0, short s1) {
		byte[] outBuf = new byte[6];

		outBuf[0] = (byte)0x00;
		outBuf[1] = (byte)0xff; 
		byte[] b = shortToByteArray(s0);
		outBuf[2] = b[0];
		outBuf[3] = b[1];
		b = shortToByteArray(s1);
		outBuf[4] = b[0];
		outBuf[5] = b[1];

		return pixy.write(outBuf);
	}

	public char[] intToCharArray(int i) {
		return new Integer(i).toString().toCharArray();
	}

//	template <class LinkType> class TPixy
//	{
//	public:
//	  TPixy(uint16_t arg=PIXY_DEFAULT_ARGVAL);
//	  ~TPixy();
//		
//	  uint16_t getBlocks(uint16_t maxBlocks=1000);
//	  int8_t setServos(uint16_t s0, uint16_t s1);
//	  int8_t setBrightness(uint8_t brightness);
//	  int8_t setLED(uint8_t r, uint8_t g, uint8_t b);
//	  void init();
//	  
//	  Block *blocks;
//		
//	private:
//	  boolean getStart();
//	  void resize();
//
//	  LinkType link;
//	  boolean  skipStart;
//	  BlockType blockType;
//	  uint16_t blockCount;
//	  uint16_t blockArraySize;
//	};
//
//
//	template <class LinkType> TPixy<LinkType>::TPixy(uint16_t arg)
//	{
//	  skipStart = false;
//	  blockCount = 0;
//	  blockArraySize = PIXY_INITIAL_ARRAYSIZE;
//	  blocks = (Block *)malloc(sizeof(Block)*blockArraySize);
//	  link.setArg(arg);
//	}
//
//	template <class LinkType> void TPixy<LinkType>::init()
//	{
//	  link.init();
//	}
//
//	template <class LinkType> TPixy<LinkType>::~TPixy()
//	{
//	  free(blocks);
//	}
//
//	template <class LinkType> boolean TPixy<LinkType>::getStart()
//	{
//	  uint16_t w, lastw;
//	 
//	  lastw = 0xffff;
//	  
//	  while(true)
//	  {
//	    w = link.getWord();
//	    if (w==0 && lastw==0)
//		{
//	      delayMicroseconds(10);
//		  return false;
//		}		
//	    else if (w==PIXY_START_WORD && lastw==PIXY_START_WORD)
//		{
//	      blockType = NORMAL_BLOCK;
//	      return true;
//		}
//	    else if (w==PIXY_START_WORD_CC && lastw==PIXY_START_WORD)
//		{
//	      blockType = CC_BLOCK;
//	      return true;
//		}
//		else if (w==PIXY_START_WORDX)
//		{
//		  Serial.println("reorder");
//		  link.getByte(); // resync
//		}
//		lastw = w; 
//	  }
//	}
//
//	template <class LinkType> void TPixy<LinkType>::resize()
//	{
//	  blockArraySize += PIXY_INITIAL_ARRAYSIZE;
//	  blocks = (Block *)realloc(blocks, sizeof(Block)*blockArraySize);
//	}  
//			
//	template <class LinkType> uint16_t TPixy<LinkType>::getBlocks(uint16_t maxBlocks)
//	{
//	  uint8_t i;
//	  uint16_t w, checksum, sum;
//	  Block *block;
//	  
//	  if (!skipStart)
//	  {
//	    if (getStart()==false)
//	      return 0;
//	  }
//	  else
//		skipStart = false;
//		
//	  for(blockCount=0; blockCount<maxBlocks && blockCount<PIXY_MAXIMUM_ARRAYSIZE;)
//	  {
//	    checksum = link.getWord();
//	    if (checksum==PIXY_START_WORD) // we've reached the beginning of the next frame
//	    {
//	      skipStart = true;
//		  blockType = NORMAL_BLOCK;
//		  //Serial.println("skip");
//	      return blockCount;
//	    }
//		else if (checksum==PIXY_START_WORD_CC)
//		{
//		  skipStart = true;
//		  blockType = CC_BLOCK;
//		  return blockCount;
//		}
//	    else if (checksum==0)
//	      return blockCount;
//	    
//		if (blockCount>blockArraySize)
//			resize();
//		
//		block = blocks + blockCount;
//		
//	    for (i=0, sum=0; i<sizeof(Block)/sizeof(uint16_t); i++)
//	    {
//		  if (blockType==NORMAL_BLOCK && i>=5) // skip 
//		  {
//			block->angle = 0;
//			break;
//		  }
//	      w = link.getWord();
//	      sum += w;
//	      *((uint16_t *)block + i) = w;
//	    }
//
//	    if (checksum==sum)
//	      blockCount++;
//	    else
//	      Serial.println("cs error");
//		
//		w = link.getWord();
//		if (w==PIXY_START_WORD)
//		  blockType = NORMAL_BLOCK;
//		else if (w==PIXY_START_WORD_CC)
//		  blockType = CC_BLOCK;
//		else
//	      return blockCount;
//	  }
//	}
//


}
