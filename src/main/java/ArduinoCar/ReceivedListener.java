package ArduinoCar;

public interface ReceivedListener {

	public void receiveMessage(MessageEvent event);
	
	public void receiveByte(MessageEvent event);
	
}
