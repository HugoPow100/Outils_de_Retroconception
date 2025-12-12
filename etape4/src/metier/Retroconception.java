package metier;

import java.util.ArrayList;
import java.util.HashMap;

public class Retroconception
{
	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.out.println("Usage: java Retroconception <fichier.java ou dossier>");
			return;
		}

		String cheminFichier = args[0];

		Lecture lecture = new Lecture(cheminFichier);
		HashMap<String, Classe> hashMapClasses = lecture.getHashMapClasses();

		for (String nomFichier : hashMapClasses.keySet()) {
			Classe classe = hashMapClasses.get(nomFichier);
			String typeInfo = "";
			String typeElement = "classe";
			
			if (classe.isInterface()) {
				typeInfo = " (interface)";
				typeElement = "interface";
			} else if (classe.isRecord()) {
				typeInfo = " (record)";
				typeElement = "record";
			} else if (classe.isEnum()) {
				typeInfo = " (enum)";
				typeElement = "enum";
			} else if (classe.isAbstract()) {
				typeInfo = " (abstract)";
			}
			
			System.out.println("\n=== Analyse de " + typeElement + " " + classe.getNom() + typeInfo + " ===\n");

			// Affichage détaillé
			// afficherDetailsClasse(classe);

			// System.out.println();

			// Affichage UML
			afficherUML(classe);
		}

		// Afficher les associations
		ArrayList<Association> associations = lecture.getLstAssociation();
		if (!associations.isEmpty()) {
			System.out.println("\n=== Associations détectées ===\n");
			for (Association assoc : associations) {
				System.out.println(assoc.toString());
			}
		}
	}

	private static boolean estMultiInstance(Association assoc) {
		String multDest = assoc.getMultDest().toString();
		return multDest.contains("*") || multDest.matches(".*\\d+\\.\\.\\.\\d+.*");
	}
	

	private static void afficherDetailsClasse(Classe classe) {
		ArrayList<Attribut> attributs = classe.getLstAttribut();
		ArrayList<Methode> methodes = classe.getLstMethode();

		// Afficher les attributs
		if (!attributs.isEmpty()) {
			int cpt = 1;
			for (Attribut a : attributs) {
				System.out.printf("attribut : %d\tnom : %-8s\ttype : %-8s\tvisibilité : %-8s\tportée : %s%n",
						cpt++, a.getNomAttribut(), a.getTypeAttribut(), a.getVisibilite(), a.getPorte());
			}
			System.out.println(); // Ligne vide après les attributs
		}

		// Afficher les méthodes
		if (!methodes.isEmpty()) {
			for (Methode m : methodes) {
				String typeRetour = m.getRetour();
				String affichageType = "";

				if (m.getNomMethode().equals("Constructeur")) {
					System.out.println("méthode : Constructeur\tvisibilité : " + m.getVisibilite());
				} else {
					if (typeRetour.equals("void")) {
						affichageType = "aucun";
					} else if (typeRetour.equals("int") || typeRetour.equals("Integer")) {
						affichageType = "entier";
					} else if (typeRetour.equals("double") || typeRetour.equals("Double")) {
						affichageType = "réel";
					} else {
						affichageType = typeRetour;
					}

					String abstractTag = m.isAbstract() ? "\t{abstract}" : "";
					System.out.println("méthode : " + m.getNomMethode() + "\tvisibilité : " + m.getVisibilite()
							+ "\ttype de retour : " + affichageType + abstractTag);
				} // Afficher les paramètres
				if (m.getLstParametre().isEmpty()) {
					System.out.println("paramètres : aucun");
				} else {
					boolean premier = true;
					for (Parametre p : m.getLstParametre()) {
						if (premier) {
							String[] parts = p.getContenue().split(" type : ");
							String nomParam = parts[0].substring(parts[0].indexOf(":") + 2);
							String typeParam = parts.length > 1 ? parts[1] : "";
							System.out.printf("paramètres : \tnom : %-8s\ttype : %s%n", nomParam, typeParam);
							premier = false;
						} else {
							String[] parts = p.getContenue().split(" type : ");
							String nomParam = parts[0].substring(parts[0].indexOf(":") + 2);
							String typeParam = parts.length > 1 ? parts[1] : "";
							System.out.printf("\t\tnom : %-8s\ttype : %s%n", nomParam, typeParam);
						}
					}
				}
				System.out.println(); // Ligne vide après chaque méthode
			}
		}
	}

	private static void afficherUML(Classe classe)
	{
		ArrayList<Attribut> attributs = classe.getLstAttribut();
		ArrayList<Methode> methodes = classe.getLstMethode();

		// Calculer la largeur de la boîte
		String nomClasse = classe.getNom();
		String stereotype = "";
		String nomInterface = classe.getNomInterface();

		
		if (classe.isInterface()) {
			stereotype = "<< interface";
		} else if (classe.isRecord()) {
			stereotype = "<< record";
		} else if (classe.isEnum()) {
			stereotype = "<< enumeration";
		} else if (classe.isAbstract()) {
			stereotype = "<< abstract";
		}

		String motHerite = "";
		String classeHerite = "";
		// rentre pas dans le if

		
		// System.out.println("Type classe [ debug ] : " + classe.getTypeClasse());


		if (classe.getTypeClasse().contains("implements"))
		{
			// System.out.println("Classe implémente une interface");
			classeHerite += "{ " + nomInterface + " }";

			if (!stereotype.isEmpty()) {
				stereotype += ", implémente";
			} else {
				stereotype = "<< implémente";
			}
		}


		if (classe.getIsHeritage())
		{
			classeHerite += "[ " + classe.getClasseParente() + " ]";
			
			if (!stereotype.isEmpty()) {
				stereotype += ", herite";
			} else {
				stereotype = "<< herite";
			}
		}

		//if (classe.getIsImplementing())
		//{
		//	classeHerite = "<< Implémente " + classe.getImplementing() + ">>";
		//}

		int largeur = Math.max(50, Math.max(nomClasse.length(), stereotype.length()) + 10);
		// largeur 50
		String ligne = "-".repeat(largeur);

		System.out.println(ligne);
		if (!stereotype.isEmpty()) {
			stereotype += " >>";
			System.out.println(centrer(stereotype, largeur));
		}

		//if (!motHerite.isEmpty())
		//{
		//	System.out.println(centrer(motHerite, largeur));
		//}
		System.out.println(centrer(nomClasse, largeur));
		if (!classeHerite.isEmpty())
		{
			System.out.println(centrer(classeHerite, largeur));
		}
		System.out.println(ligne);

		// Afficher les attributs
		if (!attributs.isEmpty()) {
			for (Attribut a : attributs) {
				// Pour les enums, afficher uniquement le nom sans symbole ni type
				if (classe.isEnum()) {
					System.out.println(a.getNomAttribut());
				} else {
					System.out.println(a.toString());
				}
			}
		}

		System.out.println(ligne);

		// Pour les enums, ne pas afficher la section méthodes
		if (!classe.isEnum())
		{
			// Afficher les méthodes
			if (!methodes.isEmpty())
			{
				for (Methode m : methodes)
				{
					System.out.println(m.toString());
				}
			}
			System.out.println(ligne);
		}
	}

	private static String centrer(String texte, int largeur) {
		int espaces = (largeur - texte.length()) / 2;
		return " ".repeat(Math.max(0, espaces)) + texte;
	}
}
