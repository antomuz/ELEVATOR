package bot;


// Project's imports
import communication.BotAction;
import communication.WirelessConnection;
import device.*;
import state.*;

// Lejos' imports
import lejos.hardware.port.SensorPort;

// Java's imports
import java.util.HashMap;

/**
 * Manage the bot
 * @author mschneider
 */
public class main {
	
	private static String UNKNOWN = StateMotor.UNKNOWN.toString();
	private static String FULLFORWARD = StateMotor.FullForward.toString();
	private static String FULLBACKWARD = StateMotor.FullBackward.toString();
	private static String CLOSE = StatePliers.Close.toString();
	private static String OPEN = StatePliers.Open.toString();
	
	private static int rangeHeight = 650;
	private static int rangeRotation = 500;
	private static int rangePliers = 95;
	
	// Motor who control the pliers
	private static BotMotor pliersMotor = new BotMotor('A');

	// Motor who control the arm height
	private static BotMotor heightMotor = new BotMotor('B');

	// Motor who control the arm rotation
	private static BotMotor rotationMotor = new BotMotor('D');

	// Touch sensor
	// Color sensor
	//private static ColorSensor colorSensor = new ColorSensor(SensorPort.S3);

	// Initialization of the motor's state
	private static StatePliers pliersMotorState = StatePliers.valueOf(UNKNOWN);
	private static StateMotor heightMotorState = StateMotor.valueOf(UNKNOWN);
	private static StateMotor rotationMotorState = StateMotor.valueOf(UNKNOWN);
	
	// Store the states of the motors
	private static HashMap<String, StateMotor> statesMotors = new HashMap<String, StateMotor>();
	private static HashMap<String, StatePliers> statesPliers = new HashMap<String, StatePliers>();

	@SuppressWarnings("static-access")
	/**
	 * Main method of the bot
	 * @param args
	 */
	public static void main(String[] args) {

		// Set the speed of the motors
		// (too fast at the start)
		pliersMotor.setSpeed(100);
		heightMotor.setSpeed(100);
		rotationMotor.setSpeed(100);

		// Create the connection thread
		WirelessConnection bluetoothConnection = new WirelessConnection();

		// Set the statesMotors hashmap (motor's name, motor's state)
		statesMotors.put(heightMotor.getName(), heightMotorState);
		statesMotors.put(rotationMotor.getName(), rotationMotorState);

		statesPliers.put(pliersMotor.getName(), pliersMotorState);
		
		// Initialize the start position of the pliers and arm 
		init(rotationMotor, heightMotor, statesMotors, pliersMotor, statesPliers);
		allMotor(pliersMotor, heightMotor, rotationMotor);
		bluetoothConnection.start();
		
		boolean appAlive = true;
		
		while (appAlive) {
			BotAction ba = bluetoothConnection.getBotAction();

			switch (ba) {
			case LeftRotationMovement: 
				backwardMovement(rotationMotor, statesMotors, statesPliers);
				forwardMovement(pliersMotor, statesMotors, statesPliers);
				bluetoothConnection.transmit((byte) 1);
				break;
			case RigthRotationMovement: 
				forwardMovement(rotationMotor, statesMotors, statesPliers);
				backwardMovement(pliersMotor, statesMotors, statesPliers);
				bluetoothConnection.transmit((byte) 1);
				break;	
			case StopRotationMovement: 
				rotationMotor.stop();
				break;
			case AscendingMovement: 
				backwardMovement(heightMotor, statesMotors, statesPliers);
				bluetoothConnection.transmit((byte) 1);
				break;
			case DescendingMovement: 
				forwardMovement(heightMotor, statesMotors, statesPliers);
				bluetoothConnection.transmit((byte) 1);
				break;
			case StopHeightMovement:
				heightMotor.stop();
				break;
			case StopPliersMovement: 
				pliersMotor.stop();
				break;
			case DisconnectAction: 
				bluetoothConnection.setAppAlive(false);
				appAlive =  false;
				break;
			default:
				break;
			}
		}

		// Close the bluetooth connection
		bluetoothConnection.disconnect();
	}

	/**
	 * Initialize the start position of the bot 
	 * @param rotationMotor
	 * @param statesMotors
	 * @param pliersMotor
	 * @param pliersMotorState
	 */
	private static void init(BotMotor rotationMotor, BotMotor heightMotor, HashMap<String, StateMotor> statesMotors, BotMotor pliersMotor,  HashMap<String, StatePliers> pliersMotorState) {

		if (rotationMotor.getName() == "rotationMotor") {
			// Initialize the rotation's motor
			forward(rotationMotor);
			StateMotor sm = statesMotors.get(rotationMotor.getName());
			sm = StateMotor.valueOf(FULLFORWARD);
			//sm.replace(rotationMotor.getName(), sm);
			statesMotors.remove(rotationMotor.getName());
			statesMotors.put(rotationMotor.getName(), sm);

			// Initialize the heightMotor's motor
			backward(heightMotor);
			sm = statesMotors.get(heightMotor.getName());
			sm = StateMotor.valueOf(FULLBACKWARD);
			//sm.replace(rotationMotor.getName(), sm);
			statesMotors.remove(heightMotor.getName());
			statesMotors.put(heightMotor.getName(), sm);
						
			// Initialize the pliers position
			forward(pliersMotor);
			StatePliers sp =  pliersMotorState.get(pliersMotor.getName());
			sp = StatePliers.valueOf(OPEN);
			pliersMotorState.remove(pliersMotor.getName());
			pliersMotorState.put(pliersMotor.getName(), sp);
		} else {
			System.out.println("Error rotationMotor:" + rotationMotor.getName());
		}
	}

