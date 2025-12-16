package controlleur;

/**
 * Classe principale de l'application de rétroconception.
 * Point d'entrée de l'application.
 */
public class Retroconception 
{
	public static void main(String[] args) 
    {
		if (args.length == 0) 
        {
			System.out.println("Usage: java Retroconception <fichier.java ou dossier>");
			return;
		}

		String cheminFichier = args[0];

		// Création et lancement du contrôleur
		ControleurRetroconception controleur = new ControleurRetroconception(cheminFichier);
		controleur.executer();
	}
}
