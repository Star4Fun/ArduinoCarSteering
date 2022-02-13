package ArduinoCar;

public enum Steering {

	LEFT('l'),
	RIGHT('r'),
	FORWARD('f'),
	BREAK('b'),
	REVERSE('z');
	
	char command;
	
	private Steering(char command) {
		this.command = command;
	}
	
	public char getCommand() {
		return command;
	}
	
	public void go(SerialConnection connection, float speed) {
		connection.write(getCommand() + " " + speed);
	}
	
}
