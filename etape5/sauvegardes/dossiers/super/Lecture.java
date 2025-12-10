package metier;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.util.HashMap;

public class Lecture 
{
	private HashMap<String, Classe> hashMapClasses;
	private ArrayList<Classe> lstClasse;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;
	private ArrayList<Association> lstAssociations;
	private ArrayList<String> lstNomFichier;

	public Lecture(String nom) 
	{
		this.hashMapClasses = new HashMap<String, Classe>();
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		this.lstNomFichier = new ArrayList<String>();
		this.lstAssociations = new ArrayList<Association>();

		analyserFichier(nom);
	}

	public void analyserFichier(String paraCheminFichier) 
	{
		Scanner scFic;

		File f = new File(paraCheminFichier);

		List<String> lstCheminFich = new ArrayList<String>();
		this.lstClasse = new ArrayList<Classe>();

		// ----- Si c'est un répertoire -----
		if (f.isDirectory()) // fichier ou dossier ( dossier pour Directory )
		{
			this.hashMapClasses = new HashMap<>();

			// Récupére tous les fichiers du dossier
			File[] tabFichiers = f.listFiles();

			if (tabFichiers != null)
			{
				for (File file : tabFichiers) 
				{
					lstCheminFich.add(file.getAbsolutePath()); // récupere tous les chemins de chaque fichier

					Path p = file.toPath();
					String nomFichier = String.valueOf(p.getFileName()).replace(".java", "");

					this.lstNomFichier.add(nomFichier);
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

					Path p = Paths.get(chemin);
					String nomFichier = String.valueOf(p.getFileName());

					Classe classePourCeFichier;
					classePourCeFichier = scanne(scFic, nomFichier);

				this.hashMapClasses.put(nomFichier, classePourCeFichier);

				scFic.close();
			}				// Nettoie la list car les chemin ne sont plus utile
				lstCheminFich.clear();
			} 
			else 
			{
				scFic = new Scanner(new FileInputStream(paraCheminFichier), "UTF8");

				Path p = Paths.get(paraCheminFichier);
				String nomFichier = String.valueOf(p.getFileName());

				Classe classePourCeFichier;
				classePourCeFichier = scanne(scFic, nomFichier);

				this.hashMapClasses.put(nomFichier, classePourCeFichier);

				scFic.close();
			}

			genererAssociation();

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}


	private Classe scanne(Scanner scFic, String nomFichierAvExt) {
		Scanner scLigne;

		Classe classe;
		Attribut attribut;
		Methode methode;
		Parametre parametre;

		String nom;

		// Réinitialiser les listes pour chaque nouveau fichier
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();

		// Variables pour stocker les attributs
		String position, nomAttribut, type, visibiliteAtribut, porteeAtribut;

		// Variables pour stocker les methodes
		String nomMethode = "";
		String visibilite, typeRetour, parametres, portee;

		// Variables pour stocker le constructeur
		String nomConstructeur = "";

		List<Parametre> lstParametres = new ArrayList<Parametre>();

		String nomFichier = nomFichierAvExt.replace(".java", "");
		boolean isClasseAbstract = false;

		while (scFic.hasNextLine()) {
			// retire les espaces en début/fin
			String ligne = scFic.nextLine().trim();

			// Détecter si la classe est abstraite
			if (ligne.contains("abstract") && ligne.contains("class")) {
				isClasseAbstract = true;
			}

			if (ligne.startsWith("private") || ligne.startsWith("protected") || ligne.startsWith("public")) {
				if (ligne.contains("class") && ligne.contains("extends")) {
					String classeOrigine = ligne.substring(ligne.indexOf("extends") + 7).trim();
					// TODO: gérer l'héritage
				}

				// c'est un attribut, pas une méthode
				if (!ligne.contains("(") && ligne.endsWith(";")) {
					// Découper la ligne : visibilité, type, nom
					Scanner scAttribut = new Scanner(ligne);
					visibiliteAtribut = scAttribut.next();

					String motSuivant = scAttribut.next();
					if (motSuivant.equals("static")) {
						portee = "classe";
						type = scAttribut.next();
						nom = scAttribut.next().replace(";", ""); // retirer le ;
					} else {
						portee = "instance";
						type = motSuivant;
						nom = scAttribut.next().replace(";", ""); // retirer le ;
					}
					scAttribut.close();

					System.out.println("Le nouvel attribut a été creer : ");
					lstAttribut.add(new Attribut(nom, type, visibiliteAtribut, portee));
				}

				// Détecter les méthodes (abstraites ou non)
				if (ligne.contains("(") && ligne.contains(")") &&
						(!ligne.endsWith(";") || ligne.contains("abstract"))) // Inclure les méthodes abstraites qui se
																	// terminent par ;
				{
					boolean isMethodeAbstract = ligne.contains("abstract");
					Scanner scMethode = new Scanner(ligne);
					visibilite = scMethode.next();

					if (isMethodeAbstract) {
						scMethode.next(); // skip "abstract"
					}

					String motSuivant = scMethode.next();
					if (motSuivant.equals("static")) {
						portee = "classe";
						typeRetour = scMethode.next();
						String methodAvecParam = scMethode.next();
						nomMethode = methodAvecParam.substring(0, methodAvecParam.indexOf("("));
					} else {
						portee = "instance";

						if (ligne.contains(nomFichier)) {
							typeRetour = "";
							nomConstructeur = "Constructeur";
						} else {
							typeRetour = motSuivant;
							String methodAvecParam = scMethode.next();
							nomMethode = methodAvecParam.substring(0, methodAvecParam.indexOf("("));
						}

					}
					scMethode.close();					String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));

					lstParametres = new ArrayList<Parametre>(); // Réinitialiser pour chaque méthode

					if (!params.isEmpty()) {
						Scanner scParams = new Scanner(params);
						scParams.useDelimiter(",");

						while (scParams.hasNext()) {
							String paramStr = scParams.next().trim();
							Scanner scParam = new Scanner(paramStr);
							
							if (scParam.hasNext()) {
								String typeParam = scParam.next();
								if (scParam.hasNext()) {
									String nomParam = scParam.next();
									parametre = new Parametre(nomParam, typeParam);
									lstParametres.add(parametre);
								} else {
									System.out.println("Paramètre ignoré (format inattendu): " + paramStr);
								}
							}
							scParam.close();
						}
						scParams.close();
					}

					if (!nomConstructeur.equals("")) {

						System.out.println("Nouvelle méthode ajouté dans la liste");
						this.lstMethode.add(
								new Methode(nomConstructeur, typeRetour, visibilite, false,
										new ArrayList<>(lstParametres)));
						nomConstructeur = ""; // Réinitialiser
					} else {
						System.out.println("Nouvelle méthode ajouté");
						this.lstMethode
								.add(new Methode(nomMethode, typeRetour, visibilite, isMethodeAbstract,
										new ArrayList<>(lstParametres)));
					}
				}
			}
		}

