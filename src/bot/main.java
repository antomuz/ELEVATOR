package bot;

// Project's imports
import communication.BotAction;
import communication.WirelessConnection;
import device.*;
import state.*;

import java.util.HashMap;

/**
 * Main class pour la gestion du robot EV3.
 * Adapté pour traiter des séquences d'actions (gamme/scénario) reçues via Bluetooth.
 *
 * Chaque chaîne d'actions est de type "5,4,8", où chaque chiffre correspond à une action.
 *
 * @author mschneider
 */
public class Main {

	private static String UNKNOWN = StateMotor.UNKNOWN.toString();
	private static String FULLFORWARD = StateMotor.FullForward.toString();
	private static String FULLBACKWARD = StateMotor.FullBackward.toString();
	private static String CLOSE = StatePliers.Close.toString();
	private static String OPEN = StatePliers.Open.toString();

	private static int rangeHeight = 650;
	private static int rangeRotation = 500;
	private static int rangePliers = 95;

	// Motors
	private static BotMotor pliersMotor = new BotMotor('A');
	private static BotMotor heightMotor = new BotMotor('B');
	private static BotMotor rotationMotor = new BotMotor('D');

	// Initialization of the motor's state
	private static StatePliers pliersMotorState = StatePliers.valueOf(UNKNOWN);
	private static StateMotor heightMotorState = StateMotor.valueOf(UNKNOWN);
	private static StateMotor rotationMotorState = StateMotor.valueOf(UNKNOWN);

	// Store the states of the motors
	private static HashMap<String, StateMotor> statesMotors = new HashMap<String, StateMotor>();
	private static HashMap<String, StatePliers> statesPliers = new HashMap<String, StatePliers>();

	/**
	 * Convertit un identifiant d'action en BotAction.
	 * @param actionId l'identifiant numérique de l'action
	 * @return la valeur correspondante de BotAction
	 */
	private static BotAction convertAction(int actionId) {
		switch (actionId) {
			case 0:
				return BotAction.valueOf(BotAction.Waiting.toString());
			case 1:
				return BotAction.valueOf(BotAction.LeftRotationMovement.toString());
			case 3:
				return BotAction.valueOf(BotAction.RigthRotationMovement.toString());
			case 4:
				return BotAction.valueOf(BotAction.StopMovement.toString());
			case 5:
				return BotAction.valueOf(BotAction.AscendingMovement.toString());
			case 7:
				return BotAction.valueOf(BotAction.DescendingMovement.toString());
			case 8:
				return BotAction.valueOf(BotAction.DisconnectAction.toString());
			default:
				System.out.println("Unknown action id: " + actionId);
				return BotAction.valueOf(BotAction.Waiting.toString());
		}
	}

