package controlleur;

import metier.lecture.Lecture;
import metier.objet.Classe;
import metier.objet.Association;
import vue.VueTerminal;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Contrôleur principal de l'application de rétroconception.
 * Gère la logique entre le modèle (metier) et la vue (VueTerminal).
 */
public class ControleurRetroconception 
{
	private Lecture lecture;
	private VueTerminal vue;

	/**
	 * Constructeur du contrôleur.
	 * 
	 * @param cheminFichier Chemin du fichier ou dossier à analyser
	 */
	public ControleurRetroconception(String cheminFichier) 
    {
		this.lecture = new Lecture(cheminFichier);
		this.vue = new VueTerminal();
	}

	/**
	 * Lance l'analyse et l'affichage des résultats.
	 */
	public void executer() 
    {
		// Récupération des données du modèle
		HashMap  <String, Classe> hashMapClasses = this.lecture.getHashMapClasses();
		ArrayList<Association>    associations   = this.lecture.getLstAssociation();

		// Affichage via la vue
		this.vue.afficherClasses(hashMapClasses);

		this.vue.afficherAssociations  (associations);
		this.vue.afficherHeritage      (this.lecture);

		this.vue.afficherImplementation(this.lecture);
	}
}
