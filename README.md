Pour lancer le Web Server :
1) Dans WebServer.java : Changer le chemin vers le dossier lib des ressources (ligne 64) Attention finir par \\
2) Compiler (javac http/server/WebServer.java) puis exécuter (java http.server.WebServer)
3) Tester depuis un navigateur web
	* Page principale
		- http://localhost:1234/
		- http://localhost:1234/nimportequoi --> erreur 404
	* Les pages html :
		- http://localhost:1234/adder.html --> dynamique
	* Les images :
		- http://localhost:1234/ananas.jpg
		- http://localhost:1234/kiwi.jpeg
		- http://localhost:1234/fraise.png
		- http://localhost:1234/cerise.svg
		- http://localhost:1234/pomme.gif
	* Les pdf :
		- http://localhost:1234/Facts.pdf
	* Les audios :
		- http://localhost:1234/BuenaVista.mp3
	* Les vidéos :
		- http://localhost:1234/pub.mp4
4) Tester depuis postman
	* GET
		- http://localhost:1234/nimportequoi --> erreur 404
		- http://localhost:1234/fraise.png --> header + taille
	* HEAD
		- http://localhost:1234/fraise.png --> header + taille
		- http://localhost:1234/nimportequoi --> erreur 404
	* POST
		- http://localhost:1234/test.txt --> 200 OK mis à jour
		- http://localhost:1234/test2.txt --> 201 Created puis écrire dedans
	* PUT
		- http://localhost:1234/test.txt --> 200 OK (fichier remis à vide)
		- http://localhost:1234/test3.txt --> 201 Created
	* DELETE
		- http://localhost:1234/test2.txt
		- http://localhost:1234/nimportequoi --> erreur 404
