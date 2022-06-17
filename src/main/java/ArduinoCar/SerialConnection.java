package ArduinoCar;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialConnection {

	ArrayList<ReceivedListener> listeners = new ArrayList<ReceivedListener>();
	SerialPort port;
	Scanner portInput;
	PrintWriter portOutput;
	
	AdvancedLogger log = Main.log;
	
	boolean isOpen = false;
	
	/**
	 * Init a connection to the arduino
	 * @param serialPort the port on which the arduino is attached to
	 */
	public SerialConnection(String serialPort) {
		isOpen = true;
		port = SerialPort.getCommPort(serialPort);
//		port.setComPortParameters(9600,8,1,0);
//		port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

		port.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		port.openPort(0);
		
		portInput = new Scanner(port.getInputStream());
		portOutput = new PrintWriter(port.getOutputStream(), true);
		
		port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            String message = "";
            
            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
            	if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
            	}
                 byte[] newData = new byte[port.bytesAvailable()];
                 for(ReceivedListener list: listeners) {
                	 MessageEvent event = new MessageEvent(newData);
                	 list.receiveByte(event);
                 }
                 int numRead = port.readBytes(newData, newData.length);
                 if(numRead >= 1) {
                	 char[] ch = new char[newData.length];
                	 for(int i = 0; i < newData.length; i++) {
                		 ch[i] = (char)newData[i];
                	 }
                	 for(char c: ch) {
	                     if(c == '\n' || c == '\r') {
	                    	 if(!message.equals("") && !message.equals("\n")) {
	                    		 for(ReceivedListener list: listeners) {
	                    			 MessageEvent event = new MessageEvent(message);
	                    			 list.receiveMessage(event);
	                    		 }
	                    	 	message = "";
	                    	 }
	                     } else {
	                    	 message += c;
	                    	 for(ReceivedListener list: listeners) {
	                			 MessageEvent event = new MessageEvent(c);
	                			 list.receiveMessage(event);
	                		 }
	                     }
                	 }
                 } else {
                	 log.log(Level.WARNING, "Error received " + numRead + " bytes!");
                	 System.out.println(new String(newData));
                 }
            }
            
        });
	}
	
	/**
	 * Send a string to the arduino
	 * @param s the message
	 */
	public void write(String s) {
//		portOutput.write(s);
//		portOutput.flush();
		try {
			port.getOutputStream().write(s.getBytes());
			port.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the connection
	 */
	public void close() {
		if(isOpen) {
			port.removeDataListener();
			portInput.close();
			portOutput.close();
			port.closePort();
		}
	}
	
	
	public void addReceivedListener(ReceivedListener listen) {
		this.listeners.add(listen);
	}
	
	public void removeReceivedListener(ReceivedListener listen) {
		this.listeners.remove(listen);
	}

}