	/**
	 * Check if the backward movement can be executed
	 * Make it if it's possible
	 * @param motor
	 */
	private static void backward(BotMotor motor) {
		if (motor.getName() == "rotationMotor") {
			motor.rotate(-rangeRotation);
		} else if (motor.getName() == "pliersMotor"){
			motor.rotate(rangePliers);
		}else {
			motor.rotate(-rangeHeight);
		}
		motor.stop();
	}

	/**
	 * Check if the forward movement can be executed
	 * Make it if it's possible
	 * @param motor
	 */
	private static void forward(BotMotor motor) {
		if (motor.getName() == "rotationMotor") {
			while (true) {
				motor.forward();
			}
		} else if (motor.getName() == "pliersMotor"){
				motor.rotate(-rangePliers);
		} else {
			motor.rotate(rangeHeight);
		}
		motor.stop();
	}

	private static void allMotor(BotMotor motor, BotMotor motor2, BotMotor motor3) {
		while (true) {
			motor.forward();
			motor2.forward();
			motor3.forward();
		}

	}
	
	/**
	 * Open the pliers, ascend the arm or rotate to the rigth the arm
	 * if possible
	 * @param motor
	 * @param sms
	 * @param sp
	 */
	public static void forwardMovement(BotMotor motor, HashMap<String,StateMotor> sms, HashMap<String, StatePliers> pliersMotorState) {

		String currentMotorName = motor.getName();

		if (motor.getName() != "pliersMotor") {		
			// Rotation's motor case and height's motor case 

			// If the state of the current motor isn't "FullForward"
			if (sms.get(currentMotorName) != (StateMotor.valueOf(FULLFORWARD))) {
				forward(motor);
				System.out.println(currentMotorName + " " + sms.get(currentMotorName) );
				StateMotor sm = sms.get(currentMotorName) ;
				sm = StateMotor.valueOf(FULLFORWARD);
				sms.remove(currentMotorName);
				sms.put(currentMotorName, sm);
			} else {
				System.out.println("Motor already in full forward position: "+ motor.getName());
			}

		} else {
			// Pliers's motor case

			// If the pliers isn't in "Open" statement
			if (pliersMotorState.get("pliersMotor") != StatePliers.valueOf(OPEN)) {
				forward(motor);

				System.out.println(currentMotorName + " " + sms.get(currentMotorName) );
				StatePliers sp =  pliersMotorState.get(pliersMotor.getName());
				sp = StatePliers.valueOf(OPEN);
				pliersMotorState.remove(pliersMotor.getName());
				pliersMotorState.put(pliersMotor.getName(), sp);
			} else {
				System.out.println("Pliers already open");
			}
		}
	}

	/**
	 * Close the pliers, descend the arm or rotate to the left the arm
	 * if possible
	 * @param motor
	 * @param sms
	 * @param sp
	 */
	public static void backwardMovement(BotMotor motor, HashMap<String,StateMotor> sms, HashMap<String, StatePliers> pliersMotorState) {

		String currentMotorName = motor.getName();

		if (motor.getName() != "pliersMotor")  {
			// Rotation's motor case and height's motor case

			// If the state of the current motor isn't "FullBackward"
			if (sms.get(currentMotorName) != StateMotor.valueOf(FULLBACKWARD)) {
				backward(motor); 
				StateMotor sm = sms.get(currentMotorName);
				sm = StateMotor.valueOf(FULLBACKWARD);
				sms.remove(currentMotorName);
				sms.put(currentMotorName, sm);
			} else {
				System.out.println("Motor already in full bakward position: "+  motor.getName());
			}
		} else {
			// Pliers motor case

			// If the pliers isn't in "Close" statement
			if (pliersMotorState.get("pliersMotor") != StatePliers.valueOf(CLOSE)) {
				backward(motor);
				StatePliers sp =  pliersMotorState.get(pliersMotor.getName());
				sp = StatePliers.valueOf(CLOSE);
				pliersMotorState.remove(pliersMotor.getName());
				pliersMotorState.put(pliersMotor.getName(), sp);
			} else {
				System.out.println("Pliers already close" );
			}
		}
	}


	/**
	 * Get the state of the given motor
	 * @param motor
	 * @param sms
	 * @return
	 */
	public StateMotor getStateMotor(BotMotor motor, HashMap<String, StateMotor> sms) {
		return sms.get(motor.getName());
	}


}