package ArduinoCar;

public enum Steering {

	LEFT('l'),
	RIGHT('r'),
	FORWARD('f'),
	STOP('s'),
	BACKWARD('b');
	
	char command;
	
	/**
	 * 
	 * @param command the char to communicate with the arduino
	 */
	private Steering(char command) {
		this.command = command;
	}
	
	public char getCommand() {
		return command;
	}
	
	/**
	 * 
	 * @param connection the connection to the arduino
	 * @param speed the targeted speed
	 */
	public void go(SerialConnection connection, int speed) {
		connection.write(getCommand() + " " + speed);
	}
	
}
