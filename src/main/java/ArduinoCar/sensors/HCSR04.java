package ArduinoCar.sensors;

import java.util.HashMap;
import java.util.logging.Level;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.PullResistance;

import ArduinoCar.Main;

public class HCSR04 {

	final static int GPIO_TRIGGER = 18;
	final static int GPIO_ECHO = 24;
	
	DigitalOutput trigger;
	DigitalInput echo;
	
	DigitalInputProvider digitalInputProvider;
	DigitalOutputProvider digitalOutputProvider;
	
	// This will read the pulse input and the stop signal
	private DigitalInput createDigitalInput(int pinInput, Context pi4j) {
		try {
			DigitalInputConfig config = DigitalInput.newConfigBuilder(pi4j)
//					.id("Digial input")
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
	private DigitalOutput createDigitalOutput(int pinOutput, Context pi4j) {
		try {
			DigitalOutputConfig config = DigitalOutput.newConfigBuilder(pi4j)
//					.id("Digial output")
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
	
	public HCSR04() {
//		  configure default lolling level, accept a log level as the fist program argument
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");
		// An auto context includes AUTO-DETECT BINDINGS enabled
		// which will load all detected Pi4J extension libraries
		// (Platforms and Providers) in the class path
		Context pi4j = Pi4J.newAutoContext();
		Main.log.getLogger().setLevel(Level.ALL);
//		DigitalOutputConfigBuilder ledConfig = DigitalOutput.newConfigBuilder(pi4j)
//                .id("led")
//                .name("LED Flasher")
//                .address(GPIO_TRIGGER)
//                .shutdown(DigitalState.LOW)
//                .initial(DigitalState.LOW)
//                .provider(PiGpioDigitalOutputProvider.NAME);
		
		// create a digital output instance using the default digital output provider
//		trigger = pi4j.dout().create(ledConfig);
		trigger = createDigitalOutput(GPIO_TRIGGER, pi4j);

//		pi4j = Pi4J.newAutoContext();
		
//		DigitalOutputConfigBuilder ledConfig2 = DigitalOutput.newConfigBuilder(pi4j)
//                .id("led2")
//                .name("LED Flasher2")
//                .address(23)
//                .shutdown(DigitalState.LOW)
//                .initial(DigitalState.LOW)
//                .provider(PiGpioDigitalOutputProvider.ID);
		
		// create a digital output instance using the default digital output provider
//		DigitalOutput led = pi4j.dout().create(ledConfig2);
		DigitalOutput led = createDigitalOutput(23, pi4j);
		led.toggle();

//		pi4j = Pi4J.newAutoContext();

//		DigitalInputConfigBuilder config = DigitalInput.newConfigBuilder(pi4j).provider("pigpio-digital-input").id("pin-"+GPIO_ECHO).address(GPIO_ECHO).pull(PullResistance.PULL_DOWN);
//				echo = pi4j.create(config);
//
//				echo.addListener(e -> {
//				    if (e.state() == DigitalState.HIGH) {
//				        System.out.println("Button is pressed");
//				    }
//				});		
		echo = createDigitalInput(GPIO_ECHO, pi4j);
		
//		echo = pi4j.din().create(config);
		System.out.println(echo.state() + " " + echo.getAddress());
//		echo.addListener(System.out::println);
//		trigger.addListener(System.out::println);

//		// shutdown Pi4J
//		pi4j.shutdown();
		
		 
//		#GPIO Modus (BOARD / BCM)
		System.out.println(led.state());
		
//		GPIO.setmode(GPIO.BCM)
//		try {
//			Scanner s = new Scanner(System.in);
//			s.next();
//			s.close();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		for(int i = 0; i < 10; i++) {
			double distance = getDistance();
			System.out.println("Gemessene Entfernung = " + distance + " cm");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void exit() {
		
	}
	
	public double getDistance() {
//	    trigger.state(DigitalState.HIGH);
	    trigger.high();
		 
		//setze Trigger nach 0.01ms aus LOW
	    try {
			Thread.sleep(0, 10000);
//			Thread.sleep(1000, 0);
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
		double distance = ((timeDif/1000000000d) * 34300f) / 2f;
		 
	    return distance;
	}

}
