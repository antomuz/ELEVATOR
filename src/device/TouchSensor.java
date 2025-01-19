package device;

// Lejos' imports
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor {

	private EV3TouchSensor device;

	/**
	 * Constructor
	 * Initialize the sensor
	 * @param port
	 */
	public TouchSensor(Port port) {
		this.device = new EV3TouchSensor(port);
	}
	
	/**
	 * isTouched
	 * Used to managed the bot's arm
	 * @return true if the sensor is pressed
	 * 			false else
	 */
	public boolean isTouched() {
		
		// Real table, 1 corresponding a pression on the sensor
		float[] sample = new float[device.sampleSize()];
		
		device.fetchSample(sample, 0);

		float etat = sample[0];
		
		if (etat == 1) {
			return true;
		} else {
			return false;
		}
	}
}
