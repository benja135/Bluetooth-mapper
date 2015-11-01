/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Fourni des déplacement optimisé en prenant en compte le diamétre
 * et l'écartement des roues (respectivement 56 et 120) ainsi que 
 * des méthodes de redressements
 *
 */
public class DeplacementOp extends DifferentialPilot {
	
	public DeplacementOp() {	
		//super(wheelDiameter, trackWidth, leftMotor, rightMotor);
		super(56, 120, Motor.B, Motor.A);	
	}

	/**
	 * Virage en épingle
	 * @param cote
	 */
	public void hairpinTurnTo(int cote) {
		setTravelSpeed(150);
		if (cote == LEFT) {
			arc(0,90);
		} else if (cote == RIGHT) {
			arc(0,-90);
		}
	}
	
	/**
	 * Demi tour
	 */
	public void backTurn() {
		setTravelSpeed(150);
		arc(0,-180);
	}
	
	/**
	 * Corrige l'angle du robot grâce à la bande noire
	 * Cette méthode doit être appelée juste après avoir détecté la bande
	 */
	public void recoveryAngleWithLine() {
		int it = 0;
	    LightSensor lightLeft = new LightSensor(SensorPort.S2, true);
	    LightSensor lightRight = new LightSensor(SensorPort.S3, true);
		while (it < 2) {					
			if (lightLeft.getNormalizedLightValue() > lightLimit) {
				Motor.B.setSpeed(180);
				Motor.B.forward();
				while (lightLeft.getNormalizedLightValue() > lightLimit) {
					Thread.yield();
				}
				Motor.B.stop();
				Sound.beep();			
			} else if (lightRight.getNormalizedLightValue() > lightLimit) {
				Motor.A.setSpeed(180);
				Motor.A.forward();
	
				while (lightRight.getNormalizedLightValue() > lightLimit) {
					Thread.yield();
				}
				Motor.A.stop();
				Sound.beep();
			}
			
			if (it == 0) {
				travel(-30);
				setTravelSpeed(100);
				forward();
				// Tant que c'est une couleur claire
				while (lightLeft.getNormalizedLightValue() > lightLimit && lightRight.getNormalizedLightValue() > lightLimit) {
					Thread.yield();
				}
				Sound.beep();
				stop();
			}
			it++;
		}
	}

	/**
	 * Rotation du moteur "moteur" de "angle" degré, vitesse en d/s
	 * La fonction est bloquante tant que le degré n'est pas atteint
	 * @param moteur
	 * @param vitesse
	 * @param angle
	 */
	private void myRotateTo(NXTRegulatedMotor moteur, int vitesse, int angle) {
		moteur.resetTachoCount();
		
		moteur.setSpeed(vitesse);
		moteur.rotateTo(angle);
		
		while (moteur.isMoving()) {
			Thread.yield();
		}
	}
	
	/**
	 * Eloigne le robot du mur sur son coté,
	 * la direction du robot n'est pas changée
	 * @param cote
	 * @throws InterruptedException
	 */
	public void redressement(int cote) throws InterruptedException {
			
		if (cote == LEFT) {
			myRotateTo(Motor.B, 150, -200);
			myRotateTo(Motor.A, 150, -200);
		} else if (cote == RIGHT) {
			myRotateTo(Motor.A, 150, -200);
			myRotateTo(Motor.B, 150, -200);
		}	

		setTravelSpeed(150);
		travel(60);
	}
	
	/**
	 * Tourne la tête sur le coté donné en paramétre, 
	 * au lancement du programme la tête doit être positionnée
	 * coté face
	 * @param cote
	 */
	public void headTurnTo(int cote){
		switch(cote){
			case LEFT: 	Motor.C.rotateTo(90);
						break;
			case RIGHT: Motor.C.rotateTo(-90);
						break;
			case FACE:	Motor.C.rotateTo(0);
						break;
		}
	}
	
	/**
	 * Corrige l'angle du robot par rapport au mur sur l'un ses cotés
	 * (le repositionne parallèle au mur)
	 * @param cote
	 * @throws InterruptedException
	 */
	/*public void recoveryAngle(int cote) throws InterruptedException {
		
		double x, y, zprime, angle;
		double z = 100;
		UltrasonicSensor sonic;
		
		if (cote == LEFT) {
			sonic = new UltrasonicSensor(SensorPort.S1);
		} else if (cote == RIGHT) {
			sonic = new UltrasonicSensor(SensorPort.S2);
		}
		
		// On avance puis on mesure x
		setTravelSpeed(50);
		travel(z/2);	
	    x = sonic.getDistance()*10;	// On mesure x (*10 pour mettre en mm)
	    Thread.sleep(50);			// Temps pour que le capteur fasse son job correctement
	    
	    // On recule puis on mesure y
	    travel(-z);    
	    y = sonic.getDistance()*10;
	    Thread.sleep(50);
	    
		// Théoréme de thalès && trigonométrie (voir schéma)
	    zprime = (-z*x)/(x-y);
	    angle = Math.asin(y/(zprime+z));	// Angle an radian
	    
	    travel(z/2);	// On se repositionne au point de départ
	    
	    // On corrige l'angle
		if (cote == LEFT) {
			arc(0, -180*angle/Math.PI);
		} else if (cote == RIGHT) {
			arc(0, 180*angle/Math.PI);
		}   
	}*/
}
