package device;

// Lejos' imports
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;


/**
 * Manage a motor
 */
public class BotMotor {

	NXTRegulatedMotor lejosMotor;

	// The motor name
	String name;

	/**
	 * Constructor
	 * Initialize the port and the name of the motor
	 * @param port :char
	 */
	public BotMotor(char port) {
		switch (port) {
			case 'A':
				this.lejosMotor = Motor.A;
				this.name = "heightMotor2";
				break;
			case 'B':
				this.lejosMotor = Motor.B;
				this.name = "heightMotor1";
				break;
			case 'D':
				this.lejosMotor = Motor.D;
				this.name = "rotationMotor";
				break;
		}
	}

	/**
	 * setVitesse
	 * Set the motor movement speed 
	 * @param speed : int
	 */
	public void setSpeed(int speed) {
		this.lejosMotor.setSpeed(speed);
	}

	/**
	 * getName
	 * Rotate the motor to the angle given
	 * @param angle : int
	 */
	public void rotate(int angle) {
		this.lejosMotor.rotateTo(angle,false);
	}

	/**
	 * forward
	 * Start the motor in forward movement
	 */
	public void forward() {
		this.lejosMotor.forward();
	}

	/**
	 * backward
	 * Start the motor in backward movement
	 */
	public void backward() {
		this.lejosMotor.backward();
	}

	/**
	 * getName
	 * Get the name of the motor
	 * @return name : string
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * inAbutment
	 * @return true is the motor is in abutment
	 * 			false else
	 */
	public Boolean inAbutment() {
		return this.lejosMotor.isStalled();
	}

	/**
	 * stop
	 * Stop the motor movement
	 */
	public void stop() {
		this.lejosMotor.stop();
	}
}

