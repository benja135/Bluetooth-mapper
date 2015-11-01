/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import java.util.ArrayList;

import bluetoothMapperView.Constantes.*;

/**
 * Type noeud
 *
 */
public class Node {
	
    private Point point; 				// Valeur stockée dans le noeud
    public ArrayList<Node> fils; 		// Liste des noeuds fils
    public Node parent;
    public Dir dir;

 
    // Constructeur
    public Node (Point point, Node parent, Dir parentDir) {
        this.point = point;
        this.fils = new ArrayList<Node>();
        this.parent = parent;
        this.dir = parentDir;		// Orientation du noeud par rapport à son parent
    }
    
    public int getX() {
    	return point.x;
    }
    
    public int getY() {
    	return point.y;
    }
    
    public String toString() {
    	return point.x + ":"  + point.y;
    }
    
    public boolean equals(Node node) {
    	return (this.getX() == node.getX()) && (this.getY() == node.getY());
    }
    
}