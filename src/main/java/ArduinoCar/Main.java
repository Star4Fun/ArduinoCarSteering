package ArduinoCar;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fazecast.jSerialComm.SerialPort;

import ArduinoCar.MessageEvent.Type;

public class Main {

	public static AdvancedLogger log;
	
	public Main(String port) {
		SerialConnection connection = new SerialConnection(port);
		log.log(Level.INFO, "Test");
		connection.addReceivedListener(new ReceivedListener() {
			
			@Override
			public void receiveMessage(MessageEvent event) {
				if(event.type == Type.String) {
            		log.log(Level.INFO, "\""+event.getMessage()+"\"");
				} else if(event.type == Type.Char) {
            		log.log(Level.INFO, "\""+event.getChar()+"\"");
				}
				
			}
			
			@Override
			public void receiveByte(MessageEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		Scanner scan = new Scanner(System.in);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					String input = scan.nextLine();
					if(input.equals("exit")) {
						connection.close();
						scan.close();
						System.exit(0);
					} else {
						connection.write(input);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						log.getLogger().log(Level.WARNING, e.getMessage());
					}
				}
			}
			
		}).start();

	}
	
	public static void main(String[] args) {
		log = new AdvancedLogger(Logger.getGlobal());
		
		for(SerialPort p: SerialPort.getCommPorts()) {
			log.log(Level.INFO, p.getSystemPortPath() + " " + p.getDescriptivePortName());
		}
		
		if(args.length > 0) {
			new Main(args[0]);
		} else {
			log.log(Level.WARNING, "You need to specify a serial port as an argument!");
		}
	}
	
}

