/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * Permet de gérer plus facilement 
 * la connexion au NXT
 *
 */
public class NxtConnexion {

	private static NXTConnector conn;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private boolean connexion = false;
		
	/**
	 * Initialisation de la connexion à Hydra,
	 * ouverture des streams d'écriture et de lecture
	 */
	public void connexion() {
		
		conn = new NXTConnector();
		
		if (!connexion) {
			conn.addLogListener(new NXTCommLogListener(){
	
				public void logEvent(String message) {
					System.out.println("BTSend Log.listener: "+message);
					
				}
	
				public void logEvent(Throwable throwable) {
					System.out.println("BTSend Log.listener - stack trace: ");
					throwable.printStackTrace();
				}	
			} 
			);
			
			// Connect to any NXT over Bluetooth : "btspp://"
			// Adresse de Hydra : 001653161388
			System.out.println("Tentative de connexion à Hydra");
			connexion = conn.connectTo("btspp://001653161388");
			
			if (!connexion) {
				System.err.println("Failed to connect to any NXT");
			} else {
				System.out.println("Connexion OK");
				dos = new DataOutputStream(conn.getOutputStream());
				dis = new DataInputStream(conn.getInputStream());
			}
			
		} else {
			System.out.println("Vous êtes déjà connecté au NXT");
		}
	}

	
	/**
	 * Envoie un entier au NXT 
	 * @param i
	 */
	public void send(int i) {
		if (connexion) {
			try {
				System.out.println("Sending : " + i);
				dos.writeInt(i);
				dos.flush();			
				
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
			}
		} else {
			System.out.println("Vous n'êtes pas connecté au NXT");
		}
	}
	
	
	/**
	 * Lit un entier sur le stream de lecture
	 * S'il n'y a rien sur le stream, l'exeception est gérée, et
	 * la méthode ne se termine pas avant qu'un entier ait été lu
	 * @return l'entier lu, ou -1 si on est pas connecté !
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public int receive() throws InterruptedException, IOException {	
		if (connexion) {
			int val = -2;	
			
			while (val == -2) {
				try {				
					val = dis.readInt();
				} catch (NullPointerException e) {
					val = -2;
					System.out.println("IO Exception");
					System.out.println(e.getMessage());
					System.out.println("Reception Fail");
				}		
				Thread.sleep(100);
			}
			System.out.println("Reception OK");
			return val;
				
		} else {
			System.out.println("Vous n'êtes pas connecté au NXT");
			return -1;
		}		
	}

	/**
	 * Déconnexion du NXT
	 */
	public void deconnexion() {
		if (connexion) {
			this.send(0);
			try {
				dis.close();
				dos.close();
				conn.close();
			} catch (IOException ioe) {
				System.out.println("IOException closing connection:");
				System.out.println(ioe.getMessage());
			}
			connexion = false;
			System.out.println("Déconnexion OK");
		} else {
			System.out.println("Vous n'êtes pas connecté au NXT");
		}
	}
	
	/**
	 * Est-on connecté ou pas ?
	 */
	public boolean isConnected() {
		return connexion;
	}

}
