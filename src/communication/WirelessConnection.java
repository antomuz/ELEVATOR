package communication;

// Java's imports
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Lejos' imports
import lejos.hardware.Bluetooth;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;


public class WirelessConnection extends Thread {

	private static String WAITING = BotAction.Waiting.toString();
	private static String DISCONNECTACTION = BotAction.DisconnectAction.toString();
	private static String DESCENDINGMOVEMENT = BotAction.DescendingMovement.toString();
	private static String ASCENDINGMOVEMENT = BotAction.AscendingMovement.toString();
	private static String STOPMOVEMENT = BotAction.StopMovement.toString();
	private static String RIGHTROTATIONMOVEMENT = BotAction.RigthRotationMovement.toString();
	private static String LEFTROTATIONMOVEMENT = BotAction.LeftRotationMovement.toString();

	// The output stream
	private static DataOutputStream dataOut;

	// The input stream
	private static DataInputStream dataIn;

	// The bluetooth connection of EV3
	private static BTConnection BTLink;

	// True if the connection is established, false otherwise
	private static boolean appAlive;

	// Action to make
	public volatile BotAction ba = BotAction.valueOf(WAITING);

	/**
	 * run
	 * Point d'entrée du thread de connexion
	 */
	public void run() {
		connection();
		this.ba = BotAction.valueOf(WAITING);
		appAlive = true;

		while (appAlive){
			try {
				// Lecture d'une chaîne de caractères contenant potentiellement plusieurs actions
				String actionChain = dataIn.readUTF();
				// Traiter la chaîne reçue (gamme ou scénario)
				processActionChain(actionChain);

				Thread.sleep(100);
				// Réinitialiser l'action après traitement
				this.ba = BotAction.valueOf(WAITING);
			}
			catch (IOException ioe) {
				setAppAlive(false);
				disconnect();
				System.out.println("IO Exception during readUTF");
				ioe.printStackTrace();
			}
			catch (InterruptedException ie) {
				setAppAlive(false);
				disconnect();
				System.out.println("Thread connection interrupted");
				ie.printStackTrace();
			}
		}
	}

	/**
	 * connection
	 * Initialise les flux d'entrée/sortie et établit la connexion Bluetooth.
	 */
	public static void connection() {
		BTConnector connector = (BTConnector) Bluetooth.getNXTCommConnector();
		BTLink = (BTConnection) connector.waitForConnection(30000, NXTConnection.RAW);
		dataOut = BTLink.openDataOutputStream();
		dataIn = BTLink.openDataInputStream();
		System.out.println("Connection established");
	}

	/**
	 * disconnect
	 * Ferme tous les flux et la connexion Bluetooth.
	 */
	public static void disconnect() {
		try {
			dataOut.close();
			dataIn.close();
			BTLink.close();
			System.out.println("Disconnection");
		} catch (IOException e) {
			System.out.println("Error during disconnection: " + e.getStackTrace());
		}
	}

	/**
	 * ProcessActionChain
	 * Traite une chaîne de commandes séparées par des virgules.
	 * Chaque valeur est convertie en action et exécutée séquentiellement.
	 *
	 * @param actionChain La chaîne d'actions reçue (ex : "1,3,4,5")
	 */
	private void processActionChain(String actionChain) {
		// Exemple de format : "5,4,8"
		String[] actions = actionChain.split(",");
		for (String actionStr : actions) {
			try {
				int actionId = Integer.parseInt(actionStr.trim());
				setAction(actionId);
				System.out.println("Action executed: " + this.ba);
				// Optionnel : ajouter un délai entre les actions
				Thread.sleep(100);
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid action code received: " + actionStr);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	/**
	 * setAction
	 * Définit l'action courante en fonction de l'ID reçu.
	 *
	 * @param actionId Identifiant de l'action
	 */
	public void setAction(int actionId) {
		switch (actionId) {
			case 0:
				this.ba = BotAction.valueOf(WAITING);
				break;
			case 1:
				// Left movement of the rotation's motor
				this.ba = BotAction.valueOf(LEFTROTATIONMOVEMENT);
				break;
			case 3:
				// Right movement of the rotation's motor
				this.ba = BotAction.valueOf(RIGHTROTATIONMOVEMENT);
				break;
			case 4:
				// Stop movement
				this.ba = BotAction.valueOf(STOPMOVEMENT);
				break;
			case 5:
				// Ascending movement of the height's motor
				this.ba = BotAction.valueOf(ASCENDINGMOVEMENT);
				break;
			case 7:
				// Descending movement of the height's motor
				this.ba = BotAction.valueOf(DESCENDINGMOVEMENT);
				break;
			case 8:
				// Disconnection
				this.ba = BotAction.valueOf(DISCONNECTACTION);
				break;
			default:
				System.out.println("Unknown action id: " + actionId);
				this.ba = BotAction.valueOf(WAITING);
				break;
		}
	}

	/**
	 * transmit
	 * Envoie une valeur "1" à l'application Android pour indiquer que l'action est terminée.
	 *
	 * @param fini Byte à transmettre (actuellement inutilisé)
	 */
	public void transmit(byte fini) {
		byte[] b = {1,0};
		int rep = BTLink.write(b, 1);
		System.out.println("Sent " + rep);
	}

	/**
	 * getBotAction
	 * Retourne l'action courante.
	 *
	 * @return BotAction
	 */
	public BotAction getBotAction() {
		return this.ba;
	}

	/**
	 * isAppAlive
	 * Retourne l'état de la connexion.
	 *
	 * @return boolean
	 */
	public static boolean isAppAlive() {
		return appAlive;
	}

	/**
	 * setAppAlive
	 * Définit l'état de la connexion.
	 *
	 * @param appAlive boolean
	 */
	public static void setAppAlive(boolean appAlive) {
		WirelessConnection.appAlive = appAlive;
	}
}
