package ArduinoCar;

import java.util.logging.Level;

public class InputProcessor {

	private static Main m;
	
	/**
	 * Needs to be initalized to obtain the main reference
	 * @param main
	 */
	public static void init(Main main) {
		m = main;
	}

	/**
	 * 
	 * @param input used to process user input
	 */
	public static void input(String input) {
		if(input.equals("exit")) {
			m.getConnection().close();
			m.getScanner().close();
			System.exit(0);
		} 
		else if(input.toLowerCase().startsWith("forward")) {
			System.out.println(input);
			Steering.FORWARD.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("right")) {
			System.out.println(input);
			Steering.RIGHT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("left")) {
			System.out.println(input);
			Steering.LEFT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("backward")) {
			System.out.println(input);
			Steering.BACKWARD.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("stop")) {
			System.out.println(input);
			Steering.STOP.go(m.getConnection(), 0);	
		}
		else if(input.toLowerCase().startsWith("servo")) {
			String[] splitted = input.split(" ");
			if(splitted[1].equals("get")) {
				m.getConnection().write("y");
			}
			else if(splitted[1].equals("set")) {
				try {
					int angle = Integer.valueOf(splitted[2]);
					m.getConnection().write("z " + angle);
				} catch(NumberFormatException e) {
					Main.log.log(Level.WARNING, "You need to specify an angle!");
				}
			}
		}
		else {
			m.getConnection().write(input);
		}
	}
}
