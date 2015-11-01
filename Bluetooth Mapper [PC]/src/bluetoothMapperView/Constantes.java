/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

/**
 * Constantes utilisées dans plusieurs classes du projet
 *
 */
public final class Constantes {

	// Empêche l'instanciation de la classe
    private Constantes() {}
    
    // Signaux que le NXT envoie au PC
	public static final int NXT_SCAN_CELL	= 5;
	public static final int NXT_CHANGE_DIR 	= 6;
	public static final int NXT_FIN_DU_GAME = 7;
	
	// Signaux que le PC envoie au NXT
	public static final int MOVE_FORWARD 	= 1;
	public static final int MOVE_LEFT 		= 2;
	public static final int MOVE_RIGHT 		= 3;
	public static final int MOVE_BACKWARD 	= 4;
	public static final int STOP		 	= 8;
	public static final int SEQUENCE		= 9;
	
	// Pour le tableau de résultats du scan
	public static final int AVANT 	= 0;
	public static final int GAUCHE 	= 1;
	public static final int DROITE 	= 2;
	
	public static final int PAS_MUR = 0;
	public static final int MUR = 1;
	
    public enum Dir {
        SOUTH, NORTH, EAST, WEST
    }

    public enum Ref {
        AVANT, GAUCHE, DROITE, ARRIERE
    }

}
