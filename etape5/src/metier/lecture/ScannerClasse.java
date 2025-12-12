package metier.lecture;

import metier.Classe;
import metier.Attribut;
import metier.Methode;
import metier.Parametre;

import metier.lecture.*;

import java.util.*;

public class ScannerClasse 
{
	private Lecture     lecture;

	public ScannerClasse (Lecture lecture)
    {
		this.lecture = lecture;
	}

    public Classe scanne(Scanner scFic, String nomFichierAvExt)
		{
		Scanner scLigne;

		Classe classe;
		Attribut attribut;
		Methode methode;
		Parametre parametre;

		String nom;

		// Réinitialiser les listes pour chaque nouveau fichier
		this.lecture.setLstAttribut(new ArrayList<Attribut>());
		this.lecture.setLstMethode(new ArrayList<Methode>());

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
						this.lecture.getLstAttribut().add(new Attribut(valeur, "", "public", "classe", true));
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
								this.lecture.getLstAttribut().add(new Attribut(nomParam, typeParam, "private", "instance", false));
								parametresConstructeur.add(new Parametre(nomParam, typeParam));
								this.lecture.getLstMethode().add(new Methode(nomParam, typeParam, "public", false, new ArrayList<>()));
							}
						}
						
						this.lecture.getLstMethode().add(new Methode("Constructeur", "", "public", false, parametresConstructeur));
					}
					
					this.lecture.getLstMethode().add(new Methode("equals", "boolean", "public", false, 
						List.of(new Parametre("obj", "Object"))));
					this.lecture.getLstMethode().add(new Methode("hashCode", "int", "public", false, new ArrayList<>()));
					this.lecture.getLstMethode().add(new Methode("toString", "String", "public", false, new ArrayList<>()));
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
					this.lecture.getLstAttribut().add(new Attribut(nom, type, visibiliteAtribut, portee, isConstant));
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
						this.lecture.getLstMethode().add(
								new Methode(nomConstructeur, typeRetour, visibilite, false,
										new ArrayList<>(lstParametres)));
						nomConstructeur = ""; // Réinitialiser
					} else {
						System.out.println("Nouvelle méthode ajouté");
						this.lecture.getLstMethode()
								.add(new Methode(nomMethode, typeRetour, visibilite, isMethodeAbstract,
										new ArrayList<>(lstParametres)));
					}
				}
			}
		}

		//System.out.println("val isHeritage : " + isHeritage);
		typeClasse = typeClasse.trim().replace(" ","," );
		System.out.println("typeClasse : " + typeClasse);
		return new Classe(nomFichier, classeParente, typeClasse, isHeritage, interfaces, this.lecture.getLstAttribut(), this.lecture.getLstMethode());
	}

}

