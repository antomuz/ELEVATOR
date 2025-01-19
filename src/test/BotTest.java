package test;

// JUnit's imports
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

// Project's iports
import bot.main;
import device.BotMotor;
import state.StateMotor;

// Java's imports
import java.util.HashMap;

// Lejos' imports
import lejos.utility.Delay;

/**
 * Test the bot's motors
 * @author mschneider
 *
 */
public class BotTest {

	private main mainTest;

	// Motor who control the Pliers
	//private BotMotor moteurA;

	// Motor who control the arm height of the bot
	private BotMotor motorB;

	// Motor who control the arm rotation of the bot
	private BotMotor motorD;

	private HashMap<String, StateMotor> statesMotors = new HashMap<String, StateMotor>();

	@Before
	/**
	 * Initialize the bot's motors
	 */
	public void setup() {
		this.mainTest = new main();
		//this.moteurA = new BotMotor('A');
		this.motorB = new BotMotor('B');
		this.motorD = new BotMotor('D');
	}

	/*
	// Tests sur le moteur A
	@Test
	public void testMoteurAOuvert() {
		this.mainTest.forward_Bras(moteurA, etatMoteurs, null);
		Delay.msDelay(6000);
		assertEquals(this.mainTest.getStateMoteurA(moteurA, null), "Ouvert");
	}

	@Test
	public void testMoteurAFerme() {
		this.mainTest.backward_Bras(moteurA, etatMoteurs, null);
		Delay.msDelay(6000);
		assertEquals(this.mainTest.getStateMoteurA(moteurA, null), "Ferme");
	}
	 */
	// Tests sur le moteur B

	@Test
	/**
	 * Test the ascension of the arm
	 */
	public void armAscensionTest() {
		main.forwardMovement(motorB, statesMotors, null);
		Delay.msDelay(6000);
		// True if the state of the motor is "FullForward"
		assertEquals(this.mainTest.getStateMotor(motorB, null), "FullForward");
	}

	@Test
	/**
	 * Test the descent of the arm
	 */
	public void armDescentTest() {
		main.backwardMovement(motorB, statesMotors, null);
		Delay.msDelay(6000);
		// True if the state of the motor is "FullBackward"
		assertEquals(this.mainTest.getStateMotor(motorB, null), "FullBackward");
	}

	@Test
	/**
	 * Test the arm rotation
	 */
	public void rotationRigthTest() {
		main.forwardMovement(motorD, statesMotors, null);
		Delay.msDelay(6000);
		// True if the state of the motor is "FullForward"
		assertEquals(this.mainTest.getStateMotor(motorD, null), "FullForward");
	}

	@Test
	/**
	 * Test the arm rotation
	 */
	public void rotationLeftTest() {
		main.backwardMovement(motorD, statesMotors, null);
		Delay.msDelay(6000);
		// True if the state of the motor is "FullBackward"
		assertEquals(this.mainTest.getStateMotor(motorD, null), "FullBackward");
	}




}
