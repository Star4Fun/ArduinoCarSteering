package ArduinoCar;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.Pi4J;

import ArduinoCar.MessageEvent.Type;
import ArduinoCar.sensors.HCSR04;
import io.github.pseudoresonance.pixy2api.Pixy2;
import io.github.pseudoresonance.pixy2api.Pixy2.LinkType;
import io.github.pseudoresonance.pixy2api.Pixy2CCC.Block;

public class Main {

	//GPIO 17

	public static AdvancedLogger log;
	SerialConnection connection;
	Scanner scan;
	static Pixy2 pixy;
	static Pi4J pi4j;

	public Main(String port) {
		InputProcessor.init(this);
		connection = new SerialConnection(port);
		connection.addReceivedListener(new ReceivedListener() {

			@Override
			public void receiveMessage(MessageEvent event) {
				if(event.type == Type.String) {
					log.log(Level.INFO, "\""+event.getMessage()+"\"");
				} else if(event.type == Type.Char) {
					//            		log.log(Level.INFO, "\""+event.getChar()+"\"");
				}

			}

			@Override
			public void receiveByte(MessageEvent event) {

			}
		});
		scan = new Scanner(System.in);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					String input = scan.nextLine();
					InputProcessor.input(input);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						log.getLogger().log(Level.WARNING, e.getMessage());
					}
				}
			}

		}).start();

	}

	/**
	 * 
	 * @return the serial connection to the arduino
	 */
	public SerialConnection getConnection() {
		return connection;
	}

	public Pi4J getPI4J() {
		return pi4j;
	}

	/**
	 * Obtain the input scanner
	 * @return System in scanner
	 */
	public Scanner getScanner() {
		return scan;
	}

	/**
	 * The program main method
	 * @param args
	 */
	public static void main(String[] args) {
		log = new AdvancedLogger(java.util.logging.Logger.getGlobal());

		// for the code below to work, it must be executed before the​
		//logger is created. see note below
	    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "NONE");
	       
	    
	    
	    try
	    {
	        Logger l = LoggerFactory.getLogger("com.pi4j.provider.impl.DefaultRuntimeProviders");  //This is actually a MavenSimpleLogger, but due to various classloader issues, can't work with the directly.
	        Field f = l.getClass().getSuperclass().getDeclaredField("currentLogLevel");
	        f.setAccessible(true);
	        f.set(l, LocationAwareLogger.WARN_INT);
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
//		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
//
//		Logger sl = LoggerFactory.getLogger(Main.class);
//		sl.trace("trace");
//		sl.debug("debug");
//		sl.info("info");
//		sl.warn("warning");
//		sl.error("error");
//		Logger pi = LoggerFactory.getLogger(Pi4J.class);
		

		for(SerialPort p: SerialPort.getCommPorts()) {
			log.log(Level.INFO, p.getSystemPortPath() + " " + p.getDescriptivePortName());
		}

		System.out.println(System.getProperty("os.name"));
		System.out.println(System.getProperty("os.arch"));

		if(System.getProperty("os.arch").equals("arm")) {

			//			new PITest();
			//			new VL53L0X();
			new HCSR04();
			
			System.exit(0);
			pixy = Pixy2.createInstance(LinkType.I2C);
			pixy.init();
			pixy.setLED(Color.GREEN);
			pixy.setLamp((byte)0, (byte)0);
			pixy.getResolution();
			pixy.getCCC().getBlocks();
			ArrayList<Block> blocks = pixy.getCCC().getBlockCache();

			while(true) {
				pixy.getCCC().getBlocks();
				for(Block b: blocks) {
					b.print();
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if(args.length > 0) {
			new Main(args[0]);
		} else {
			log.log(Level.WARNING, "You need to specify a serial port as an argument!");
		}
	}

}

