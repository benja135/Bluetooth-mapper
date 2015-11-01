/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
 * Cartographie le labyrinthe
 *
 */
public class Scanner {
	
	private static ThreadStop emergencyStop = new ThreadStop();

	// Initialisation des capteurs
    private static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    private static LightSensor lightLeft = new LightSensor(SensorPort.S2, true);
    private static LightSensor lightRight = new LightSensor(SensorPort.S3, true);
    
    // Gestion des déplacements
	private static DeplacementOp deplacement = new DeplacementOp();

	// Pour la communication
	private static BTConnection btc;
	private static DataInputStream receive;
	private static DataOutputStream send;
	
	//private static File son = new File("cri_agonie.wav");
	private static  boolean termine = false; 		// Cartographie terminée ?
	private static  boolean ligneArrive = false; 	// Est-ce qu'on a trouvé la ligne d'arrive ?
	
    
	public static void main(String[] args) throws Exception {
		
	    emergencyStop.setDaemon(true);
	    emergencyStop.start();
	    
		System.out.println("Wait for BT...");
		btc = Bluetooth.waitForConnection();
		receive = btc.openDataInputStream();
		send = btc.openDataOutputStream();
		System.out.println("Connected !");

	    int [] resultScan = new int[4];
	    
		while (!termine)
		{		
			forwardRecoveryAndMoveToCenter();
			
			/* Si on a pas trouvé la ligne d'arrivé et qu'on est ici, 
			 * c'est qu'on est au milieu d'une case inconnue, on va
			 * donc la scanner et envoyer les résulats au PC
			 */
			if (!ligneArrive) 
			{
				// On initialise le tableau
				resultScan[0]=5;
				for(int i=1;i<4;i++){
					resultScan[i] = 0;
				}
				// Scan
				deplacement.headTurnTo(FACE);
				if (sonic.getDistance() < distanceFace) {
					resultScan[1] = 1;
				}
				deplacement.headTurnTo(LEFT);
				if (sonic.getDistance() < distanceGauche) {
					resultScan[2] = 1;
				}
				deplacement.headTurnTo(RIGHT);
				if (sonic.getDistance() < distanceDroit) {
					resultScan[3] = 1;
				}
				// On envoie le message
				for (int val : resultScan) {
					send.writeInt(val);
					send.flush();
					System.out.println("Send Ok");
				}
				
				// Attente de l'indication par le pc	
				System.out.println("Wait indication..");	
				int valeur = receive.readInt();
				switch (valeur) {
					case MOVE_LEFT :		deplacement.hairpinTurnTo(LEFT);
											break;
					case MOVE_RIGHT :		deplacement.hairpinTurnTo(RIGHT);
											break;
					case MOVE_BACKWARD :	deplacement.backTurn();
											break;
					case SEQUENCE : 		receiveAndDoSequence();
											break;
						
					default: break;
				}
			} 
			else 
			{	
				send.writeInt(NXT_FIN_DU_GAME);	// Envoie du sinal de "ligne d'arrivé trouvé"
				send.flush();
				int valeur = receive.readInt();
				if (valeur == STOP) { 			// Arrêt total
					System.exit(0);
				}
				else if (valeur == SEQUENCE) 	// Récupération séquence case manquante
				{ 
					receiveAndDoSequence();
					ligneArrive = false;
				}
			} 
		
		}//while
		
	}
	
	/**
	 * Avance jusqu'à trouver une ligne noire, corrige l'angle du robot si besoin
	 * puis avance jusqu'au milieu de la cellule. Si une autre bande noire est trouvé,
	 * alors ligneArrive passe à true.
	 * Ecarte le robot du mur s'il est trop proche
	 * @throws InterruptedException
	 */
	private static void forwardRecoveryAndMoveToCenter() throws InterruptedException{

		deplacement.setTravelSpeed(100);
		deplacement.forward();
		// Tant qu'on a pas vu de bande noire
		while (lightLeft.getNormalizedLightValue() > lightLimit && lightRight.getNormalizedLightValue() > lightLimit) {
			Thread.yield();
		}
		deplacement.stop();
		Sound.beep();
		
		// Si un des capteurs n'est pas sur le scotch
		if (lightLeft.getNormalizedLightValue() > lightLimit || lightRight.getNormalizedLightValue() > lightLimit) {
			deplacement.recoveryAngleWithLine();
		}
		
		deplacement.forward();
		
		// Si on est là c'est qu'on est sur le scotch // Tant qu'on est sur le scotch
		while (lightLeft.getNormalizedLightValue() < lightLimit || lightRight.getNormalizedLightValue() < lightLimit) {
			Thread.yield();
		}
				
		deplacement.travel(225, true);
		while(deplacement.isMoving()){
			// Si on trouve une bande noir ici, c'est que c'est l'arrivé
			if (lightLeft.getNormalizedLightValue() < lightLimit && lightRight.getNormalizedLightValue() < lightLimit) {
				Sound.buzz();
				ligneArrive = true;
			}
		}
		
		// Redressement si trop proche d'un mur
		deplacement.headTurnTo(RIGHT);
		if (sonic.getDistance() <= distanceMinDroite) {
			deplacement.redressement(RIGHT);
		}
		deplacement.headTurnTo(LEFT);
		if (sonic.getDistance() <= distanceMinGauche) {
			deplacement.redressement(LEFT);
		}
	}
	
	private static void receiveAndDoSequence() throws IOException, InterruptedException {
		
		System.out.println("Wait for sequence..");
		List<Integer> sequence = new LinkedList<Integer>();
		int valeur = receive.readInt();
		while (valeur != SEQUENCE) {						
			sequence.add(valeur);
			valeur = receive.readInt();
		}

		while (!sequence.isEmpty()) {
			switch (sequence.get(0)) {
				case MOVE_LEFT :		deplacement.hairpinTurnTo(LEFT);
										break;
				case MOVE_RIGHT :		deplacement.hairpinTurnTo(RIGHT);
										break;
				case MOVE_BACKWARD :	deplacement.backTurn();
										break;
				default: break;
									
			}
			sequence.remove(0);
			if (!sequence.isEmpty()) {
				forwardRecoveryAndMoveToCenter();
			}
		}
		sequence.clear();
	}
	
}
