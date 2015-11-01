/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

/**
 * Constantes utilisées dans plusieurs classes du projet
 *
 */
public final class Constantes {

	// Empêche l'instanciation de la classe
    private Constantes() {}    
    
    // Seuils de détection //TODO
    public static final int distanceFace = 28;		// si > alors il n'y a rien en face
    public static final int distanceGauche = 28;	// si > alors il n'y a rien sur le coté
    public static final int distanceDroit = 33;		// si > alors il n'y a rien sur le coté
    public static final int distanceMinDroite = 16;	// si < alors on redresse
    public static final int distanceMinGauche = 8;	// si < alors on redresse
    public static final int lightLimit = 450;		// si < alors c'est une bande noire
    
    // Signaux que le NXT envoie au PC
	public static final int NXT_SCAN_CELL	= 5;
	public static final int NXT_FIN_DU_GAME = 7;
	
	// Signaux que le PC envoie au NXT
	public static final int MOVE_FORWARD 	= 1;
	public static final int MOVE_LEFT 		= 2;
	public static final int MOVE_RIGHT 		= 3;
	public static final int MOVE_BACKWARD 	= 4;
	public static final int STOP		 	= 8;
	public static final int SEQUENCE		= 9;
	
	public static final int RIGHT = 1;
	public static final int LEFT = 0;
	public static final int FACE = 2;

}
