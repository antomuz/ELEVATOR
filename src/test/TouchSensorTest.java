package test;

// JUnit's imports
import static org.junit.Assert.assertEquals;
import org.junit.Test;

// Project's imports
import device.TouchSensor;

// Lejos' imports
import lejos.hardware.port.SensorPort;

/**
 * Test of TouchSensor class
 * @author mschneider
 */
public class TouchSensorTest {

	@Test
	/**
	 * Test if the touch sensor isn't touched
	 */
	public void isNotTouchedTest() {
		TouchSensor device = new TouchSensor(SensorPort.S1);
		assertEquals(0, device.isTouched());
	}
}
