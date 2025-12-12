package metier;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.HashMap;

public class Lecture
{
	private HashMap<String, Classe> hashMapClasses;
	private ArrayList<Classe> lstClasse;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;
	private ArrayList<Heritage> lstHeritage;
	private ArrayList<Association> lstAssociations;
	private ArrayList<String> lstNomFichier;

	public Lecture(String nom) {
		this.hashMapClasses = new HashMap<String, Classe>();
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();
		this.lstHeritage = new ArrayList<Heritage>();
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

			for (Classe classe : hashMapClasses.values())
			{
				String nomParent = classe.getClasseParente();

				if (nomParent != null && !nomParent.isEmpty())
				{
					Classe classParent = getClasse(nomParent);

					if (classParent != null)
					{
						Heritage heritage = new Heritage(classParent, classe);
						lstHeritage.add(heritage);

						System.out.println(heritage);
					}
					// Cette classe a bien une classe parente
					// Maintenant cherche la classe parente et crée Heritage
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}


	private Classe scanne(Scanner scFic, String nomFichierAvExt)
		{
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

		// Variable pour l'heritage
		String classeParente = null;
		
		List<Parametre> lstParametres = new ArrayList<Parametre>();

		String nomFichier = nomFichierAvExt.replace(".java", "");
		boolean isHeritage = false;
		String nomClasse = nomFichier;
		String typeClasse = "";

		// Variables implements
		String interfaces = "";

		System.out.println("-----------------------------------------------------");
		System.out.println("On est dans la classe " + nomFichier);

		while (scFic.hasNextLine())
		{
			// retire les espaces en début/fin
			String ligne = scFic.nextLine().trim();

			if (ligne.contains("class") && ligne.contains("extends"))
			{
				typeClasse += "extends ";
				
				isHeritage = true ;
				
				System.out.println("DEBUG ligne détectée héritage : " + ligne);
				String classeOrigine = ligne.substring(ligne.indexOf("extends") + 7).trim();
				// classeParente = classeOrigine.split("\\s+")[0];
				Scanner scParent = new Scanner(classeOrigine);
				scParent.useDelimiter("\\s+");
				if (scParent.hasNext())
				{
					classeParente = scParent.next();
				}
				scParent.close();
			}

			if (ligne.contains("class") && ligne.contains("implements"))
			{

				interfaces = ligne.substring(ligne.indexOf("implements") + 10).trim();

				typeClasse += "implements ";
			}

			// Détecter si la classe est abstraite
			if (ligne.contains("abstract") && ligne.contains("class")) {
				System.out.println("ligne abstract : " + ligne);
				typeClasse += "abstract ";
			}

			// Détecter si c'est une interface
			if (ligne.contains("interface")) {
				typeClasse += "interface ";
			}

			// Détecter si c'est une enum
			if (ligne.contains("enum") && ligne.contains("{")) {
				typeClasse += "enum ";
			}

			// Détecter les valeurs d'enum (constantes)
			if (typeClasse.contains("enum") && !ligne.isEmpty() && !ligne.contains("enum") && !ligne.equals("{") && !ligne.equals("}")) {
				String ligneTrimmed = ligne.trim();
				if (!ligneTrimmed.startsWith("//") && !ligneTrimmed.startsWith("/*") && 
					!ligneTrimmed.startsWith("*") && !ligneTrimmed.startsWith("public") && 
					!ligneTrimmed.startsWith("private") && !ligneTrimmed.startsWith("protected") &&
					!ligneTrimmed.contains("(") && ligneTrimmed.matches("^[A-Z].*[,;]?$")) {
					String valeur = ligneTrimmed.replaceAll("[,;]\\s*$", "").trim();
					if (!valeur.isEmpty()) {
						lstAttribut.add(new Attribut(valeur, "", "public", "classe", true));
					}
				}
			}

			// Détecter si c'est un record
			if (ligne.contains("record") && ligne.contains("(")) {
				typeClasse += "record ";
				int debutParam = ligne.indexOf("(");
				int finParam = ligne.indexOf(")");
				if (debutParam != -1 && finParam != -1 && finParam > debutParam) {
					String params = ligne.substring(debutParam + 1, finParam).trim();
					List<Parametre> parametresConstructeur = new ArrayList<>();
					
					if (!params.isEmpty()) {
						String[] listParam = params.split(",");
						for (String p : listParam) {
							String[] pTokens = p.trim().split("\\s+");
							if (pTokens.length >= 2) {
								String typeParam = pTokens[0];
								String nomParam = pTokens[1];
							lstAttribut.add(new Attribut(nomParam, typeParam, "private", "instance", false));
								parametresConstructeur.add(new Parametre(nomParam, typeParam));
								lstMethode.add(new Methode(nomParam, typeParam, "public", false, new ArrayList<>()));
							}
						}
						
						lstMethode.add(new Methode("Constructeur", "", "public", false, parametresConstructeur));
					}
					
					lstMethode.add(new Methode("equals", "boolean", "public", false, 
						List.of(new Parametre("obj", "Object"))));
					lstMethode.add(new Methode("hashCode", "int", "public", false, new ArrayList<>()));
					lstMethode.add(new Methode("toString", "String", "public", false, new ArrayList<>()));
				}
			}

			// Pour les interfaces, détecter aussi les méthodes sans modificateur explicite
			boolean ligneCommenceParModificateur = ligne.startsWith("private") || ligne.startsWith("protected") || ligne.startsWith("public");
			boolean estMethodeInterface = typeClasse.contains("interface") && ligne.contains("(") && ligne.contains(")") && ligne.endsWith(";") && !ligne.contains("{");

			if (ligneCommenceParModificateur || estMethodeInterface) {
				// c'est un attribut, pas une méthode
				if (!ligne.contains("(") && ligne.endsWith(";") && ligneCommenceParModificateur) {
					// Découper la ligne : visibilité, type, nom
					Scanner scAttribut = new Scanner(ligne);
					visibiliteAtribut = scAttribut.next();

					boolean isFinal = false;
					boolean isStatic = false;
					String motSuivant = scAttribut.next();
					
					// Détecter final et/ou static
					while (motSuivant.equals("final") || motSuivant.equals("static")) {
						if (motSuivant.equals("final")) {
							isFinal = true;
						}
						if (motSuivant.equals("static")) {
							isStatic = true;
						}
						if (scAttribut.hasNext()) {
							motSuivant = scAttribut.next();
						} else {
							break;
						}
					}
					
					portee = isStatic ? "classe" : "instance";
					type = motSuivant;
					
					// Si le type contient des génériques (comme HashMap<String, Point>)
					// il faut lire jusqu'au > de fermeture
					while (type.contains("<") && !type.contains(">")) {
						type += " " + scAttribut.next();
					}
					
					nom = scAttribut.next().replace(";", ""); // retirer le ;
					// Extraire juste le nom sans l'initialisation (ex: MAX_SIZE = 100)
					if (nom.contains("=")) {
						nom = nom.substring(0, nom.indexOf("=")).trim();
					}
					
					scAttribut.close();

					// Déterminer si c'est une constante : final = {frozen}
					boolean isConstant = isFinal;

					System.out.println("Le nouvel attribut a été creer : ");
					lstAttribut.add(new Attribut(nom, type, visibiliteAtribut, portee, isConstant));
				}

				// Détecter les méthodes (abstraites ou non)
				if (ligne.contains("(") && ligne.contains(")") &&
						(!ligne.endsWith(";") || ligne.contains("abstract") || estMethodeInterface)) // Inclure les méthodes abstraites et les méthodes d'interface
				{
					boolean isMethodeAbstract = ligne.contains("abstract") || estMethodeInterface;
					boolean isClasseHeritage = ligne.contains("extends");

					Scanner scMethode = new Scanner(ligne);
					
					// Pour les interfaces, si pas de modificateur, la visibilité est "public" par défaut
					if (estMethodeInterface && !ligneCommenceParModificateur) {
						visibilite = "public";
					} else {
						visibilite = scMethode.next();
					}

					System.out.println(ligne);
					// System.out.println("AbstractAbstractAbstractAbstractAbstractAbstractAbstract");
					
					if (ligne.contains("abstract") && ligneCommenceParModificateur) {
						scMethode.next(); // skip "abstract"
					}

					if (isClasseHeritage)
					{
						scMethode.next();
					}

					String motSuivant = "";
					if (scMethode.hasNext()) {
						motSuivant = scMethode.next();
					}
					
					if (motSuivant.equals("static")) {
						portee = "classe";
						typeRetour = scMethode.next();
						if (scMethode.hasNext()) {
							String methodAvecParam = scMethode.next();
							int indexParen = methodAvecParam.indexOf("(");
							if (indexParen != -1) {
								nomMethode = methodAvecParam.substring(0, indexParen);
							} else {
								nomMethode = methodAvecParam;
							}
						}
					} else {
						portee = "instance";

						if (ligne.contains(nomFichier) && !ligne.contains("record")) {
							typeRetour = "";
							nomConstructeur = "Constructeur";
						} else {
							typeRetour = motSuivant;
							if (scMethode.hasNext()) {
								String methodAvecParam = scMethode.next();
								int indexParen = methodAvecParam.indexOf("(");
								if (indexParen != -1) {
									nomMethode = methodAvecParam.substring(0, indexParen);
								} else {
									nomMethode = methodAvecParam;
								}
							}
						}

					}
					scMethode.close();					String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));

					lstParametres = new ArrayList<Parametre>(); // Réinitialiser pour chaque méthode

					if (!params.isEmpty()) {
						// Découper les paramètres en tenant compte des génériques
						ArrayList<String> paramsListe = new ArrayList<>();
						int niveau = 0;
						StringBuilder paramActuel = new StringBuilder();
						
						for (int i = 0; i < params.length(); i++) {
							char c = params.charAt(i);
							
							if (c == '<') {
								niveau++;
								paramActuel.append(c);
							} else if (c == '>') {
								niveau--;
								paramActuel.append(c);
							} else if (c == ',' && niveau == 0) {
								// Virgule hors des génériques : nouveau paramètre
								paramsListe.add(paramActuel.toString().trim());
								paramActuel = new StringBuilder();
							} else {
								paramActuel.append(c);
							}
						}
						// Ajouter le dernier paramètre
						if (paramActuel.length() > 0) {
							paramsListe.add(paramActuel.toString().trim());
						}
						
						// Analyser chaque paramètre
						for (String paramStr : paramsListe) {
							Scanner scParam = new Scanner(paramStr);
							
							if (scParam.hasNext()) {
								String typeParam = scParam.next();
								
								// Si le type contient des génériques (comme HashMap<String, Point>)
								// il faut lire jusqu'au > de fermeture
								while (typeParam.contains("<") && !typeParam.contains(">") && scParam.hasNext()) {
									typeParam += " " + scParam.next();
								}
								
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

		//System.out.println("val isHeritage : " + isHeritage);
		typeClasse = typeClasse.trim().replace(" ","," );
		System.out.println("typeClasse : " + typeClasse);
		return new Classe(nomFichier, classeParente, typeClasse, isHeritage, interfaces, lstAttribut, this.lstMethode);
	}

	private void genererAssociation()
	{
		ArrayList<Attribut> attributsARetirer = new ArrayList<>();

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
					attributsARetirer.add(attr);
				}
				else // Simple instance
				{
					compteur.put(typeNettoye, compteur.getOrDefault(typeNettoye, 0) + 1);
					if (hashMapClasses.containsKey(typeNettoye + ".java")) {
						attributsARetirer.add(attr);
					}
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
				nettoyerAssociations();
			// SUPPRESSION DES ATTRIBUTS DE RELATION
			classeOrig.getLstAttribut().removeAll(attributsARetirer);
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

	// public Classe getClasseImplements()
	// {
	// 	Classe classeImplements = null;
	// 	for (Classe classe : hashMapClasses.values())
	// 	{
	// 		for (Methode methode : this.lstMethode)
	// 		{
	// 			if (!methode.getListeInterfaces().isEmpty())
	// 			{

	// 			}
	// 		}
	// 	}
	// }
//
	// Recupere une classe en fonction du nom
	private Classe getClasse(String nomFichier)
	{
		for (Classe classe : hashMapClasses.values())
		{
			if (classe.getNom().equals(nomFichier))
				return classe;
		}
		return null;
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

	public ArrayList<Heritage> getLstHeritage()
	{
		return this.lstHeritage;
	}
}