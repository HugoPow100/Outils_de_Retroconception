package metier;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.HashMap;

public class Lecture
{
	private HashMap<String, ArrayList<Classe>> hashMapClasses;
	private	ArrayList<Classe>   lstClasse   ;
	private	ArrayList<Attribut> lstAttribut ;
	private	ArrayList<Methode>  lstMethode  ;

	public Lecture(String nom)
	{
		this.hashMapClasses = new HashMap<String, ArrayList<Classe>>();
        this.lstAttribut    = new ArrayList <Attribut>();
        this.lstMethode     = new ArrayList <Methode>();

		analyserFichier(nom);
	}

	public void analyserFichier(String paraCheminFichier)
	{
		Scanner scFic;

		File f = new File(paraCheminFichier);

		List<String> lstCheminFich = new ArrayList<String>();
		this.lstClasse      = new ArrayList <Classe>();
		
		//----- Si c'est un répertoire -----
		if(f.isDirectory()) // fichier ou dossier ( dossier pour Directory ) 
		{
			this.hashMapClasses = new HashMap<>();

			//Récupére tous les fichiers du dossier
			File[] tabFichiers = f.listFiles();

			if (tabFichiers != null) 
			{
				for (File file : tabFichiers)
				{
					lstCheminFich.add(file.getAbsolutePath()); // récupere tous les chemins de chaque fichier
				}
					
				//Libére la mémoire
				tabFichiers = null;
			}
		}


		try
		{
			if( !lstCheminFich.isEmpty())
			{
				for(String chemin : lstCheminFich)
				{
					scFic = new Scanner ( new FileInputStream ( chemin ), "UTF8" );
					
					Path p            = Paths.get(chemin);
					String nomFichier = String.valueOf(p.getFileName());

					ArrayList<Classe> classesPourCeFichier = new ArrayList<>();
					classesPourCeFichier.add(scanne(scFic, nomFichier));

					this.hashMapClasses.put(nomFichier, classesPourCeFichier);

					scFic.close();
				}

				//Nettoie la list car les chemin ne sont plus utile
				lstCheminFich.clear();
			}
			else
			{
				scFic = new Scanner ( new FileInputStream ( paraCheminFichier ), "UTF8" );

				Path p            = Paths.get(paraCheminFichier);
				String nomFichier = String.valueOf(p.getFileName());

				ArrayList<Classe> classesPourCeFichier = new ArrayList<>();
				classesPourCeFichier.add(scanne(scFic, nomFichier));
				
				this.hashMapClasses.put(nomFichier, classesPourCeFichier);

				scFic.close();
			}
			
		}
		catch (Exception e){ e.printStackTrace(); }

	}

	private Classe scanne(Scanner scFic, String nomFichierAvExt)
	{
		Scanner scLigne;

		Classe    classe;
		Attribut  attribut;
		Methode   methode;
		Parametre parametre;

		String  nom;

		// Réinitialiser les listes pour chaque nouveau fichier
		this.lstAttribut = new ArrayList<Attribut>();
		this.lstMethode = new ArrayList<Methode>();

		// Variables pour stocker les attributs
		String position, nomAttribut,type,visibiliteAtribut,porteeAtribut;

		// Variables pour stocker les methodes
		String nomMethode = "";
		String visibilite, typeRetour, parametres, portee;

		// Variables pour stocker le constructeur
		String nomConstructeur = "";

		List<Parametre> lstParametres = new ArrayList<Parametre>();

		String nomFichier        = nomFichierAvExt.replace(".java","");
		boolean isClasseAbstract = false;
        boolean isInterface = false;
        boolean isRecord = false;
        boolean isEnum = false;

		while ( scFic.hasNextLine() )
		{
			// retire les espaces en début/fin
			String ligne = scFic.nextLine().trim();

			// Détecter si la classe est abstraite
			if (ligne.contains("abstract") && ligne.contains("class")) {
				isClasseAbstract = true;
			} 

            // Détecter si c'est une interface
            if (ligne.contains("interface")) {
                isInterface = true;
            }
            
            // Détecter si c'est une enum
            if (ligne.contains("enum") && ligne.contains("{")) {
                isEnum = true;
            }
            
            // Détecter les valeurs d'enum (constantes)
            if (isEnum && !ligne.isEmpty() && !ligne.contains("enum") && !ligne.equals("{") && !ligne.equals("}")) {
                // Vérifier si la ligne contient des valeurs d'enum (commence généralement par une majuscule)
                String ligneTrimmed = ligne.trim();
                // Ignorer les lignes de méthodes, attributs classiques, ou commentaires
                if (!ligneTrimmed.startsWith("//") && !ligneTrimmed.startsWith("/*") && 
                    !ligneTrimmed.startsWith("*") && !ligneTrimmed.startsWith("public") && 
                    !ligneTrimmed.startsWith("private") && !ligneTrimmed.startsWith("protected") &&
                    !ligneTrimmed.contains("(") && ligneTrimmed.matches("^[A-Z].*[,;]?$")) {
                    // Enlever la virgule ou le point-virgule à la fin
                    String valeur = ligneTrimmed.replaceAll("[,;]\\s*$", "").trim();
                    if (!valeur.isEmpty()) {
                        // Ajouter comme attribut sans type pour les enums
                        lstAttribut.add(new Attribut(valeur, "", "public", "classe"));
                    }
                }
            }
            
            // Détecter si c'est un record
            if (ligne.contains("record") && ligne.contains("(")) {
                isRecord = true;
                // Extraire les attributs du record depuis la déclaration
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
                                // Ajouter l'attribut
                                lstAttribut.add(new Attribut(nomParam, typeParam, "private", "instance"));
                                // Préparer le paramètre pour le constructeur
                                parametresConstructeur.add(new Parametre(nomParam, typeParam));
                                
                                // Générer le getter (nom de l'attribut sans "get")
                                lstMethode.add(new Methode(nomParam, typeParam, "public", false, new ArrayList<>()));
                            }
                        }
                        
                        // Ajouter le constructeur canonique
                        lstMethode.add(new Methode("Constructeur", "", "public", false, parametresConstructeur));
                    }
                    
