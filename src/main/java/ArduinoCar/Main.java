package ArduinoCar;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import ArduinoCar.MessageEvent.Type;
import ArduinoCar.algorithm.IAlgorithm;
import ArduinoCar.algorithm.Wall;
import ArduinoCar.sensors.SensorThread;

public class Main {

	//GPIO 17

	//Michael Jacksons Whiskey Buch

	public static AdvancedLogger log;
	SerialConnection connection;
	Scanner scan;
	Context pi4j;
	static boolean isAlive = true;
	static boolean onRasp = false;
	RaspberrySensors sensors;
	FileLogger test = new FileLogger(new File("test.log"));
	SensorThread t;
	
	IAlgorithm currentAlgo;
	
	static {
		System.setProperty("-Djava.util.logging.SimpleFormatter.format=", "'%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %5$s%6$s%n'");
		System.setProperty("-cacao", "");
	}
	
	public Main(String port, String sensor) {
		InputProcessor.init(this);
		if(onRasp) {
			connection = new SerialConnection(port);
			connection.addReceivedListener(new ReceivedListener() {
	
				@Override
				public void receiveMessage(MessageEvent event) {
					if(event.type == Type.String) {
						if(event.getMessage().startsWith("Servo")) {
							String[] splitted = event.getMessage().split(" ");
	//						int servoNr = Integer.valueOf(splitted[1]);
							int angle = Integer.valueOf(splitted[3]);
							GuiConsole.getInstance().updateAngle(angle);
						} else {
							log.log(Level.INFO, "\""+event.getMessage()+"\"");
						}
					} else if(event.type == Type.Char) {
						//            		log.log(Level.INFO, "\""+event.getChar()+"\"");
					}
				}
	
				@Override
				public void receiveByte(MessageEvent event) {}
			});
		}
//		scan = new Scanner(System.in);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				while(true) {
////					String input = scan.nextLine();
//					if(GuiConsole.getInstance().hasNext()) {
//						String input = GuiConsole.getInstance().getNext();
//						System.out.println(input);
////						InputProcessor.input(input);
//					}
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						log.getLogger().log(Level.WARNING, e.getMessage());
//					}
//				}
//			}
//
//		}).start();
		GuiConsole.getInstance().addInputListener(InputProcessor.getInstance());
		if(onRasp) {
			pi4j = Pi4J.newAutoContext();
			sensors = new RaspberrySensors(this);
			sensors.init(sensor);
			sensors.run();
			currentAlgo = new Wall(sensors);
			t = new SensorThread((int)(1/2f*1000)) {
				
				@Override
				public void doAction() {
					if(currentAlgo != null) {
						currentAlgo.loop();
					} else {
						System.out.println("Algo null");
					}
				}

				@Override
				public void init() {
					if(currentAlgo != null) {
						currentAlgo.init();
					}
					this.stopAction();
				}
			};
//			t.stopAction();
		}
	}

	/**
	 * 
	 * @return the serial connection to the arduino
	 */
	public SerialConnection getConnection() {
		return connection;
	}

	public Context getPI4J() {
		return pi4j;
	}

	/**
	 * Obtain the input scanner
	 * @return System in scanner
	 */
	public Scanner getScanner() {
		return scan;
	}

	public static boolean isAlive() {
		return isAlive;
	}
	
	public void close() {
		isAlive = false;
		if(onRasp) {
			getScanner().close();
			getConnection().close();
			sensors.close();
		}
		GuiConsole.getInstance().close();
		System.exit(0);
	}
	
	/**
	 * The program main method
	 * @param args
	 */
	public static void main(String[] args) {
		new GuiConsole("Console", 800, 400);

		if(System.getProperty("os.arch").equals("arm")) {
			onRasp = true;
		}
		
		//Initialize the logger
		log = new AdvancedLogger(java.util.logging.Logger.getGlobal());

		// for the code below to work, it must be executed before the​
		//logger is created. see note below
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "NONE");

		try
		{
			LoggerFactory.getLogger("com.pi4j.provider.impl.DefaultRuntimeProviders");  //This is actually a MavenSimpleLogger, but due to various classloader issues, can't work with the directly.
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for(SerialPort p: SerialPort.getCommPorts()) {
			log.log(Level.INFO, p.getSystemPortPath() + " " + p.getDescriptivePortName());
		}

		log.log(Level.INFO, System.getProperty("os.name"));
		log.log(Level.INFO, System.getProperty("os.arch"));
		List<String> vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
		log.log(Level.INFO, "VM Arguments:");
		for(String s: vmArgs) {
			log.log(Level.INFO, s);
		}
		log.logger.addHandler(new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				System.out.println(record.getMessage());
				
			}
			
			@Override
			public void flush() {
				
			}
			
			@Override
			public void close() throws SecurityException {
				
			}
		});

		if(args.length > 0) {
			new Main(args[0], args[1]);
		} else {
			log.log(Level.WARNING, "You need to specify a serial port as an argument!");
		}
	}

}

