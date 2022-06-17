package ArduinoCar.sensors;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.i2c.I2C;

public abstract class ElectricComponent {

	static DigitalInputProvider digitalInputProvider;
	static DigitalOutputProvider digitalOutputProvider;

	public static final DecimalFormat df;

	static {
		df = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
		df.applyPattern("#.###");
	}
	
	protected Context pi4j;

	public ElectricComponent(Context pi4j) {
		this.pi4j = pi4j;
	}

	// This will read the pulse input and the stop signal
	protected DigitalInput createDigitalInput(int pinInput, Context pi4j) {
		try {
			DigitalInputConfig config = DigitalInput.newConfigBuilder(pi4j)
					.name("Digital input")
					.address(pinInput)
					.pull(PullResistance.PULL_DOWN)
					.debounce(3000L)
					.build();

			if(digitalInputProvider == null) {
				// get a Digital Input I/O provider from the Pi4J context
				digitalInputProvider = pi4j.provider("pigpio-digital-input");
			}
			return digitalInputProvider.create(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	// This will read the pulse input and the stop signal
	protected DigitalOutput createDigitalOutput(int pinOutput, Context pi4j) {
		try {
			DigitalOutputConfig config = DigitalOutput.newConfigBuilder(pi4j)
					.name("Digital output")
					.address(pinOutput)
					.build();

			if(digitalOutputProvider == null) {
				// get a Digital Input I/O provider from the Pi4J context
				digitalOutputProvider = pi4j.provider("pigpio-digital-output");
			}
			return digitalOutputProvider.create(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

    
    protected short byteArrayToShort(byte[] array) {
    	return ByteBuffer.wrap(array).getShort();
    }
    
    protected void writeShort(I2C vl53, int register, short s) {
    	vl53.writeRegister(register, new Short(s).byteValue());
    }
    
    protected short readShort(I2C vl53, int register) {
    	return vl53.readRegisterByteBuffer(register, 2).getShort();
    }
    
    protected String getHexString(byte value) {
		return Integer.toHexString(Byte.toUnsignedInt(value));
	}

	public abstract void exit();

}