                    // Ajouter les méthodes equals, hashCode et toString
                    lstMethode.add(new Methode("equals", "boolean", "public", false, 
                        List.of(new Parametre("obj", "Object"))));
                    lstMethode.add(new Methode("hashCode", "int", "public", false, new ArrayList<>()));
                    lstMethode.add(new Methode("toString", "String", "public", false, new ArrayList<>()));
                }
            }

			// Pour les interfaces, détecter aussi les méthodes sans modificateur explicite
			boolean ligneCommenceParModificateur = ligne.startsWith("private") || ligne.startsWith("protected") || ligne.startsWith("public");
			boolean estMethodeInterface = isInterface && ligne.contains("(") && ligne.contains(")") && ligne.endsWith(";") && !ligne.contains("{");

			if (ligneCommenceParModificateur || estMethodeInterface)
			{
				// c'est un attribut, pas une méthode
				if (!ligne.contains("(") && ligne.endsWith(";") && ligneCommenceParModificateur)
				{
					// Découper la ligne : visibilité, type, nom
					String[] ligneAttribut  = ligne.split("\\s+"); // séparer par espaces
					visibiliteAtribut       = ligneAttribut[0];

					if(ligneAttribut[1].equals("static") )
					{
						portee       = "classe";
						type         = ligneAttribut[2];
						nom          = ligneAttribut[3].replace(";", ""); // retirer le ;
					}
					else
					{
						portee       = "instance";
						type         = ligneAttribut[1];
						nom          = ligneAttribut[2].replace(";", ""); // retirer le ;
					}

				System.out.println("Le nouvel attribut a été creer : ");
				lstAttribut.add(new Attribut(nom,type,visibiliteAtribut,portee));
			}

			// Détecter les méthodes (abstraites ou non)
			if (ligne.contains("(") && ligne.contains(")") && 
			    (!ligne.endsWith(";") || ligne.contains("abstract") || estMethodeInterface)) // Inclure les méthodes abstraites et les méthodes d'interface
			{ 
				boolean isMethodeAbstract = ligne.contains("abstract") || estMethodeInterface; // Les méthodes d'interface sont abstraites
				String[] ligneMethode = ligne.split("\\s+");
				
				// Pour les interfaces, si pas de modificateur, la visibilité est "public" par défaut
				if (estMethodeInterface && !ligneCommenceParModificateur) {
					visibilite = "public";
				} else {
					visibilite = ligneMethode[0];
				}
					
				int offset = (ligne.contains("abstract") && ligneCommenceParModificateur) ? 1 : 0;
				int baseIndex = (estMethodeInterface && !ligneCommenceParModificateur) ? 0 : 1;

				if(ligne.contains("static"))
				{		
					portee     = "classe";
					typeRetour = ligneMethode[baseIndex + 1 + offset];
					nomMethode = ligneMethode[baseIndex + 2 + offset].substring(0, ligneMethode[baseIndex + 2 + offset].indexOf("("));
				}
				else
				{
					portee = "instance";
					// Ne pas détecter la déclaration du record comme un constructeur
					if(ligne.contains(nomFichier) && !estMethodeInterface && !ligne.contains("record"))
					{
						typeRetour       = "";
						nomConstructeur  = "Constructeur";
					}
					else
					{
						typeRetour = ligneMethode[baseIndex + offset];
						nomMethode = ligneMethode[baseIndex + 1 + offset].substring(0, ligneMethode[baseIndex + 1 + offset].indexOf("("));
					}
					
				}

					
					String params = ligne.substring(ligne.indexOf("(")+1, ligne.indexOf(")"));

					lstParametres = new ArrayList<Parametre>(); // Réinitialiser pour chaque méthode

					if (!params.isEmpty()) 
					{
						String[] listParam = params.split(",");

						
						for(String p : listParam)
						{
							String[] pTokens   = p.trim().split("\\s+");
							String   typeParam = pTokens[0];
							String   nomParam  = pTokens[1];

							parametre = new Parametre(nomParam, typeParam);

							lstParametres.add(parametre);
						}
					}

					if(!nomConstructeur.equals("") )
					{
					
						System.out.println("Nouvelle méthode ajouté dans la liste");
						this.lstMethode.add(new Methode(nomConstructeur,typeRetour,visibilite, false, new ArrayList<>(lstParametres)));
						nomConstructeur = ""; // Réinitialiser
					}
					else
					{
						System.out.println("Nouvelle méthode ajouté");
						this.lstMethode.add(new Methode(nomMethode,typeRetour,visibilite, isMethodeAbstract, new ArrayList<>(lstParametres)));
					}
				}
			}
		}
		
			// Ajout des paramètres isInterface, isRecord et isEnum à la classe
			return new Classe(nomFichier, isClasseAbstract, isInterface, isRecord, isEnum, this.lstAttribut, this.lstMethode);
	}

	public HashMap<String, ArrayList<Classe>> getHashMapClasses()
	{
		return this.hashMapClasses;
	}
}