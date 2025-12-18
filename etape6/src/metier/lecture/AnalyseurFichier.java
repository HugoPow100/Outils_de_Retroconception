package metier.lecture;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import metier.objet.*;

/**
 * Analyseur de fichiers Java.
 * 
 * Cette classe parcourt un fichier ou un répertoire contenant des fichiers
 * Java, ouvre chaque fichier trouvé puis délègue l'analyse syntaxique à
 * {@link ParseurJava}. Les objets {@link Classe} obtenus sont stockés dans une
 * HashMap, indexés par leur nom de fichier.
 */
public class AnalyseurFichier
{
	private HashMap  <String, Classe> hashMapClasses;
	private ArrayList<String>         lstNomFichier ;

	public AnalyseurFichier()
	{
		this.hashMapClasses = new HashMap  <>();
		this.lstNomFichier  = new ArrayList<>();
	}

	/**
	 * Analyse un fichier Java isolé ou l'ensemble des fichiers d'un répertoire.
	 *
	 * Si le chemin fourni correspond à un répertoire, tous les fichiers
	 * contenus dans ce dossier sont analysés individuellement.
	 *
	 * @param cheminFichier chemin vers un fichier .java ou un répertoire
	 * @return une HashMap associant chaque nom de fichier à l'objet {@link Classe} produit
	 */
	public HashMap<String, Classe> analyser(String cheminFichier)
	{
		Scanner scFic;

		File         f             = new File(cheminFichier);
		List<String> lstCheminFich = new ArrayList<String>();

		// ----- Si c'est un répertoire -----
		if (f.isDirectory())
		{
			this.hashMapClasses = new HashMap<>();

			// Récupére tous les fichiers du dossier
			File[] tabFichiers = f.listFiles();

			if (tabFichiers != null)
			{
				for (File file : tabFichiers)
				{
					if (file.getName().endsWith(".java"))
					{
						lstCheminFich.add(file.getAbsolutePath());

						Path p = file.toPath();
						String nomFichier = String.valueOf(p.getFileName()).replace(".java", "");

						this.lstNomFichier.add(nomFichier);
					}
				}

				// Libére la mémoire
				tabFichiers = null;
			}
		}

		try
		{
			if (!lstCheminFich.isEmpty())
			{
				for (String chemin : lstCheminFich)
				{
					scFic = new Scanner(new FileInputStream(chemin), "UTF8");

					Path   p          = Paths.get(chemin);
					String nomFichier = String.valueOf(p.getFileName());

					ParseurJava parseur             = new ParseurJava();
					Classe      classePourCeFichier = parseur.parser(scFic, nomFichier);

					this.hashMapClasses.put(nomFichier, classePourCeFichier);

					scFic.close();
				}

				lstCheminFich.clear();
			}
			else
			{
				scFic = new Scanner(new FileInputStream(cheminFichier), "UTF8");

				Path   p          = Paths.get(cheminFichier);
				String nomFichier = String.valueOf(p.getFileName());

				ParseurJava parseur             = new ParseurJava();
				Classe      classePourCeFichier = parseur.parser(scFic, nomFichier);

				this.hashMapClasses.put(nomFichier, classePourCeFichier);

				scFic.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Erreur lors de l'analyse du fichier : " + e.getMessage(), e);
		}

		return this.hashMapClasses;
	}

	public HashMap<String, Classe> getHashMapClasses()
	{
		return this.hashMapClasses;
	}

	public ArrayList<String> getLstNomFichier()
	{
		return this.lstNomFichier;
	}
}