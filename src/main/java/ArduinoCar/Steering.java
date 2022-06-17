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
		GuiConsole.getInstance().updateInformation(this, speed);
		connection.write(getCommand() + " " + speed);
	}
	
	public static void stop(SerialConnection connection) {
		STOP.go(connection, 0);
	}
	
	public static void turnAndGo(SerialConnection connection, int speed, Steering steering, boolean forward) {
		try {
			Steering dir, turn;
			if(forward)	{
				dir = FORWARD;
			} else {
				dir = BACKWARD;
			}
			switch(steering) {
			case LEFT:
					turn = LEFT;
				break;
			case RIGHT: 
					turn = RIGHT;
				break;
			default:
					return;
			}
		
			int delay = 750; //1050
			
			dir.go(connection, speed);
			Thread.sleep(delay);
			turn.go(connection, speed);
			Thread.sleep(delay);
			dir.go(connection, speed);
			Thread.sleep(delay);
			stop(connection);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
