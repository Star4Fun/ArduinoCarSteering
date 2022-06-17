package ArduinoCar.algorithm;

import java.util.ArrayList;

import ArduinoCar.RaspberrySensors;
import ArduinoCar.MeasurementUnit.DistanceUnit;

public class Wall implements IAlgorithm {

	RaspberrySensors sensors;
	
	int current = 0;
	float[] distance = 
//		{
//			1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20
//	};
	new float[20];
	
	public Wall(RaspberrySensors sensors) {
		this.sensors = sensors;
	}
	
	@Override
	public void init() {
		moveUp();
	}

	@Override
	public void loop() {
		if(sensors != null && sensors.getHandSensor() != null)  {
			if(this.shouldSteer()) {
				float average = this.getAverageValue();
				float current = getDistance();
				if(current - average > 0) {
					//right
					System.out.println("right");
				} else {
					//left
					System.out.println("left");
				}
			} else {
				
			}
			
			if(current < distance.length-1) {
				current++;
			} else {
				moveUp();
			}
			distance[current] = getDistance();
		}
	}
	
	public static final float tolerance = 0.5f;
	
	public float getDistance() {
		return (float)sensors.getHandSensor().getDistance(DistanceUnit.CM);
	}
	
	public boolean shouldSteer() {
		float currentValue = getDistance();
		if(isInTolerance(currentValue, tolerance, getAverageValue())) {
			return true;
		}
		return false;
	}
	
	public float getAverageValue() {
		ArrayList<Float> list = new ArrayList<Float>();
		
		for(int i = 0; i < current-1; i++) {
			if(isInTolerance(distance[i], tolerance, distance[i+1])) {
				list.add(distance[i]);
			}
		}
		
		float value = 0;
		
		for(float f: list) {
			value += f;
		}
		
		return value/list.size();
	}
	
	public boolean hasGreatDeviation() {
		float currentDistance = getDistance();
		for(float f: distance) {
			if(isInTolerance(currentDistance, tolerance, f)) {
				
			}
		}
		
		return false;
	}
	
	public boolean isInTolerance(float check, float tolerance, float value) {
		if(check - tolerance < value && check + tolerance > value) {
			return true;
		}
		return false;
	}
	
	public void moveUp() {
		float[] tmp = new float[distance.length];
		for(int i = 0; i < distance.length-1; i++) {
			tmp[i] = distance[i+1];
		}
		distance = tmp;
	}

}
