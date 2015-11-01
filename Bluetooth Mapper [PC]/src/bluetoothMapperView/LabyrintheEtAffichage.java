/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import static bluetoothMapperView.Constantes.*;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.swing.JPanel;


/**
 * Dessine et gère tout ce qui a trait au labyrinthe
 * 
 */

@SuppressWarnings("serial")
public class LabyrintheEtAffichage extends JPanel {
  
    private final int colonne = 21;
    private final int ligne = 21;
    
    private int x;                  			// Colonne courante
    private int y;                  			// Ligne courante
    private Dir dir = Dir.NORTH;    			// Direction courante   
    private CelluleLabyrinthe labyrinthe[][];
    private boolean cellDejaVisite[][];			// Pour le pathFinding
    private Point finalCell = null;


    public LabyrintheEtAffichage() {
        this.labyrinthe = new CelluleLabyrinthe[colonne][ligne];
        this.initLabyrinthe();
        this.x = 10;
        this.y = 11;
        labyrinthe[x][y].visite = true;
        labyrinthe[x][y].north = false;
    }
    
    	 
    private void initLabyrinthe() {
        for (int c = 0; c < colonne; c++) {
            for (int l = 0; l < ligne; l++) {
                this.labyrinthe[c][l] = new CelluleLabyrinthe();
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {

        int largeur = this.getWidth();
        int hauteur = this.getHeight();

        // Dessine un fond coloré
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, largeur, hauteur);

        // Dessine la grille 
        g.setColor(Color.BLACK);
        for (int c = 0; c < colonne; c++) {
            g.drawLine(c*largeur/colonne, 0, c*largeur/colonne, hauteur);
        }
        for (int l = 0; l < ligne; l++) {
            g.drawLine(0, l*hauteur/ligne, largeur, l*hauteur/ligne);
        }


        // Color les cases si elles ont déjà été visitées
        for (int c = 0; c < colonne; c++) {
            for (int l = 0; l < ligne; l++) {
                if (labyrinthe[c][l].visite) {
                    if (x == c && y == l) { // Si le robot est sûr cette cellule
                        g.setColor(Color.BLUE);
                    } else if (finalCell != null) {               	
                    	if (finalCell.equals(new Point(c,l))) {	// Si c'est la cellule d'arrivé
                    		g.setColor(Color.YELLOW);
                    	} else {
                    		g.setColor(Color.GREEN);
                    	}
                    } else {
                        g.setColor(Color.GREEN);
                    }
                    g.fillRect(c*largeur/colonne, l*hauteur/ligne, largeur/colonne+1, hauteur/ligne+1);

                    // Si on connait la case, on dessine aussi les murs qui l'entoure
                    g.setColor(Color.RED);
                    if (labyrinthe[c][l].north) {  
                    	g.fillRect(c*largeur/colonne, l*hauteur/ligne, largeur/colonne+1, 2);
                    }
                    if (labyrinthe[c][l].south) {
                    	g.fillRect(c*largeur/colonne, (l+1)*hauteur/ligne, largeur/colonne+1, 2);
                    }
                    if (labyrinthe[c][l].east) {
                    	g.fillRect((c+1)*largeur/colonne, l*hauteur/ligne, 2, hauteur/ligne+2);
                    }
                    if (labyrinthe[c][l].west) {
                    	g.fillRect(c*largeur/colonne, l*hauteur/ligne, 2, hauteur/ligne+2);
                    }

                }

            }
        }


    }


    /**
     * Pour ajouter une cellule qui vient d'être scanné
     * @param resultScan
     * @param actuPos
     */
    public void scanOfNextCellIs(int resultScan[], boolean actuPos) {
        
        /* Calcule de la prochaine case en fonction
         * de la direction dans laquelle le robot était
         */
    	if (actuPos) {
	        switch (dir) {
	            case NORTH: y--;
	                        break;
	            case SOUTH: y++;
	                        break;
	            case EAST:  x++;
	                        break;
	            case WEST:  x--;
	                        break;
	        }
    	}
        
        /* Maintenant qu'on la connait, 
         * on redéfinie correctement la case
         */
        labyrinthe[x][y].visite = true;
        
        switch (dir) {
        	case NORTH :	labyrinthe[x][y].north  = (resultScan[AVANT] == MUR);
				            labyrinthe[x][y].south  = false;
				            labyrinthe[x][y].west   = (resultScan[GAUCHE] == MUR);
				            labyrinthe[x][y].east   = (resultScan[DROITE] == MUR);
				            break;
				            
        	case SOUTH :	labyrinthe[x][y].north  = false;
				            labyrinthe[x][y].south  = (resultScan[AVANT] == MUR);
				            labyrinthe[x][y].west   = (resultScan[DROITE] == MUR);
				            labyrinthe[x][y].east   = (resultScan[GAUCHE] == MUR);
				            break;
				            
        	case EAST :		labyrinthe[x][y].north  = (resultScan[GAUCHE] == MUR);
				            labyrinthe[x][y].south  = (resultScan[DROITE] == MUR);
				            labyrinthe[x][y].west   = false;
				            labyrinthe[x][y].east   = (resultScan[AVANT] == MUR);
				            break;
				            
        	case WEST :		labyrinthe[x][y].north  = (resultScan[DROITE] == MUR);
				            labyrinthe[x][y].south  = (resultScan[GAUCHE] == MUR);
				            labyrinthe[x][y].west   = (resultScan[AVANT] == MUR);
				            labyrinthe[x][y].east   = false;
				            break;
        }
        
        repaint();     
    }


    /**
     * Change l'orientation en fonction de l'orientation courante (dir)
     * et de là où va aller le robot (ref)
     * @param ref
     */
	@SuppressWarnings("incomplete-switch")
	public void changementDirection(Ref ref) {
		switch (dir) {
			case NORTH: switch (ref) {
							case GAUCHE : dir = Dir.WEST; break;
							case DROITE : dir = Dir.EAST; break;
							case ARRIERE : dir = Dir.SOUTH; break;
						}
						break;
			case SOUTH: switch (ref) {
							case GAUCHE : dir = Dir.EAST; break;
							case DROITE : dir = Dir.WEST; break;
							case ARRIERE : dir = Dir.NORTH; break;
						}
						break;
			case EAST: switch (ref) {
							case GAUCHE : dir = Dir.NORTH; break;
							case DROITE : dir = Dir.SOUTH; break;
							case ARRIERE : dir = Dir.WEST; break;
						}
						break;
			case WEST: switch (ref) {
							case GAUCHE : dir = Dir.SOUTH; break;
							case DROITE : dir = Dir.NORTH; break;
							case ARRIERE : dir = Dir.EAST; break;
						}
						break;
		}
		
	}
	
	
	/**
	 * Recherche les fils d'un noeud et les ajoute à sa liste
	 * Ajoute que la case a maintenant été visitée
	 */
	private void searchForChild(Node node) {
		
		// Ajoute le fils nord au noeud courant s'il n'y a pas de mur entre eux
		// Et si le fils n'a pas été déjà visité !
		if (!labyrinthe[node.getX()][node.getY()].north) {
			if (!cellDejaVisite[node.getX()][node.getY()-1]) {
				node.fils.add(new Node(new Point(node.getX(),node.getY()-1), node, Dir.NORTH));
				cellDejaVisite[node.getX()][node.getY()-1] = true;
			}		
		}
		// Pareil mais pour le fils du sud
		if (!labyrinthe[node.getX()][node.getY()].south) {
			if (!cellDejaVisite[node.getX()][node.getY()+1]) {
				node.fils.add(new Node(new Point(node.getX(),node.getY()+1), node, Dir.SOUTH));
				cellDejaVisite[node.getX()][node.getY()+1] = true;
			}	
		}
		// Etc...
		if (!labyrinthe[node.getX()][node.getY()].west) {
			if (!cellDejaVisite[node.getX()-1][node.getY()]) {
				node.fils.add(new Node(new Point(node.getX()-1,node.getY()), node, Dir.WEST));
				cellDejaVisite[node.getX()-1][node.getY()] = true;
			}
		}
		if (!labyrinthe[node.getX()][node.getY()].east) {
			if (!cellDejaVisite[node.getX()+1][node.getY()]) {
				node.fils.add(new Node(new Point(node.getX()+1,node.getY()), node, Dir.EAST));
				cellDejaVisite[node.getX()+1][node.getY()] = true;
			}	
		}
		
	}
	
	/**
	 * Recherche les fils d'un noeud récursivement (parcours en largeur)
	 * jusqu'à avoir atteint la cellule d'arrivée ou avoir visité toutes
	 * les cases possibles
	 * Retourne le chemin entre la cellule d'arrivée et de départ, 
	 * ou null s'il n'y pas de chemin
	 * @param p1
	 * @param p2
	 * @return ArrayDeque contenant la liste des orientations a prendre
	 */
	public Deque<Dir> pathFinding(Point p1, Point p2) {
		
		Node depart = new Node(p1, null, null);
		Node arrive = new Node(p2, null, null);
		cellDejaVisite = new boolean[colonne][ligne];
		
		// Initialise le tableau des "noeuds" déjà parcourus
		for (int c = 0; c < colonne; c++) {
			for (int l = 0; l < ligne; l++) {
				cellDejaVisite[c][l] = false;
			}
		}
		cellDejaVisite[depart.getX()][depart.getY()] = true;
		
		Deque<Node> file = new ArrayDeque<Node>();
		Deque<Node> newFile = new ArrayDeque<Node>();
		
		/* Etapes de l'algorithme : 
		 * 	1. 	On place dans "file" le noeud de départ
		 * 	2. 	Puis on cherche et place tous les fils de ce(s) noeud(s)
		 * 		dans "newFile", au fur à mesure on vide "file" (en enlevant les
		 * 		noeuds dont les fils ont été trouvés)
		 * 	3.	On place tout les noeuds de "newFile" dans "file" et on vide "newFile"
		 *  4. 	On reprend à l'étape 2
		 *  5.	Quand on trouve le noeud correspondant au noeud d'arrivé, alors on 
		 *  	arrête et on retrace son chemin en parcourant ses parents (pathBuilder)
		 *  ->	Ce chemin est le plus court chemin entre le départ est l'arrivé !
		 */
		
		file.offer(depart);		// Etape 1
		
		while (!file.isEmpty()) {		// Etape 2
			while (!file.isEmpty()) {
				searchForChild(file.peek());		// Recherche les fils du noeud
				for (Node n : file.poll().fils) {	// Enléve un noeud à file ...
					newFile.offer(n);				// ... Ajoute ses fils à newFile
					if (n.equals(arrive)) {		// Etape 5
						return pathBuilder(n);
					}
				}
			}
			for (Node n : newFile) {	// Etape 3
				file.offer(n);
			}
			newFile.clear();
		}	// Etape 4
		
		return null;	// Pas de chemin trouvé
	}
	
	/**
	 * Construit la liste des parents du noeud jusqu'à trouver null
	 * (null correspondant au parent de la cellule de départ)
	 * @param node
	 * @return ArrayDeque contenant la liste des orientations a prendre
	 */
	private Deque<Dir> pathBuilder(Node node) {
		Deque<Dir> path = new ArrayDeque<Dir>();

		while (node.parent != null) {
			path.offer(node.dir);
			node = node.parent;
		}
		return path;
	}


	/**
	 * Retourne une cellule du labyrinthe accessible 
	 * mais encore non visité par le robot, s'il n'y a 
	 * pas de cellule non visité: retourne null
	 * S'il y a plusieur cellule non visité, c'est la cellule 
	 * la plus proche du robot qui est retourné !
	 * @return point
	 */
	public Point returnCellNotVisited() {
		List<Point> cell = new ArrayList<Point>();
		
		for (int c = 0; c < colonne; c++) {
			for (int l = 0; l < ligne; l++) {
				if (labyrinthe[c][l].visite) {
					if (!labyrinthe[c][l].north) {			// S'il n'y a pas de mur entre la cellule et son fils nord
						if (!labyrinthe[c][l-1].visite) {	// Mais que la cellule n'a pas été visité
							cell.add(new Point(c,l-1));		// Alors on doit aller dans cette cellule
						}
					}
					if (!labyrinthe[c][l].south) {
						if (!labyrinthe[c][l+1].visite) {
							cell.add(new Point(c,l+1));
						}
					}
					if (!labyrinthe[c][l].east) {
						if (!labyrinthe[c+1][l].visite) {
							cell.add(new Point(c+1,l));
						}
					}
					if (!labyrinthe[c][l].west) {
						if (!labyrinthe[c-1][l].visite) {
							cell.add(new Point(c-1,l));
						}
					}
				}
			}
		}
		
		if (cell.isEmpty()) {	// S'il n'y pas de cellule non visité
			return null;
		}
		
		// On va maitenant calculer la cellule la plus proche du robot
		int minSize = 0;
		Point minPoint = null;
		for (Point e : cell) {
			int eSize = pathFinding(getCellNxt(), e).size();
			if (eSize < minSize || minPoint == null) {
				minSize = eSize;
				minPoint = e;
			}
		}
		return minPoint;
	}
	
	public Point getCellNxt() {
		return new Point(x,y);
	}
	
	/**
	 * Convertie une orientation à prendre en une instruction de direction
	 * en fonction de l'orientation courante
	 * @param dest
	 * @return instruction
	 */
	public int destToInstruction(Dir dest) {
		switch (dir) {
			case NORTH: switch (dest) {
							case NORTH : return MOVE_FORWARD;
							case SOUTH : dir = Dir.SOUTH; return MOVE_BACKWARD;
							case EAST : dir = Dir.EAST; return MOVE_RIGHT;
							case WEST : dir = Dir.WEST; return MOVE_LEFT;
						}
						break;
			case SOUTH: switch (dest) {
							case NORTH : dir = Dir.NORTH; return MOVE_BACKWARD;
							case SOUTH : return MOVE_FORWARD;
							case EAST : dir = Dir.EAST; return MOVE_LEFT;
							case WEST : dir = Dir.WEST; return MOVE_RIGHT;
						}
						break;
			case EAST: switch (dest) {
							case NORTH : dir = Dir.NORTH; return MOVE_LEFT;
							case SOUTH : dir = Dir.SOUTH; return MOVE_RIGHT;
							case EAST : return MOVE_FORWARD;
							case WEST : dir = Dir.WEST; return MOVE_BACKWARD;
						}
						break;
			case WEST: switch (dest) {
							case NORTH : dir = Dir.NORTH; return MOVE_RIGHT;
							case SOUTH : dir = Dir.SOUTH; return MOVE_LEFT;
							case EAST : dir = Dir.EAST; return MOVE_BACKWARD;
							case WEST : return MOVE_FORWARD;
						}
						break;
		}
		return 0;
	}
	
	/**
	 * Ferme la cellule finale et note sa position
	 * (Doit être appelée après avoir trouver la ligne d'arrivé)
	 */
	public void closeTheLastCell(boolean afterSequence) {
		
		// Simule le scan de la dernière case: c'est une impasse
		int resultScan[] = new int[3];
		for (int i = 0; i < 3; i++) {
			resultScan[i] = MUR;
		}
 		
        scanOfNextCellIs(resultScan, afterSequence); 	// Ajoute la cellule au labyrinthe
        
        finalCell = new Point(x,y);						// Note sa position
    
    	repaint();
	}


	/**
	 * Retourne si une cellule voisine (à droite, à gauche, ou en avant) 
	 * de la cellule courante du robot a été visitée ou pas
	 * @param cote
	 * @return cellNotVisited
	 */
	public boolean cellNotVisited(int cote) {
		switch (cote) {
				case AVANT:  switch (dir) {
									case NORTH : return !labyrinthe[x][y-1].visite;
									case SOUTH : return !labyrinthe[x][y+1].visite;
									case EAST : return !labyrinthe[x+1][y].visite;
									case WEST : return !labyrinthe[x-1][y].visite;
								}
								break;
				case GAUCHE:	switch (dir) {
									case NORTH : return !labyrinthe[x-1][y].visite;
									case SOUTH : return !labyrinthe[x+1][y].visite;
									case EAST : return !labyrinthe[x][y-1].visite;
									case WEST : return !labyrinthe[x][y+1].visite;
								}
								break;
				case DROITE:	switch (dir) {
									case NORTH : return !labyrinthe[x+1][y].visite;
									case SOUTH : return !labyrinthe[x-1][y+1].visite;
									case EAST : return !labyrinthe[x][y+1].visite;
									case WEST : return !labyrinthe[x][y-1].visite;
								}
								break;
		}
		return false;
	}
	
	/**
	 * Résout les cellules déductibles non visitées, c'est à dire
	 * les cellules non visitées mais dont on connait tous les voisins
	 */
	public void resolveDeductibleCells() {
		for (int c = 1; c < colonne-1; c++) {
			for (int l = 1; l < ligne-1; l++) {
				if (!labyrinthe[c][l].visite) {
					if (voisinsConnus(c, l)) {
						labyrinthe[c][l].north = labyrinthe[c][l-1].south;							
						labyrinthe[c][l].south = labyrinthe[c][l+1].north;				
						labyrinthe[c][l].west = labyrinthe[c+1][l].east;						
						labyrinthe[c][l].east = labyrinthe[c-1][l].west;
						labyrinthe[c][l].visite = true;
						System.out.println(c + ":" + l + " a été résolue");
					}
				}
			}
		}
	}
	
	private boolean voisinsConnus(int c, int l) {
		
		if (!labyrinthe[c][l-1].visite)
			return false;

		if (!labyrinthe[c][l+1].visite)
			return false;
	
		if (!labyrinthe[c+1][l].visite)
			return false;
		
		if (!labyrinthe[c-1][l].visite)
			return false;
		
		return true;
	}
	
	public Point getFinalCell() {
		return finalCell;
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setDir(Dir dir) {
		this.dir = dir;
	}
}