package ArduinoCar;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;

import ArduinoCar.sensors.ElectricComponent;

public abstract class MeasurementUnit extends ElectricComponent {

	public MeasurementUnit(Context pi4j) {
		super(pi4j);
		// TODO Auto-generated constructor stub
	}
	
	public static enum DistanceUnit {
		CM,
		METER,
		INCH;
	}
	
	/**
	 * 
	 * @param value in mm
	 * @param unitTo targetUnit
	 * @return converted value
	 */
	public double convert(double value, DistanceUnit unitTo) {
		if (unitTo == DistanceUnit.CM) {
            return value / 10;
        } else if (unitTo == DistanceUnit.METER) {
            return value / 1000;
        } else if (unitTo == DistanceUnit.INCH) {
            return value / 25.4;
        } else {
            return value;
        }
	}
	
	public abstract double getDistance(DistanceUnit unit);
	
    public abstract double getDistance(I2C vl53, DistanceUnit unit);

}
