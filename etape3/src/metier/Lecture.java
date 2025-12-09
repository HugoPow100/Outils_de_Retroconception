package metier;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.util.HashMap;

public class Lecture {
	private HashMap<String, ArrayList<Classe>> hashMapClasses;
	private ArrayList<Classe>      lstClasse;
	private ArrayList<Attribut>    lstAttribut;
	private ArrayList<Methode>     lstMethode;
	private ArrayList<Association> lstAssociations;
	private ArrayList<String>      lstNomFichier;

	public Lecture(String nom) 
	{
		this.hashMapClasses = new HashMap<String, ArrayList<Classe>>();
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();

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

					ArrayList<Classe> classesPourCeFichier = new ArrayList<>();
					classesPourCeFichier.add(scanne(scFic, nomFichier));

					this.hashMapClasses.put(nomFichier, classesPourCeFichier);

					scFic.close();

					// Nettoi les liste pour le prochain fichier à lire
					this.lstAttribut.clear();
					this.lstClasse.clear();
					this.lstMethode.clear();
				}

				// Nettoie la list car les chemin ne sont plus utile
				lstCheminFich.clear();
			} 
			else 
			{
				scFic = new Scanner(new FileInputStream(paraCheminFichier), "UTF8");

				Path p = Paths.get(paraCheminFichier);
				String nomFichier = String.valueOf(p.getFileName());

				ArrayList<Classe> classesPourCeFichier = new ArrayList<>();
				classesPourCeFichier.add(scanne(scFic, nomFichier));

				this.hashMapClasses.put(nomFichier, classesPourCeFichier);

				scFic.close();

				// Nettoie les liste car plus utile
				this.lstAttribut.clear();
				this.lstClasse.clear();
				this.lstMethode.clear();
			}

			/*for (String nomFichier : this.lstNomFichier)
			{
				Classe classeOrig = this.hashMapClasses.get(nomFichier).get(0);

				for(Attribut attribut : classeOrig.lstAttribut())
				{
					for(String nomFichierCheck : lstNomFichier)
					{
						if(attribut.getTypeAttribut().equals(nomFichierCheck) && !attribut.getTypeAttribut().equals(nomFichier))
						{
							Classe classeDest =  this.hashMapClasses.get(nomFichierCheck).get(0);

							Association ass = new Association (classeDest, classeOrig,Multiplicite multDest, Multiplicite multOrig, boolean unidirectionnel);
							
							this.lstAssociations.add(ass);
						}
					}
				}
			}*/


			for (String nomFichier : hashMapClasses.keySet()) 
			{
				Classe classeOrig = hashMapClasses.get(nomFichier).get(0);

				// Compter combien de fois chaque type de classe apparaît dans les attributs
				Map<String, Integer> compteur = new HashMap<>();
				for (Attribut attr : classeOrig.lstAttribut()) 
				{
					String type = nettoyerType(attr.getTypeAttribut());
					if (hashMapClasses.containsKey(type)) 
					{
						compteur.put(type, compteur.getOrDefault(type, 0) + 1);
					}
				}

				// Créer les associations avec multiplicité
				for (Map.Entry<String, Integer> entry : compteur.entrySet()) 
				{
					String typeDest   = entry.getKey();
					int    max        = entry.getValue();

					Classe classeDest = hashMapClasses.get(typeDest).get(0); // récupérer la Classe dans l'ArrayList

					Multiplicite multOrig = new Multiplicite(1, 1); // origine = 1..1
					Multiplicite multDest;

					boolean multi = estMultiInstance(a.getTypeAttribut());

					if (multi) 
					{
						multDest = new Multiplicite(0, Integer.MAX_VALUE); // 0..*
					} 
					else 
					{
						multDest = new Multiplicite(1, max); // 1..N selon occurrences
					}

					lstAssociations.add(new Association(classeDest, classeOrig, multDest, multOrig, true));
				}
			}

			// Méthode utilitaire pour nettoyer le type
			private String nettoyerType(String type) 
			{
				type = type.trim();
				if (type.endsWith("[]")) return type.substring(0, type.length() - 2);
				if (type.startsWith("List<") && type.endsWith(">")) return type.substring(5, type.length() - 1);
				if (type.startsWith("Set<") && type.endsWith(">")) return type.substring(4, type.length() - 1);
				return type;
			}

			private boolean estMultiInstance(String type) 
			{
				type = type.trim();
				return type.endsWith("[]") || type.startsWith("List<") || type.startsWith("Set<");
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

		List<Parametre> lstParametres = new ArrayList<Parametre>();

		String nomFichier = nomFichierAvExt.replace(".java", "");
		boolean isClasseAbstract = false;

		while (scFic.hasNextLine()) 
		{
			// retire les espaces en début/fin
			String ligne = scFic.nextLine().trim();

			// Détecter si la classe est abstraite
			if (ligne.contains("abstract") && ligne.contains("class")) {
				isClasseAbstract = true;
			}

			if (ligne.startsWith("private") || ligne.startsWith("protected") || ligne.startsWith("public")) 
			{
				if (ligne.contains("class") && ligne.contains("extends"))
				{
					String classeOrigine = ligne.substring(indexOf("extends")+7)
				}

				// c'est un attribut, pas une méthode
				if (!ligne.contains("(") && ligne.endsWith(";")) 
				{
					// Découper la ligne : visibilité, type, nom
					String[] ligneAttribut = ligne.split("\\s+"); // séparer par espaces
					visibiliteAtribut = ligneAttribut[0];

					if (ligneAttribut[1].equals("static")) 
					{
						portee = "classe";
						type = ligneAttribut[2];
						nom = ligneAttribut[3].replace(";", ""); // retirer le ;
					} 
					else 
					{
						portee = "instance";
						type = ligneAttribut[1];
						nom = ligneAttribut[2].replace(";", ""); // retirer le ;
					} 

					System.out.println("Le nouvel attribut a été creer : ");
					lstAttribut.add(new Attribut(nom, type, visibiliteAtribut, portee));
				}

				// Détecter les méthodes (abstraites ou non)
				if (ligne.contains("(") && ligne.contains(")") && 
				    (!ligne.endsWith(";") || ligne.contains("abstract"))) // Inclure les méthodes abstraites qui se terminent par ;
				{
					boolean isMethodeAbstract = ligne.contains("abstract");
					String[] ligneMethode = ligne.split("\\s+");
					visibilite = ligneMethode[0];

					int offset = isMethodeAbstract ? 1 : 0;

					if (ligne.contains("static")) 
					{
						portee = "classe";
						typeRetour = ligneMethode[2 + offset];
						nomMethode = ligneMethode[3 + offset].substring(0, ligneMethode[3 + offset].indexOf("("));
					} 
					else 
					{
						portee = "instance";

						if (ligne.contains(nomFichier)) 
						{
							typeRetour = "";
							nomConstructeur = "Constructeur";
						} 
						else 
						{
							typeRetour = ligneMethode[1 + offset];
							nomMethode = ligneMethode[2 + offset].substring(0, ligneMethode[2 + offset].indexOf("("));
						}

					}

					String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));

					lstParametres = new ArrayList<Parametre>(); // Réinitialiser pour chaque méthode

					if (!params.isEmpty()) 
					{
						String[] listParam = params.split(",");

						for (String p : listParam) 
						{
							String[] pTokens = p.trim().split("\\s+");
							String typeParam = pTokens[0];
							String nomParam = pTokens[1];

							parametre = new Parametre(nomParam, typeParam);

							lstParametres.add(parametre);
						}
					}

					if (!nomConstructeur.equals("")) 
					{

						System.out.println("Nouvelle méthode ajouté dans la liste");
						this.lstMethode.add(
								new Methode(nomConstructeur, typeRetour, visibilite, false, new ArrayList<>(lstParametres)));
						nomConstructeur = ""; // Réinitialiser
					} 
					else 
					{
						System.out.println("Nouvelle méthode ajouté");
						this.lstMethode
								.add(new Methode(nomMethode, typeRetour, visibilite, isMethodeAbstract, new ArrayList<>(lstParametres)));
					}
				}
			}
		}

		return new Classe(nomFichier, isClasseAbstract, this.lstAttribut, this.lstMethode);
	}

	public HashMap<String, ArrayList<Classe>> getHashMapClasses() 
	{
		return this.hashMapClasses;
	}
}