		return new Classe(nomFichier, isClasseAbstract, this.lstAttribut, this.lstMethode);
	}


	private void genererAssociation()
	{
		for (String nomFichier : hashMapClasses.keySet()) 
		{
			Classe classeOrig = hashMapClasses.get(nomFichier);

			// Compteur : clé = type nettoyé, valeur = nb occurrences				
			Map<String, Integer> compteur = new HashMap<>();
			
			// Array pour savoir si ce type était multi-instance
			ArrayList<String> listeMultiInstance = new ArrayList<>();

			for (Attribut attr : classeOrig.getLstAttribut()) 
			{
				String typeOriginal = attr.getTypeAttribut().trim();
				String typeNettoye  = nettoyerType(typeOriginal).trim();
				
				/*if (!hashMapClasses.containsKey(typeNettoye))
							continue;*/

				if (estMultiInstance(typeOriginal)) 
				{
					// Ajoute tel quel
					listeMultiInstance.add(typeNettoye);
				}
				else // Simple instance
				{
					compteur.put(typeNettoye, compteur.getOrDefault(typeNettoye, 0) + 1);
				}
				
			}

			// -------- MULTI-INSTANCE --------
			for (String typeDest : listeMultiInstance) 
			{
				// Vérifier que la classe existe dans la HashMap
				if (!hashMapClasses.containsKey(typeDest + ".java")) {
					continue; // Ignorer si la classe n'existe pas
				}

				Classe classeDest     = hashMapClasses.get(typeDest + ".java");

				Multiplicite multOrig = new Multiplicite(1,1);
				Multiplicite multDest = new Multiplicite(0, "*");  // valeur par défaut


				// Si la classe apparaît en paramètre d'une méthode => multiplicité 1..*
				for (Methode methode : classeOrig.getLstMethode()) 
				{
					for (Parametre param : methode.getLstParametre()) 
					{
						// comparer le type paramètre avec le nom de classe destination
						if (nettoyerType(param.getTypePara()).equals(typeDest)) 
						{
							multDest = new Multiplicite(1, "*");
						}
					}
				}

				// Vérifier si la classe destination référence aussi la classe origine
				boolean bidirectionnel = false;
				for (Attribut attrDest : classeDest.getLstAttribut()) 
				{
					String typeAttrDest = nettoyerType(attrDest.getTypeAttribut().trim());
					if (typeAttrDest.equals(classeOrig.getNom())) 
					{
						bidirectionnel = true;
						break;
					}
				}
				
				// Vérifier aussi dans les paramètres des méthodes de la classe destination
				if (!bidirectionnel) 
				{
					for (Methode methodeDest : classeDest.getLstMethode()) 
					{
						for (Parametre paramDest : methodeDest.getLstParametre()) 
						{
							if (nettoyerType(paramDest.getTypePara()).equals(classeOrig.getNom())) 
							{
								bidirectionnel = true;
								break;
							}
						}
						if (bidirectionnel) break;
					}
				}

			lstAssociations.add(new Association(
				classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
			}

			// -------- SIMPLE INSTANCE --------
			for (Map.Entry<String,Integer> entry : compteur.entrySet()) 
			{

				String   typeDest   = entry.getKey();
				int      max        = entry.getValue();

				// Vérifier que la classe existe dans la HashMap
				if (!hashMapClasses.containsKey(typeDest + ".java")) {
					continue; // Ignorer si la classe n'existe pas
				}

				Classe   classeDest = hashMapClasses.get(typeDest + ".java");				
				Multiplicite multOrig  = new Multiplicite(1,1);
				Multiplicite multDest  = new Multiplicite(1, max);

				// Vérifier si la classe destination référence aussi la classe origine
				boolean bidirectionnel = false;
				for (Attribut attrDest : classeDest.getLstAttribut()) 
				{
					String typeAttrDest = nettoyerType(attrDest.getTypeAttribut().trim());
					if (typeAttrDest.equals(classeOrig.getNom())) {
						bidirectionnel = true;
						break;
					}
				}
				// Vérifier aussi dans les paramètres des méthodes de la classe destination
				if (!bidirectionnel) {
					for (Methode methodeDest : classeDest.getLstMethode()) {
						for (Parametre paramDest : methodeDest.getLstParametre()) {
							if (nettoyerType(paramDest.getTypePara()).equals(classeOrig.getNom())) {
								bidirectionnel = true;
								break;
							}
						}
						if (bidirectionnel) break;
					}
				}

				lstAssociations.add(new Association(
					classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
			}
			nettoyerAssociations();
		}
	}

	/**
	 * Nettoie la liste des associations pour :
	 * - supprimer les doublons (ex : A→B et B→A)
	 * - transformer ces doublons en une seule association bidirectionnelle A↔B
	 * - conserver une seule association unique par couple de classes
	*/
	private void nettoyerAssociations() 
	{
		// Cette Map va contenir UNE seule association par paire de classes
		Map<String, Association> uniques = new HashMap<>();

		for (Association asso : lstAssociations) 
		{

			String origine = asso.getClasseOrig().getNom();
			String dest    = asso.getClasseDest().getNom();

			String key;

			if (origine.compareTo(dest) < 0) 
				key = origine + "-" + dest;
			else
				key = dest + "-" + origine;
		

			if (!uniques.containsKey(key)) 
			{
				// Première association → on la garde
				uniques.put(key, asso);
			}
			else 
			{
				// Une association opposée existe déjà
				Association exist = uniques.get(key);
			}
		}

		// On remplace la liste par la liste unique
		lstAssociations = new ArrayList<>(uniques.values());
	}


	




	/**
	 * Nettoie le type pour retirer List<>, Set<> ou [].
	 */
	private String nettoyerType(String type) 
	{
		type = type.trim();
		if (type.endsWith("[]")) return type.substring(0, type.length() - 2);
		if (type.startsWith("List<") && type.endsWith(">")) return type.substring(5, type.length() - 1);
		if (type.startsWith("Set<") && type.endsWith(">")) return type.substring(4, type.length() - 1);
		return type;
	}

	/**
	 * Retourne true si le type peut contenir plusieurs instances (tableau ou collection).
	 */
	private boolean estMultiInstance(String type) 
	{
		type = type.trim();
		return type.endsWith("[]") || type.startsWith("List<") || type.startsWith("Set<");
	}

	public HashMap<String, Classe> getHashMapClasses() 
	{
		// System.out.println(this.hashMapClasses.values());
		return this.hashMapClasses;
	}

	public ArrayList<Association> getLstAssociation()
	{
		return this.lstAssociations;
	}
}