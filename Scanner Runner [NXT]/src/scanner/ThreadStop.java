/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;
import lejos.nxt.Button;

/**
 * Ce thread permet d'interrompre un programme
 * simplement en appuyant sur un bouton
 * Utile pour ne pas être obligé de trouver un cure-dent
 * pour reboot le NXT...
 *
 */
public class ThreadStop extends Thread {
	
	
	public void run() {
	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Button.waitForAnyPress();
		System.exit(0);

	}
}
