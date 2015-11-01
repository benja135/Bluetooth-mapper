Il s'agit du projet initial qui a été rendu à la fin de notre TER (travail d'étude et de recherche).
Depuis une version avec quelques améliorations a été créé : https://github.com/benja135/bluetooth-mapper-v2

# bluetooth-mapper
Cartographie et résolution de labyrinthe pour robot Lego Mindostorms NXT (équipe du firmware NXJ LejOS)

Pour compiler et lancer les programmes, il faut installer le logiciel
NXJ leJOS : http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/GettingStarted.htm

Puis installer le plugin Eclipse :
http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm

Vous pouvez ensuite importer les programmes du dossier courant.

Les projets principaux sont : 
  - Scanner Runner [NXT]
  - Bluetooth Mapper [PC]

(la Javadoc est disponible dans leur dossier doc)

Le sigle [PC] signifie que le programme fonctionne du coté PC.
Le sigle [NXT] signifie que le programme fonctionne du coté robot.

Vous devez disposez d'un dongle bluetooth sur votre ordinateur.

Note: pour que tout fonctionne, l'adresse du robot dans la classe NxtConnexion
doit être remplacée par l'adresse de votre robot.
