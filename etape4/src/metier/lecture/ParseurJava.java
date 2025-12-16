package metier.lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import metier.objet.*;

/**
 * Classe responsable du parsing d'un fichier Java.
 * Analyse le contenu d'un fichier et crée un objet Classe.
 */
public class ParseurJava
{
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode>  lstMethode;

	public ParseurJava()
	{
		this.lstAttribut = new ArrayList<>();
		this.lstMethode  = new ArrayList<>();
	}

	/**
	 * Parse un fichier Java et retourne un objet Classe.
	 * @param scFic           Scanner du fichier à parser
	 * @param nomFichierAvExt Nom du fichier avec extension
	 * @return                Objet Classe créé
	 */
	public Classe parser(Scanner scFic, String nomFichierAvExt)
	{
		// Réinitialiser les listes pour chaque nouveau fichier
		this.lstAttribut = new ArrayList<>();
		this.lstMethode  = new ArrayList<>();

		// Variables pour stocker les informations de la classe
		String  nomFichier    = nomFichierAvExt.replace(".java", "");
		String  classeParente = null;
		boolean isHeritage    = false;
		String  typeClasse    = "";
		String  interfaces    = "";

		// Variables pour le parsing des méthodes
		String          nomMethode      = "";
		String          nomConstructeur = "";
		List<Parametre> lstParametres   = new ArrayList<>();

		while (scFic.hasNextLine())
		{
			String ligne = scFic.nextLine().trim();

			// Détecter l'héritage
			if (ligne.contains("class") && ligne.contains("extends"))
			{
				typeClasse += "extends ";
				isHeritage = true;
				
				String  classeOrigine = ligne.substring(ligne.indexOf("extends") + 7).trim();
				Scanner scParent      = new Scanner(classeOrigine);
				scParent.useDelimiter("\\s+");
				if (scParent.hasNext())
					classeParente = scParent.next();
				scParent.close();
			}

			// Détecter l'implémentation d'interface
			if (ligne.contains("class") && ligne.contains("implements"))
			{
				interfaces = ligne.substring(ligne.indexOf("implements") + 10).trim();
				typeClasse += "implements ";
			}

			// Détecter le type de classe
			if (ligne.contains("abstract") && ligne.contains("class")) {typeClasse += "abstract ";}

			if (ligne.contains("interface")) {typeClasse += "interface ";}

		// Support des conventions K&R et Allman pour les enums
		if (ligne.contains("enum")) {typeClasse += "enum ";}

			// Traiter les enums
			if (typeClasse.contains("enum") && !ligne.isEmpty() && !ligne.contains("enum") && 
			    !ligne.equals("{") && !ligne.equals("}"))
			{
				String ligneTrimmed = ligne.trim();
				if (!ligneTrimmed.startsWith("//") && !ligneTrimmed.startsWith("/*") && 
					!ligneTrimmed.startsWith("*") && !ligneTrimmed.startsWith("public") && 
					!ligneTrimmed.startsWith("private") && !ligneTrimmed.startsWith("protected") &&
					!ligneTrimmed.contains("(") && ligneTrimmed.matches("^[A-Z].*[,;]?$"))
				{
					String valeur = ligneTrimmed.replaceAll("[,;]\\s*$", "").trim();
					if (!valeur.isEmpty())
						lstAttribut.add(new Attribut(valeur, "", "public", "classe", true));
					
				}
			}

			// Traiter les records
			if (ligne.contains("record") && ligne.contains("("))
			{
				typeClasse += "record ";
				traiterRecord(ligne);
			}

			// Parser les membres (attributs et méthodes)
			boolean ligneCommenceParModificateur = ligne.startsWith("private")   || 
			                                       ligne.startsWith("protected") || 
			                                        ligne.startsWith("public");
													
			boolean estMethodeInterface = typeClasse.contains("interface") && 
										  			   ligne.contains("(") && 
			                              			   ligne.contains(")") && 
										               ligne.endsWith(";") && 
										               !ligne.contains("{");

			if (ligneCommenceParModificateur || estMethodeInterface)
			{
				// Parser attribut
				if (!ligne.contains("(") && ligne.endsWith(";") && ligneCommenceParModificateur)
				{
					parserAttribut(ligne);
				}

				// Parser méthode
				boolean estAppelMethode = ligne.trim().startsWith("super(") || 
				                          ligne.trim().startsWith("this(") || 
				                          ligne.trim().startsWith("return ");
				
				if (ligne.contains("(") && ligne.contains(")") && !estAppelMethode &&
				   (!ligne.endsWith(";") || ligne.contains("abstract")             ||
					estMethodeInterface || ligne.contains("{")))
				{
					nomMethode = "";
					nomConstructeur = "";
					lstParametres = new ArrayList<>();
					
					parserMethode(ligne, nomFichier, typeClasse, ligneCommenceParModificateur, 
					              estMethodeInterface, nomMethode, nomConstructeur, lstParametres);
				}
			}
		}

		typeClasse = typeClasse.trim().replace(" ", ",");
		return new Classe(nomFichier, classeParente, typeClasse, isHeritage, 
		                  interfaces, lstAttribut, this.lstMethode);
	}

