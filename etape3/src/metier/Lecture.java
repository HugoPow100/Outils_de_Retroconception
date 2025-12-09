package metier;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.util.HashMap;

public class Lecture {
	private HashMap<String, Classe> hashMapClasses;
	private ArrayList<Classe> lstClasse;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;
	private ArrayList<Association> lstAssociations;
	private ArrayList<String> lstNomFichier;

	public Lecture(String nom) {
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
					String[] ligneAttribut = ligne.split("\\s+"); // séparer par espaces
					visibiliteAtribut = ligneAttribut[0];

					if (ligneAttribut[1].equals("static")) {
						portee = "classe";
						type = ligneAttribut[2];
						nom = ligneAttribut[3].replace(";", ""); // retirer le ;
					} else {
						portee = "instance";
						type = ligneAttribut[1];
						nom = ligneAttribut[2].replace(";", ""); // retirer le ;
					}

					System.out.println("Le nouvel attribut a été creer : ");
					lstAttribut.add(new Attribut(nom, type, visibiliteAtribut, portee));
				}

				// Détecter les méthodes (abstraites ou non)
				if (ligne.contains("(") && ligne.contains(")") &&
						(!ligne.endsWith(";") || ligne.contains("abstract"))) // Inclure les méthodes abstraites qui se
																				// terminent par ;
				{
					boolean isMethodeAbstract = ligne.contains("abstract");
					String[] ligneMethode = ligne.split("\\s+");
					visibilite = ligneMethode[0];

					int offset = isMethodeAbstract ? 1 : 0;

					if (ligne.contains("static")) {
						portee = "classe";
						typeRetour = ligneMethode[2 + offset];
						nomMethode = ligneMethode[3 + offset].substring(0, ligneMethode[3 + offset].indexOf("("));
					} else {
						portee = "instance";

						if (ligne.contains(nomFichier)) {
							typeRetour = "";
							nomConstructeur = "Constructeur";
						} else {
							typeRetour = ligneMethode[1 + offset];
							nomMethode = ligneMethode[2 + offset].substring(0, ligneMethode[2 + offset].indexOf("("));
						}

					}

					String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));

					lstParametres = new ArrayList<Parametre>(); // Réinitialiser pour chaque méthode

					if (!params.isEmpty()) {
						String[] listParam = params.split(",");

						for (String p : listParam) {
							String[] pTokens = p.trim().split("\\s+");
							if (pTokens.length >= 2) {
								String typeParam = pTokens[0];
								String nomParam = pTokens[1];
								parametre = new Parametre(nomParam, typeParam);
								lstParametres.add(parametre);
							} else {
								System.out.println("Paramètre ignoré (format inattendu): " + p);
							}
						}
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
				for (Attribut attrDest : classeDest.getLstAttribut()) {
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
				for (Attribut attrDest : classeDest.getLstAttribut()) {
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
		}
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