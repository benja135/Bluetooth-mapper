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
 * Fourni des déplacements et redressements optimisés pour 
 * la vitesse. Pour plus de documentation sur les méthodes,
 * voir la classe DeplacementOp
 *
 */
public class DeplacementFast extends DifferentialPilot {
	
	public DeplacementFast() {	
		//super(wheelDiameter, trackWidth, leftMotor, rightMotor);
		super(56, 120, Motor.B, Motor.A);	
	}
	
	public void fastHairpinTurnTo(int cote) {
		setTravelSpeed(340);
		if (cote == LEFT) {
			arc(0,90);
		} else if (cote == RIGHT) {
			arc(0,-90);
		}
	}
	
	public void fastRecoveryAngleWithLine() {

	    LightSensor lightLeft = new LightSensor(SensorPort.S2, true);
	    LightSensor lightRight = new LightSensor(SensorPort.S3, true);				
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
	}
	
	private void myRotateTo(NXTRegulatedMotor moteur, int vitesse, int angle) {
		moteur.resetTachoCount();
		
		moteur.setSpeed(vitesse);
		moteur.rotateTo(angle);
		
		while (moteur.isMoving()) {
			Thread.yield();
		}
	}
	
	public void fastRedressement(int cote) throws InterruptedException {
		int vitesse = 600; // en degré/s
		if (cote == LEFT) {
			myRotateTo(Motor.B, vitesse, -200);
			myRotateTo(Motor.A, vitesse, -200);
		} else if (cote == RIGHT) {
			myRotateTo(Motor.A, vitesse, -200);
			myRotateTo(Motor.B, vitesse, -200);
		}	

		setTravelSpeed(150);
		travel(60);
	}
	
}
