/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Fourni des déplacements basiques pour le NXT en utilisant les moteurs A et B
 *
 */
public class Deplacement {
	
	private static final NXTRegulatedMotor moteurDroit = Motor.A;
	private static final NXTRegulatedMotor moteurGauche = Motor.B;
	
	/**
	 * Tourne "moteur" de "angle" degré à une vitesse de "vitesse"
	 * @param moteur
	 * @param vitesse
	 * @param angle
	 */
	public void myRotateTo(NXTRegulatedMotor moteur, int vitesse, int angle) {
		moteur.resetTachoCount();
		
		moteur.setSpeed(vitesse);
		moteur.rotateTo(angle);
		
		while (moteur.isMoving()) {
			Thread.yield();
		}
	}
	
	/**
	 *  Stop les moteurs de manière synchrone (et attend jusqu'à l'arrêt total)
	 */
	public void stopMotors() {
		moteurDroit.setSpeed(0);
		moteurGauche.setSpeed(0);
		
		while (moteurDroit.isMoving() || moteurGauche.isMoving()) 
		{
			Thread.yield();
		}
	}
	
	/**
	 *  Virage à 90° vers la gauche
	 * @throws Exception
	 */
	public void hairpinTurnToLeft() throws Exception {
			stopMotors();
			moteurDroit.resetTachoCount();
			moteurDroit.setSpeed(360);
			moteurGauche.setSpeed(360);
			
			moteurDroit.forward();
			moteurGauche.backward();
			
			while (moteurDroit.getTachoCount() < 169)
			{
				Thread.yield();
			}
			stopMotors();
		
	}
	
	/**
	 *  Virage à 90° vers la droite
	 * @throws Exception
	 */
	public void hairpinTurnToRight() throws Exception {
		stopMotors();
		moteurGauche.resetTachoCount();
		moteurDroit.setSpeed(360);
		moteurGauche.setSpeed(360);
		
		moteurDroit.backward();
		moteurGauche.forward();
		
		while (moteurGauche.getTachoCount() < 169)
		{
			Thread.yield();
		}
		stopMotors();
	}
	
	/**
	 *  Petit virage vers la gauche
	 * @throws Exception
	 */
	public void turnToLeft() throws Exception {	
		stopMotors();
		myRotateTo(moteurDroit, 120, 50);
		stopMotors();
	}
	
	/**
	 *  Petit virage vers la droite
	 * @throws Exception
	 */
	public void turnToRight() throws Exception {
		stopMotors();
		myRotateTo(moteurGauche, 120, 50);
		stopMotors();
	}
	
	/**
	 * Demi tour complet
	 * @throws Exception
	 */
	public void backTurn() throws Exception {
		stopMotors();
		moteurGauche.resetTachoCount();
		
		moteurGauche.setSpeed(360);
		moteurDroit.setSpeed(360);
		
		moteurGauche.forward();
		moteurDroit.backward();
		//crier();
		
		while (moteurGauche.getTachoCount() < 355)
		{
			Thread.yield();
		}
		stopMotors();
		
	}
	
	/**
	 *  Met en marche les 2 moteurs en avant
	 * @param vitesse
	 */
	public void avancer(int vitesse) {
	    moteurDroit.setSpeed(vitesse);
		moteurGauche.setSpeed(vitesse);
		moteurDroit.forward();
		moteurGauche.forward();
	}
	
	/**
	 *  Met en marche les 2 moteurs en arriere
	 * @param vitesse
	 */
	public void reculer(int vitesse) {
	    moteurDroit.setSpeed(vitesse);
		moteurGauche.setSpeed(vitesse);
		moteurDroit.backward();
		moteurGauche.backward();
	}
	
	
	/**
	 * Eloigne le robot du mur sur son coté,
	 * la direction du robot n'est pas changée
	 * @param cote
	 * @throws InterruptedException
	 */
	public void redressement(int cote) throws InterruptedException {
		
		if (cote == 0) { // Gauche
			myRotateTo(moteurGauche, 150, -180);
			myRotateTo(moteurDroit, 150, -180);
		} else { // Droite
			myRotateTo(moteurDroit, 150, -180);
			myRotateTo(moteurGauche, 150, -180);
		}
		
		avancer(360);
		Thread.sleep(400);
		stopMotors();
	}
	
}
