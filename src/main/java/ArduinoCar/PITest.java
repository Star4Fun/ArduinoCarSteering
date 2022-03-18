package ArduinoCar;

import java.util.concurrent.TimeUnit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

public class PITest {

	public PITest() {
		Context pi4j = Pi4J.newAutoContext();
		// create a digital output instance using the default digital output provider
//		DigitalOutput output = pi4j.dout().create(17);
//		output.config().shutdownState(DigitalState.HIGH);
		
		DigitalOutputConfig ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(17)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output").build();
        DigitalOutput led = pi4j.create(ledConfig);


		// setup a digital output listener to listen for any state changes on the digital output
        led.addListener(System.out::println);

		// lets invoke some changes on the digital output
        led.state(DigitalState.HIGH)
		          .state(DigitalState.LOW)
		          .state(DigitalState.HIGH)
		          .state(DigitalState.LOW);

		// lets toggle the digital output state a few times
        led.toggle()
		          .toggle()
		          .toggle();

		// another friendly method of setting output state
        led.high()
		          .low();

		// lets read the digital output state
		System.out.print("CURRENT DIGITAL OUTPUT [" + led + "] STATE IS [");
		System.out.println(led.state() + "]");

		// pulse to HIGH state for 3 seconds
		System.out.println("PULSING OUTPUT STATE TO HIGH FOR 3 SECONDS");
		led.pulse(3, TimeUnit.SECONDS, DigitalState.HIGH);
		System.out.println("PULSING OUTPUT STATE COMPLETE");

		// shutdown Pi4J
		pi4j.shutdown();
	}

}
