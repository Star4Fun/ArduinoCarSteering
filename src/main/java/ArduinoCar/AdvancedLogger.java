package ArduinoCar;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvancedLogger {
	
	Logger logger;
	
	public AdvancedLogger(Logger logger) {
		this.logger = logger;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public void log(Level level, String message) {
		logger.log(level, Thread.currentThread().getStackTrace()[2] + " " + message);
	}
}
