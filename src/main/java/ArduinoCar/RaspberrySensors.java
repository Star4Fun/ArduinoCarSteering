package ArduinoCar;

import ArduinoCar.MeasurementUnit.DistanceUnit;
import ArduinoCar.algorithm.Wall;
import ArduinoCar.sensors.HCSR04;
import ArduinoCar.sensors.SensorThread;
import ArduinoCar.sensors.TFLuna;
import io.github.pseudoresonance.pixy2api.Pixy2;

public class RaspberrySensors {

	MeasurementUnit distanceSen;
	VL53L0X handSensor;
	
	Pixy2 pixy;

	SensorThread distanceSensor, rightHandAlgorithm;
	
	String sensor;
	
	private Main m;
	
	public RaspberrySensors(Main m) {
		this.m = m;
	}

	public void init(String sensor) {
		this.sensor = sensor.toLowerCase();
		if(sensor.equals("ultraschall")) {
			distanceSen = new HCSR04(m.getPI4J());	
		} else if(sensor.equals("luna")) {
			distanceSen = new TFLuna(m.getPI4J());
		}
		handSensor = new VL53L0X(m.getPI4J());
	}
	
	public void run() {
		//Sonic sensor thread
		distanceSensor = new SensorThread(200) {
			@Override
			public void doAction() {
				float distance = (float) distanceSen.getDistance(DistanceUnit.CM);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				float distanceWall = (float) handSensor.getDistance(DistanceUnit.CM);
				GuiConsole.getInstance().updateDistance(distance, distanceWall);
//				Main.log.log(Level.INFO, "Gemessene Entfernung = " + distance + " cm");
			}

			@Override
			public void init() {
				
			}
		};
	}
	
	public VL53L0X getHandSensor() {
		return handSensor;
	}
	
	public MeasurementUnit getDistanceSensor() {
		return distanceSen;
	}

	public void close() {
		System.out.println("fuck");
		distanceSen.exit();
		if(pixy != null) {
			pixy.close();
		}
	}

	//			new VL53L0X();
//	distance.exit(Main.getPI4J());
//	System.exit(0);
//	pixy = Pixy2.createInstance(LinkType.I2C);
//	pixy.init();
//	pixy.setLED(Color.GREEN);
//	pixy.setLamp((byte)0, (byte)0);
//	pixy.getResolution();
//	pixy.getCCC().getBlocks();
//	ArrayList<Block> blocks = pixy.getCCC().getBlockCache();
//
//	while(true) {
//		pixy.getCCC().getBlocks();
//		for(Block b: blocks) {
//			b.print();
//		}
//		try {
//			Thread.sleep(20);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}