	public static void main(String[] args) {

		// Set the speed of the motors (initially low)
		pliersMotor.setSpeed(100);
		heightMotor.setSpeed(100);
		rotationMotor.setSpeed(100);

		// Create the Bluetooth connection thread
		WirelessConnection bluetoothConnection = new WirelessConnection();

		// Initialize state maps
		statesMotors.put(heightMotor.getName(), heightMotorState);
		statesMotors.put(rotationMotor.getName(), rotationMotorState);
		statesPliers.put(pliersMotor.getName(), pliersMotorState);

		// Initialize the starting positions of the motors and pliers
		init(rotationMotor, heightMotor, statesMotors, pliersMotor, statesPliers);

		// On démarre la connexion Bluetooth
		bluetoothConnection.start();

		boolean appAlive = true;

		// Boucle principale de traitement des actions
		while (appAlive) {
			// Supposons que la méthode getActionChain() retourne une chaîne comme "5,4,8"
			String actionChain = bluetoothConnection.getActionChain();
			if (actionChain != null && !actionChain.isEmpty()) {
				String[] actions = actionChain.split(",");
				for (String actionStr : actions) {
					try {
						int actionId = Integer.parseInt(actionStr.trim());
						BotAction ba = convertAction(actionId);
						// Traitement de l'action
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
							case AscendingMovement:
								backwardMovement(heightMotor, statesMotors, statesPliers);
								bluetoothConnection.transmit((byte) 1);
								break;
							case DescendingMovement:
								forwardMovement(heightMotor, statesMotors, statesPliers);
								bluetoothConnection.transmit((byte) 1);
								break;
							case StopMovement:
								heightMotor.stop();
								pliersMotor.stop();
								rotationMotor.stop();
								break;
							case DisconnectAction:
								bluetoothConnection.setAppAlive(false);
								appAlive = false;
								break;
							default:
								break;
						}
						// Pause entre chaque action pour assurer leur enchaînement correct
						Thread.sleep(100);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid action code: " + actionStr);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		}

		// Fermeture de la connexion Bluetooth
		bluetoothConnection.disconnect();
	}

	private static void init(BotMotor rotationMotor, BotMotor heightMotor, HashMap<String, StateMotor> statesMotors, BotMotor pliersMotor, HashMap<String, StatePliers> pliersMotorState) {
		if (rotationMotor.getName().equals("rotationMotor")) {
			// Initialisation du moteur de rotation
			forward(rotationMotor);
			StateMotor sm = statesMotors.get(rotationMotor.getName());
			sm = StateMotor.valueOf(FULLFORWARD);
			statesMotors.put(rotationMotor.getName(), sm);

			// Initialisation du moteur de hauteur
			backward(heightMotor);
			sm = statesMotors.get(heightMotor.getName());
			sm = StateMotor.valueOf(FULLBACKWARD);
			statesMotors.put(heightMotor.getName(), sm);

			// Initialisation de la pince
			forward(pliersMotor);
			StatePliers sp =  pliersMotorState.get(pliersMotor.getName());
			sp = StatePliers.valueOf(OPEN);
			statesPliers.put(pliersMotor.getName(), sp);
		} else {
			System.out.println("Error rotationMotor: " + rotationMotor.getName());
		}
	}

	private static void backward(BotMotor motor) {
		if (motor.getName().equals("rotationMotor")) {
			motor.rotate(-rangeRotation);
		} else if (motor.getName().equals("pliersMotor")){
			motor.rotate(rangePliers);
		} else {
			motor.rotate(-rangeHeight);
		}
		motor.stop();
	}

	private static void forward(BotMotor motor) {
		if (motor.getName().equals("rotationMotor")) {
			// Exemple simplifié : remplacer la boucle infinie par un simple appel forward()
			motor.forward();
		} else if (motor.getName().equals("pliersMotor")){
			motor.rotate(-rangePliers);
		} else {
			motor.rotate(rangeHeight);
		}
		motor.stop();
	}

	public static void forwardMovement(BotMotor motor, HashMap<String, StateMotor> sms, HashMap<String, StatePliers> pliersMotorState) {
		String currentMotorName = motor.getName();
		if (!motor.getName().equals("pliersMotor")) {
			if (sms.get(currentMotorName) != StateMotor.valueOf(FULLFORWARD)) {
				forward(motor);
				System.out.println(currentMotorName + " " + sms.get(currentMotorName));
				StateMotor sm = StateMotor.valueOf(FULLFORWARD);
				sms.put(currentMotorName, sm);
			} else {
				System.out.println("Motor already in full forward position: " + motor.getName());
			}
		} else {
			if (pliersMotorState.get("pliersMotor") != StatePliers.valueOf(OPEN)) {
				forward(motor);
				System.out.println(currentMotorName + " " + sms.get(currentMotorName));
				StatePliers sp = StatePliers.valueOf(OPEN);
				pliersMotorState.put(pliersMotor.getName(), sp);
			} else {
				System.out.println("Pliers already open");
			}
		}
	}

	public static void backwardMovement(BotMotor motor, HashMap<String, StateMotor> sms, HashMap<String, StatePliers> pliersMotorState) {
		String currentMotorName = motor.getName();
		if (!motor.getName().equals("pliersMotor")) {
			if (sms.get(currentMotorName) != StateMotor.valueOf(FULLBACKWARD)) {
				backward(motor);
				StateMotor sm = StateMotor.valueOf(FULLBACKWARD);
				sms.put(currentMotorName, sm);
			} else {
				System.out.println("Motor already in full backward position: " + motor.getName());
			}
		} else {
			if (pliersMotorState.get("pliersMotor") != StatePliers.valueOf(CLOSE)) {
				backward(motor);
				StatePliers sp = StatePliers.valueOf(CLOSE);
				pliersMotorState.put(pliersMotor.getName(), sp);
			} else {
				System.out.println("Pliers already closed");
			}
		}
	}

	public StateMotor getStateMotor(BotMotor motor, HashMap<String, StateMotor> sms) {
		return sms.get(motor.getName());
	}
}
