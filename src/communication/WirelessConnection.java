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

/**
 * WirelessConnection
 * Thread who enabled the bluetooth connection
 * @author mschneider
 */
public class WirelessConnection extends Thread {
	
	private static String WAITING = BotAction.Waiting.toString(); 
	private static String DISCONNECTACTION = BotAction.DisconnectAction.toString(); 
	private static String STOPPLIERSMOVEMENT = BotAction.StopPliersMovement.toString();
	private static String STOPHEIGHTMOVEMENT = BotAction.StopHeightMovement.toString(); 
	private static String DESCENDINGMOVEMENT = BotAction.DescendingMovement.toString(); 
	private static String ASCENDINGMOVEMENT = BotAction.AscendingMovement.toString(); 
	private static String STOPROTATIONMOVEMENT = BotAction.StopRotationMovement.toString(); 
	private static String RIGHTROTATIONMOVEMENT = BotAction.RigthRotationMovement.toString(); 
	private static String LEFTROTATIONMOVEMENT = BotAction.LeftRotationMovement.toString(); 

	// The output stream
	private static DataOutputStream dataOut; 

	// The input stream
	private static DataInputStream dataIn;

	// The bluetooth connection of EV3
	private static BTConnection BTLink;

	// True of the connection is established, false else
	private static boolean appAlive;

	// Action to make
	public volatile BotAction ba = BotAction.valueOf(WAITING);

	/**
	 * run
	 * Start of the thread connection
	 */
	public void run() {

		connection();
		this.ba = BotAction.valueOf(WAITING);
		appAlive = true;

		while (appAlive){
			try {

				this.setAction((int) dataIn.readByte());

				Thread.sleep(100);
				System.out.println("Order received: " + this.ba);
				this.ba = BotAction.valueOf(WAITING);
			}
			catch (IOException ioe) {
				setAppAlive(false);
				disconnect();
				System.out.println("IO Exception readInt");
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
	 * Initialize output and input streams
	 * Create the Bluetooth connection
	 */
	public static void connection()
	{
		BTConnector connector = (BTConnector) Bluetooth.getNXTCommConnector();
		BTLink = (BTConnection) connector.waitForConnection(30000, NXTConnection.RAW);
		dataOut = BTLink.openDataOutputStream();
		dataIn = BTLink.openDataInputStream();
		System.out.println("Connection etablished");
	}


	/**
	 * disconnect
	 * close all stream of the thread to close the bluetooth connection
	 */
	public static void disconnect() {
		try {
			dataOut.close();
			dataIn.close();
			BTLink.close();
			System.out.println("Disconnection");
		}
		catch (IOException e) {
			System.out.println("Error when disconnection : "+ e.getStackTrace());
		}
	}

	/**
	 * set the action
	 * @param actionId
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
			// Stop the movement of the rotation's motor
			this.ba = BotAction.valueOf(STOPROTATIONMOVEMENT);
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
			// Stop the movement of the height's motor
			this.ba = BotAction.valueOf(STOPHEIGHTMOVEMENT);
			break;
		/*case 9:
			// Open the pliers
			this.ba = BotAction.valueOf(OPENINGMOVEMENT);
			break;
		case 11:
			// Close the pliers
			this.ba = BotAction.valueOf(CLOSINGMOVEMENT);
			break;*/
		case 12: 
			// Stop the movement of the pliers
			this.ba = BotAction.valueOf(STOPPLIERSMOVEMENT);
			break;
		case 13: 
			// Disconnection
			this.ba = BotAction.valueOf(DISCONNECTACTION);
			break;
		}
	}
	
	/**
	 * send a value "1" to the android app
	 * to say the action is done
	 * @param fini : Byte
	 */
	public void transmit(byte fini) {
		byte[] b = {1,0};
		int rep = BTLink.write(b, 1);
		//dataOut.writeByte(fini);
		System.out.println("Envoy� " + rep);
	}
	
	/**
	 * @return the value of appAlive
	 */
	public BotAction getBotAction() {
		return this.ba;
	}

	/**
	 * @return the value of appAlive
	 */
	public static boolean isAppAlive() {
		return appAlive;
	}

	/**
	 * Set the value of appAlive
	 * @param appAlive
	 */
	public static void setAppAlive(boolean appAlive) {
		WirelessConnection.appAlive = appAlive;
	}
}
