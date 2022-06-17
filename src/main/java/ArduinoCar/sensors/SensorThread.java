package ArduinoCar.sensors;

import ArduinoCar.Main;

public abstract class SensorThread implements Runnable {

	boolean action = true;
	
	Thread t;
	
	int delay;
	
	/**
	 * 
	 * @param delay in ms
	 */
	public SensorThread(int delay) {
		this.delay = delay;
		this.init();
		t = new Thread(this);
		t.start();
	}
	
	public Thread getThread() {
		return t;
	}

	public void toggleAction() {
		action = !action;
	}
	
	public void stopAction() {
		action = false;
	}
	
	public void continueAction() {
		action = true;
	}
	
	@Override
	public void run() {
		while(Main.isAlive()) {
			if(action) {
				this.doAction();
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void init();
	
	public abstract void doAction();
	
}
