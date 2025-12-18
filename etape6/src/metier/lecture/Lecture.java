package metier.lecture;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import metier.objet.*;

/**
 * Classe principale pour la lecture et l'analyse de fichiers Java.
 * Orchestre l'analyse des fichiers, la génération des associations et des héritages.
 */
public class Lecture
{
	private HashMap  <String, Classe> hashMapClasses ;
	private ArrayList<Heritage      > lstHeritage    ;
	private ArrayList<Association   > lstAssociations;
	private ArrayList<Interface     > lstInterface   ;

	public Lecture(String cheminFichier)
	{
		this.hashMapClasses       = new HashMap  <String, Classe>();
		this.lstHeritage          = new ArrayList<Heritage      >();
		this.lstAssociations      = new ArrayList<Association   >();
		this.lstInterface         = new ArrayList<Interface     >();

		analyserFichier (cheminFichier);

		construireHeritage();
		creerLstInterface ();
	}

	/**
	 * Analyse un fichier ou un répertoire de fichiers Java.
	 * @param cheminFichier Chemin du fichier ou dossier à analyser
	 */
	private void analyserFichier(String cheminFichier)
	{
		// Analyse des fichiers
		AnalyseurFichier analyseur = new AnalyseurFichier();
		this.hashMapClasses        = analyseur.analyser(cheminFichier);

		// Génération des associations
		GenerateurAssociation generateurAssoc = new GenerateurAssociation(this.hashMapClasses);
		this.lstAssociations = generateurAssoc.generer();
	}

	/**
	 * Affiche les relations d'héritage entre les classes.
	 */
	public void construireHeritage()
	{
		for (Classe classe : hashMapClasses.values())
		{
			String nomParent = classe.getClasseParente();

			if (nomParent != null && !nomParent.isEmpty())
			{
				Classe classeParent = getClasse(nomParent);

				if (classeParent != null)
				{
					lstHeritage.add(new Heritage(classeParent, classe));
				}
			}
		}
	}

	/**
	 * Affiche les implémentations d'interfaces.
	 */
	public void afficherLstInterface()
	{
		System.out.println("--- Affichage des interfaces ---");

		for (Interface inter : lstInterface)
			System.out.println(inter);
	}

	public void creerLstInterface()
	{
		//System.out.println("--- Création des interfaces ---");
		for (Classe classe : hashMapClasses.values())
		{
			String nomInterfacesStr = classe.getNomInterface();
			
			// Vérifier que le nom n'est pas vide
			if (nomInterfacesStr != null && !nomInterfacesStr.isEmpty())
			{
				// Diviser les interfaces séparées par des virgules
				String[] tabNomInterfaces = nomInterfacesStr.split(",");
				
				for (String nomInterface : tabNomInterfaces)
				{
					nomInterface = nomInterface.replace("{", "").trim();
					Classe interfaceClasse = getClasse(nomInterface);
					//System.out.println("interface : " + interfaceClasse.getNom());

					if (interfaceClasse != null)
					{
						Interface inter = new Interface(interfaceClasse, classe);
						//System.out.println("L'interface a été créér : " + inter);
						lstInterface.add(inter);
					}
					else
					{
						System.err.println(" Interface introuvable : " + nomInterface);
					}
				}
			}
		}
	}

	/**
	 * Récupère une classe par son nom.
	 * @param nomFichier Nom du fichier (classe)
	 * @return La classe trouvée ou null
	 */
	private Classe getClasse(String nomFichier)
	{
		for (Classe classe : hashMapClasses.values())
		{
			if (classe.getNom().equals(nomFichier))
				return classe;
		}
		return null;
	}

	// ========== Getters ==========

	public HashMap  <String, Classe> getHashMapClasses	()	   { return this.hashMapClasses ; }
	public ArrayList<Association>    getLstAssociation	()	   { return this.lstAssociations; }
	public ArrayList<Heritage>       getLstHeritage   	()	   { return this.lstHeritage    ; }
	public ArrayList<Interface>      getLstInterface 	()	   { return this.lstInterface   ; }

	public static ArrayList<String> getFichiersInvalides(String cheminDossiers)
    {
		ArrayList<String> lstFichiersInvalides = new ArrayList<>();
        File projet = new File(cheminDossiers);
        //String messageInvalide = "Attention : Fichiers non valides detectés.";
        //String messageErreur = "\n( ";

        if (projet.isDirectory())
        {
            File[] tabFichiers = projet.listFiles();

            for (File file : tabFichiers)
            {
                if (file.isFile() && !file.getName().endsWith(".java"))
                {
                    lstFichiersInvalides.add(file.getName());
                }
            }
        }

		return lstFichiersInvalides;
	}
}