	/**
	 * Traite une déclaration de record.
	 */
	private void traiterRecord(String ligne)
	{
		int debutParam = ligne.indexOf("(");
		int finParam = ligne.indexOf(")");
		
		if (debutParam != -1 && finParam != -1 && finParam > debutParam)
		{
			String params = ligne.substring(debutParam + 1, finParam).trim();
			List<Parametre> parametresConstructeur = new ArrayList<>();
			
			if (!params.isEmpty())
			{
				String[] listParam = params.split(",");
				for (String p : listParam)
				{
					String[] pTokens = p.trim().split("\\s+");
					if (pTokens.length >= 2)
					{
						String typeParam = pTokens[0];
						String nomParam = pTokens[1];
						lstAttribut.add(new Attribut(nomParam, typeParam, 
						                             "private", "instance", false));
						parametresConstructeur.add(new Parametre(nomParam, typeParam));
						lstMethode.add(new Methode(nomParam, typeParam, "public", 
						                           false, new ArrayList<>()));
					}
				}
				
				lstMethode.add(new Methode("Constructeur", "", "public", 
				                           false, parametresConstructeur));
			}
			
			lstMethode.add(new Methode("equals", "boolean", "public", false, 
			              List.of(new Parametre("obj", "Object"))));
			lstMethode.add(new Methode("hashCode", "int", "public", 
			                           false, new ArrayList<>()));
			lstMethode.add(new Methode("toString", "String", "public", 
			                           false, new ArrayList<>()));
		}
	}

	/**
	 * Parse une déclaration d'attribut.
	 */
	private void parserAttribut(String ligne)
	{
		Scanner scAttribut = new Scanner(ligne);
		String visibiliteAtribut = scAttribut.next();

		boolean isFinal = false;
		boolean isStatic = false;
		String motSuivant = scAttribut.next();
		
		// Détecter final et/ou static
		while (motSuivant.equals("final") || motSuivant.equals("static"))
		{
			if (motSuivant.equals("final")) {isFinal = true;}
			if (motSuivant.equals("static")) {isStatic = true;}
			if (scAttribut.hasNext()) {motSuivant = scAttribut.next();} 
			else 
			{
				break;
			}
		}
		
		String portee = isStatic ? "classe" : "instance";
		String type = motSuivant;
		
		// Si le type contient des génériques
		while (type.contains("<") && !type.contains(">"))
		{
			type += " " + scAttribut.next();
		}
		
		String nom = scAttribut.next().replace(";", "");
		// Extraire juste le nom sans l'initialisation
		if (nom.contains("=")) {nom = nom.substring(0, nom.indexOf("=")).trim();}
		
		scAttribut.close();

		boolean isConstant = isFinal;
		lstAttribut.add(new Attribut(nom, type, visibiliteAtribut, portee, isConstant));
	}

