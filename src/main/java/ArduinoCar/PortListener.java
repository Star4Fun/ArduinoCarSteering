package ArduinoCar;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PortListener implements SerialPortDataListener {

	@Override
	public int getListeningEvents() {
		// TODO Auto-generated method stub
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
//		byte[] bytes = event.getReceivedData();
//		for(int i = 0; i < bytes.length; i++) {
//			System.out.print((char)bytes[i]);
//		}
//		System.out.println("\n");
	}

}
