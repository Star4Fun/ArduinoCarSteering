package ArduinoCar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class FileLogger {

	File theFile;
	
	private BufferedWriter bw;
	
	public FileLogger(File theFile) {
		try {
			System.out.println("Outputting to: " + theFile.getAbsolutePath());
			this.theFile = theFile;
			this.bw = new BufferedWriter(new FileWriter(theFile));
			init();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		Date now = Calendar.getInstance().getTime();
		this.log(now.toString());
	}
	
	public void log(String line) {
		try {
			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
