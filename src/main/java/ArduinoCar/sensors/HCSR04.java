package ArduinoCar.sensors;

import java.util.logging.Level;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.i2c.I2C;

import ArduinoCar.Main;
import ArduinoCar.MeasurementUnit;

public class HCSR04 extends MeasurementUnit {

	static final int GPIO_TRIGGER = 18;
	static final int GPIO_ECHO = 24;

	DigitalOutput trigger;
	DigitalInput echo;
	DigitalOutput led;

	public HCSR04(Context pi4j) {
		this(pi4j, GPIO_TRIGGER, GPIO_ECHO);
	}

	public HCSR04(Context pi4j, int triggerPin, int echoPin) {
		super(pi4j);
		Main.log.getLogger().setLevel(Level.ALL);

		// create a digital output instance using the default digital output provider
		trigger = createDigitalOutput(triggerPin, pi4j);

		// create a digital output instance using the default digital output provider
		led = createDigitalOutput(23, pi4j);
		led.low();
		echo = createDigitalInput(echoPin, pi4j);
	}

	public void exit() {
		echo.shutdown(pi4j);
		trigger.shutdown(pi4j);
		led.shutdown(pi4j);
	}

	@Override
	public double getDistance(I2C vl53, DistanceUnit unit) {
		trigger.high();

		//setze Trigger nach 0.01ms aus LOW
		try {
			Thread.sleep(0, 10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//	    trigger.state(DigitalState.LOW);
		trigger.low();

		long startTime = System.nanoTime();
		long stopTime = System.nanoTime();


		//speichere Startzeit
		while(echo.isLow()) {
			startTime = System.nanoTime();
		}

		//speichere Ankunftszeit
		while(echo.isHigh()) {
			stopTime = System.nanoTime();
		}

		//Zeit Differenz zwischen Start und Ankunft
		double timeDif = (double)(stopTime - startTime);
		//mit der Schallgeschwindigkeit (34300 cm/s) multiplizieren
		//und durch 2 teilen, da hin und zurueck
		double distance = ((timeDif/1000000000d) * 34300d) / 2d;

		return Float.parseFloat(df.format(distance).replace(",", "."));
	}

	@Override
	public double getDistance(DistanceUnit unit) {
		return getDistance(null, unit);
	}

}
