package test;

// JUnit's imports
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

// Project's imports
import device.ColorSensor;

// Lejos' imports
import lejos.hardware.port.SensorPort;


/**
 * Test the ColorSensor class
 * @author mschneider
 */
public class ColorSensorTest{
	
	@Test
	/**
	 * Test the color get is'nt null
	 */
	public void getColorTest() {
		ColorSensor device = new ColorSensor(SensorPort.S3);
		assertNotNull(device.getColor());
	}
}
