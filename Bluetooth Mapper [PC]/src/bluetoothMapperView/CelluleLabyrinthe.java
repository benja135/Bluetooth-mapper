/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

/**
 * Type cellule
 */
public class CelluleLabyrinthe {
    
    public boolean visite;      // Est-ce que la cellule a déjà été visité ?
    public boolean north;       // Est-ce qu'il y a un mur au nord ?
    public boolean south;       // ... au sud ?
    public boolean east;        // ... à l'est ?
    public boolean west;        // ... à l'ouest ?
    
    public CelluleLabyrinthe() {
        this.visite = false;
        this.north = true;
        this.south = true;
        this.east = true;
        this.west = true;
    }
       
}
