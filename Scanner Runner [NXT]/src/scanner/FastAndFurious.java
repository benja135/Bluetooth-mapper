/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Reçois une séquence de déplacement et l'effectue
 * en utilisant des déplacements rapides
 *
 */
public class FastAndFurious {
	
	private static ThreadStop emergencyStop = new ThreadStop();

	// Initialisation des capteurs
    private static UltrasonicSensor sonicRight = new UltrasonicSensor(SensorPort.S1);
    private static UltrasonicSensor sonicLeft = new UltrasonicSensor(SensorPort.S4);
    private static LightSensor lightLeft = new LightSensor(SensorPort.S2, true);
    private static LightSensor lightRight = new LightSensor(SensorPort.S3, true);   
    
    // Gestion des déplacements
	private static DeplacementFast deplacement = new DeplacementFast();

	// Pour la communication
	private static BTConnection btc;
	private static DataInputStream receive;
	
	private static int distanceMin = 6;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		emergencyStop.setDaemon(true);
		emergencyStop.start();
		    
		System.out.println("Wait for BT...");
		btc = Bluetooth.waitForConnection();
		receive = btc.openDataInputStream();
		System.out.println("Connected !");
		
		receiveAndDoSequence();

	}
	
	private static void receiveAndDoSequence() throws IOException, InterruptedException {
		
		System.out.println("Wait signal seq...");
		int valeur = -2;
		while (valeur != SEQUENCE) {
			try {				
				valeur = receive.readInt();
			} catch (NullPointerException e) {
				valeur = -2;
				System.out.println("Stream lecture vide");
			}		
			Thread.sleep(500);
		}

		System.out.println("Wait for sequence..");
		List<Integer> sequence = new LinkedList<Integer>();
		valeur = receive.readInt();
		while (valeur != SEQUENCE) {					
			sequence.add(valeur);
			valeur = receive.readInt();
		}
		System.out.println("Sequence reçue !");
		while (!sequence.isEmpty()) {
			switch (sequence.get(0)) {
				case MOVE_LEFT :		deplacement.fastHairpinTurnTo(LEFT);
										break;
				case MOVE_RIGHT :		deplacement.fastHairpinTurnTo(RIGHT);
										break;
				default: break;
									
			}
			sequence.remove(0);
			forwardRecoveryAndMoveToCenter();
		}
	}
	
	/**
	 * Avance jusqu'à trouver une ligne noire, corrige l'angle du robot si besoin
	 * puis avance jusqu'au milieu de la cellule
	 * Ecarte le robot du mur s'il est trop proche
	 * @throws InterruptedException
	 */
	private static void forwardRecoveryAndMoveToCenter() throws InterruptedException{

		deplacement.setTravelSpeed(120);
		deplacement.forward();
		
		// Tant qu'on a pas vu de bande noire
		while (lightLeft.getNormalizedLightValue() > lightLimit && lightRight.getNormalizedLightValue() > lightLimit) {
			Thread.yield();
		}
		deplacement.stop();
		Sound.beep();
		
		// Si un des capteurs n'est pas sur le scotch
		if (lightLeft.getNormalizedLightValue() > lightLimit || lightRight.getNormalizedLightValue() > lightLimit) {
			deplacement.fastRecoveryAngleWithLine();
		}
		
		deplacement.setTravelSpeed(340);
		deplacement.travel(245, false);
		
		// Redressement si trop proche d'un mur
		if (sonicRight.getDistance() <= distanceMin) {
			deplacement.fastRedressement(RIGHT);
		}
		if (sonicLeft.getDistance() <= distanceMin) {
			deplacement.fastRedressement(LEFT);
		}
	}
	
}
