package ArduinoCar;

import java.util.logging.Level;

import ArduinoCar.MeasurementUnit.DistanceUnit;
import ArduinoCar.algorithm.Wall;

public class InputProcessor implements ConsoleInputListener {

	private static Main m;

	private static InputProcessor instance;
	
	/**
	 * Needs to be initalized to obtain the main reference
	 * @param main
	 */
	public static void init(Main main) {
		m = main;
		instance = new InputProcessor();
	}

	public static InputProcessor getInstance() {
		return instance;
	}

	@Override
	public void handleInput(String input) {
		if(input.equals("exit")) {
			m.close();
		} 
		else if(input.toLowerCase().equals("w")) {
			m.t.toggleAction();
		}
		else if(input.toLowerCase().startsWith("forward") || input.toLowerCase().startsWith("f ")) {
//			System.out.println(input);
			Steering.FORWARD.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("right") || input.toLowerCase().startsWith("r ")) {
//			System.out.println(input);
			Steering.RIGHT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("left") || input.toLowerCase().startsWith("l ")) {
//			System.out.println(input);
			Steering.LEFT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("_left") || input.toLowerCase().startsWith("_l ")) {
//			System.out.println(input);
			String[] split = input.split(" ");
			if(split.length == 3) {
				Steering.turnAndGo(m.getConnection(), Integer.parseInt(split[1]), Steering.LEFT, Boolean.parseBoolean(split[2]));
			} else {
				Steering.turnAndGo(m.getConnection(), Integer.parseInt(split[1]), Steering.LEFT, true);
			}
//			Steering.LEFT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("_right") || input.toLowerCase().startsWith("_r ")) {
//			System.out.println(input);
			String[] split = input.split(" ");
			if(split.length == 3) {
				Steering.turnAndGo(m.getConnection(), Integer.parseInt(split[1]), Steering.RIGHT, Boolean.parseBoolean(split[2]));
			} else {
				Steering.turnAndGo(m.getConnection(), Integer.parseInt(split[1]), Steering.RIGHT, true);
			}
//			Steering.LEFT.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("backward") || input.toLowerCase().startsWith("b ")) {
//			System.out.println(input);
			Steering.BACKWARD.go(m.getConnection(), Integer.parseInt(input.split(" ")[1]));
		}
		else if(input.toLowerCase().startsWith("stop") || input.toLowerCase().equals("s")) {
//			System.out.println(input);
//			Steering.STOP.go(m.getConnection(), 0);	
			Steering.stop(m.getConnection());
		}
		else if(input.toLowerCase().startsWith("servo")) {
			String[] splitted = input.split(" ");
			if(splitted[1].equals("get")) {
				m.getConnection().write("y");
			}
			else if(splitted[1].equals("set")) {
				try {
					int angle = Integer.valueOf(splitted[2]);
					m.getConnection().write("z 1 " + angle);
					GuiConsole.getInstance().updateAngle(angle);
				} catch(NumberFormatException e) {
					Main.log.log(Level.WARNING, "You need to specify an angle!");
				}
			}
		}
		else {
			Main.log.log(Level.INFO, "Writing: " + input);
			m.getConnection().write(input);
		}
	}
}
