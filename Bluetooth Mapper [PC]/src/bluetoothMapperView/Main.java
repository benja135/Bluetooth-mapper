/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import static bluetoothMapperView.Constantes.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Contient la méthode main du projet
 *
 */
public class Main {

	private static NxtConnexion nxt;
	private static Window w;
	private static int phase = 1;
	private static FileReader fileReader;
	private static FileWriter fileWriter;

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws IOException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */   
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
        nxt = new NxtConnexion();	
        w = new Window(nxt);
        w.setVisible(true);
        w.phase.setText(String.valueOf(phase));
        boolean actuPos = true;
        
        while (true) {
        	if (nxt.isConnected())
        	{
        		if (phase == 1) 
        		{
		        	int resultScan[] = new int[3];
		        	System.out.println("Attente du sinal");
		        		        	
			        switch (nxt.receive()) {        
			        
			        	case NXT_SCAN_CELL :		        			        		
			        				
	    							System.out.println("Signal de scan reçu");
					        		for (int i=0; i<3; i++) {
					        			resultScan[i] = nxt.receive();
					        		}
					        		w.LabyrintheEtAffichage.scanOfNextCellIs(resultScan, actuPos);
					        		
					        		actuPos = true;
					        		
					        		if (resultScan[DROITE] == PAS_MUR && w.LabyrintheEtAffichage.cellNotVisited(DROITE)) {
								        	nxt.send(MOVE_RIGHT);
								        	w.LabyrintheEtAffichage.changementDirection(Ref.DROITE);
					        		} else if (resultScan[AVANT] == PAS_MUR && w.LabyrintheEtAffichage.cellNotVisited(AVANT)) {
							        	nxt.send(MOVE_FORWARD);	
					        		} else if (resultScan[GAUCHE] == PAS_MUR && w.LabyrintheEtAffichage.cellNotVisited(GAUCHE)) {
								        	nxt.send(MOVE_LEFT);
								        	w.LabyrintheEtAffichage.changementDirection(Ref.GAUCHE);
							        } else {
							        	System.out.println("Impasse détectée");
							        	sendSequence();	// Impasse
							        	actuPos = false;
							        }
				        		
					        		break;
			        		      	
					        		
			        	case NXT_FIN_DU_GAME :
			        		
			        				System.out.println("Signal FIN_DU_GAME reçu");
			        				
			        				w.LabyrintheEtAffichage.closeTheLastCell(actuPos);
			        				actuPos = true;
			        				
	    							System.out.println("Final Cell: " + w.LabyrintheEtAffichage.getFinalCell());
	    							Point cellToVisite = w.LabyrintheEtAffichage.returnCellNotVisited();
	    							if (cellToVisite == null) 
	    							{	
	    								// Enregistrement de la sequence la plus courte
	    								File seq = new File("seq.txt");
	    								fileWriter = new FileWriter(seq, false);	    								
	    								
	    								Point cell = w.LabyrintheEtAffichage.getFinalCell();
	    								w.LabyrintheEtAffichage.setPos(10, 11);
	    								w.LabyrintheEtAffichage.setDir(Dir.NORTH);
        			        
					        			Deque<Dir> path = new ArrayDeque<Dir>();
					        	        path = w.LabyrintheEtAffichage.pathFinding
					        	        		(w.LabyrintheEtAffichage.getCellNxt(),cell);
					        	        
					        	        while (!path.isEmpty()) {
					        	        	fileWriter.write(String.valueOf(w.LabyrintheEtAffichage.destToInstruction(path.pollLast())));
					        	        }
					        	        fileWriter.flush();
					        	        fileWriter.close();
					        	        // Fin enregistrement
					        	             	        
	    								nxt.send(STOP);
	    								nxt.deconnexion();
	    								phase = 2;
	    								w.phase.setText("2");
	    							} else {
	    								sendSequence();
	    								actuPos = false;
	    							}
						            break;
			        	
			        }
        		} else if (phase == 2) 
        		{
        			// Chargement de la séquence
					File seq = new File("seq.txt");
					fileReader = new FileReader(seq);
					nxt.send(SEQUENCE);
			        while (fileReader.ready()) {				
			        	nxt.send(Character.getNumericValue(fileReader.read()));
			        }
			        nxt.send(SEQUENCE);
        			phase = 3;
        			w.phase.setText("Fini !");
        		}

        	} else {
        		Thread.sleep(500);	// On est pas connecté alors on attend pour pas boucler trop vite
        	}
        	
        }
    }
     
        	
	private static void sendSequence() {
		
		w.LabyrintheEtAffichage.resolveDeductibleCells();
		
		Point cellToVisite = w.LabyrintheEtAffichage.returnCellNotVisited();
		
		if (cellToVisite == null) {
			cellToVisite = w.LabyrintheEtAffichage.getFinalCell();
		}
		
		System.out.println("CellToVisite: " + cellToVisite.toString());

		Deque<Dir> path = new ArrayDeque<Dir>();
        path = w.LabyrintheEtAffichage.pathFinding
        		(w.LabyrintheEtAffichage.getCellNxt(),cellToVisite);
        nxt.send(SEQUENCE);
        System.out.println("Chemin pour " + w.LabyrintheEtAffichage.getCellNxt() 
        		+ " " + cellToVisite.toString() + ": " + path);
        while (!path.isEmpty()) {				
        	nxt.send(w.LabyrintheEtAffichage.destToInstruction(path.pollLast()));
        }
        nxt.send(SEQUENCE);
        
        w.LabyrintheEtAffichage.setPos(cellToVisite.x, cellToVisite.y);
	}
    
}
