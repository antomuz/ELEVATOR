package device;

// LeJos' imports
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * ColorSensor
 * Manage the color captor of the bot
 * @author mschneider
 */
public class ColorSensor {

	private EV3ColorSensor device;
	
	/**
	 * Initialization of the sensor
	 * @param port
	 */
	public ColorSensor(Port port) {
		this.device = new EV3ColorSensor(port);
	}
	
	/**
	 * Get the color id percieved by sensor
	 * @return id : int
	 */
	public int getColor() {
		return this.device.getColorID();
	}
	
}