	/**
	 * Parse une déclaration de méthode ou constructeur.
	 */
	private void parserMethode(String ligne, String nomFichier, String typeClasse, 
	                           boolean ligneCommenceParModificateur, boolean estMethodeInterface,
	                           String nomMethode, String nomConstructeur, 
							   List<Parametre> lstParametres)
	{
		boolean isMethodeAbstract = ligne.contains("abstract") || estMethodeInterface;
		boolean isClasseHeritage = ligne.contains("extends");

		Scanner scMethode = new Scanner(ligne);
		
		// Déterminer la visibilité
		String visibilite;
		if (estMethodeInterface && !ligneCommenceParModificateur) 
		{
			visibilite = "public";
		} 
		
		else 
		{
			visibilite = scMethode.next();
		}

		if (ligne.contains("abstract") && ligneCommenceParModificateur) 
		{
			scMethode.next(); // skip "abstract"
		}

		if (isClasseHeritage) {scMethode.next();}

		String motSuivant = "";
		if (scMethode.hasNext()) {motSuivant = scMethode.next();}
		
		String typeRetour;
		String portee;
		
		// Traiter les méthodes static
		if (motSuivant.equals("static"))
		{
			portee = "classe";
			typeRetour = scMethode.next();
			if (scMethode.hasNext()) 
			{
				String methodAvecParam = scMethode.next();
				int indexParen = methodAvecParam.indexOf("(");
				if (indexParen != -1) 
				{
					nomMethode = methodAvecParam.substring(0, indexParen);
				} 
				else 
				{
					nomMethode = methodAvecParam;
				}
			}
		}
		else
		{
			portee = "instance";

			// Cas où le type de retour et le nom de la méthode sont collés
			if (motSuivant.contains("("))
			{
				int indexParen = motSuivant.indexOf("(");
				String nomMethodeTemp = motSuivant.substring(0, indexParen);
				
				// Vérifier si c'est un constructeur
				if (nomMethodeTemp.equals(nomFichier) && !ligne.contains("record")) 
				{
					typeRetour = "";
					nomConstructeur = "Constructeur";
				} 
				else 
				{
					typeRetour = "void";
					nomMethode = nomMethodeTemp;
				}
			}
			else
			{
				// Cas normal : type de retour puis nom de méthode
				typeRetour = motSuivant;
				String nomMethodeTemp = "";
				if (scMethode.hasNext()) 
				{
					String methodAvecParam = scMethode.next();
					int indexParen = methodAvecParam.indexOf("(");
					if (indexParen != -1) 
					{
						nomMethodeTemp = methodAvecParam.substring(0, indexParen);
					} 
					else 
					{
						nomMethodeTemp = methodAvecParam;
					}
				}
				
				// Vérifier si c'est un constructeur
				if (nomMethodeTemp.equals(nomFichier) && !ligne.contains("record")) 
				{
					typeRetour = "";
					nomConstructeur = "Constructeur";
				} 
				else 
				{
					nomMethode = nomMethodeTemp;
				}
			}
		}
		
		scMethode.close();
		
		// Parser les paramètres
		String params = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));
		lstParametres = parserParametres(params);

		// Ajouter la méthode ou le constructeur
		if (!nomConstructeur.equals(""))
		{
			this.lstMethode.add(new Methode(nomConstructeur, typeRetour, visibilite, false,
			                                new ArrayList<>(lstParametres)));
		}
		else
		{
			this.lstMethode.add(new Methode(nomMethode, typeRetour, visibilite, isMethodeAbstract,
			                                new ArrayList<>(lstParametres)));
		}
	}

	/**
	 * Parse une liste de paramètres.
	 */
	private List<Parametre> parserParametres(String params)
	{
		List<Parametre> lstParametres = new ArrayList<>();
		
		if (params.isEmpty()) {return lstParametres;}

		// Découper les paramètres en tenant compte des génériques
		ArrayList<String> paramsListe = new ArrayList<>();
		int niveau = 0;
		StringBuilder paramActuel = new StringBuilder();
		
		for (int i = 0; i < params.length(); i++)
		{
			char c = params.charAt(i);
			
			if (c == '<') 
			{
				niveau++;
				paramActuel.append(c);
			} 
			else if (c == '>') 
			{
				niveau--;
				paramActuel.append(c);
			} 
			else if (c == ',' && niveau == 0) 
			{
				paramsListe.add(paramActuel.toString().trim());
				paramActuel = new StringBuilder();
			} 
			else 
			{
				paramActuel.append(c);
			}
		}
		
		if (paramActuel.length() > 0) 
		{
			paramsListe.add(paramActuel.toString().trim());
		}
		
		// Analyser chaque paramètre
		for (String paramStr : paramsListe)
		{
			Scanner scParam = new Scanner(paramStr);
			
			if (scParam.hasNext())
			{
				String typeParam = scParam.next();
				
				// Si le type contient des génériques
				while (typeParam.contains("<") && !typeParam.contains(">") && scParam.hasNext()) 
				{
					typeParam += " " + scParam.next();
				}
				
				if (scParam.hasNext()) 
				{
					String nomParam = scParam.next();
					lstParametres.add(new Parametre(nomParam, typeParam));
				}
			}
			scParam.close();
		}
		
		return lstParametres;
	}
}

