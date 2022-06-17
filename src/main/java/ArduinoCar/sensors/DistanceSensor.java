package ArduinoCar.sensors;

import com.pi4j.context.Context;

public abstract class DistanceSensor extends ElectricComponent {

	public DistanceSensor(Context pi4j) {
		super(pi4j);
		// TODO Auto-generated constructor stub
	}
	
	public abstract float getDistance();

}
