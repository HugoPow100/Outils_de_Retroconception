package metier.util.test_structure_projet;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe chargée de vérifier la conformité de la structure du projet.
 *
 * Elle s'appuie sur l'enum {@link ElementStructureProjet} pour vérifier
 * la présence des dossiers et fichiers obligatoires, et signale toute
 * absence détectée de fichier ou de dossier essentiel au bons fonctionement
 * du projet.
 */
public class VerificationStructureProjet
{
	private final List<String> erreurs;

	//Uniquement pour l'afficahge 
	private  final String ROUGE = "\u001B[31m";
	public   final String JAUNE = "\u001B[33m";
	public   final String VERT  = "\u001B[32m";
	private  final String RESET = "\u001B[0m";

	public VerificationStructureProjet()
	{
		this.erreurs = new ArrayList<>();
	}

	/**
	 * Vérifie la structure du projet courant.
	 *
	 * Si un ou plusieurs éléments obligatoires sont manquants, les erreurs
	 * sont affichées sur la sortie d'erreur et l'exécution du programme est arrêtée.
	 */
	public void verifierStructure()
	{
		
		// VÉRIFICATION GÉNÉRALE
		for (ElementStructureProjet element : ElementStructureProjet.values())
		{
			if (!element.existe())
			{
				erreurs.add(element.getMessageErreur());
				creerElement(element);
			}
		}

		
		// verifie les SCRIPTS DE LANCEMENT
		if (!Files.exists(Path.of("run.sh")) &&
			!Files.exists(Path.of("run.bat")))
		{
			erreurs.add("Script de lancement manquant (run.sh ou run.bat)");
		}


		// AFFICHAGE DU RÉSULTAT
		if (!erreurs.isEmpty())
		{
			System.err.println( ROUGE                                    +
								"====================================\n" +
								" ERREUR : STRUCTURE PROJET INVALIDE\n"  +
								"===================================="   +
								RESET);

			for (String erreur : erreurs)
			{
				System.err.println(ROUGE + "[X] " + JAUNE + erreur + RESET);
			}

			
		}
		else
		{
			System.err.println( VERT                                                                           + 
								"==============================================================================\n" +
								" STRUCTURE VALIDÉE : tous les dossiers/fichiers obligatoires sont présents\n"   +
								"=============================================================================="    +
								RESET );
		}
	}

	private void creerElement(ElementStructureProjet element)
	{
		Path path = Path.of(element.getChemin());

		try
		{
			if (element.getType() == TypeElement.DOSSIER)
			{
				Files.createDirectories(path);
			}
			else
			{
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}

			erreurs.add(String.format("%s%-22s %s%s", VERT, "Créé automatiquement :", element.getChemin(), RESET));


		}
		catch (Exception e)
		{
			erreurs.add("Impossible de créer : " + element.getChemin());
		}
	}
}

