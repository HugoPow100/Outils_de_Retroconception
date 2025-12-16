package vue;

import metier.lecture.Lecture;
import metier.objet.*;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Vue pour l'affichage dans le terminal.
 * Gère tous les affichages de l'application.
 */
public class VueTerminal
{
	/**
	 * Affiche toutes les classes analysées.
	 * 
	 * @param hashMapClasses Map des classes à afficher
	 */
	public void afficherClasses(HashMap<String, Classe> hashMapClasses)
	{
		for (String nomFichier : hashMapClasses.keySet())
		{
			Classe classe = hashMapClasses.get(nomFichier);
			String typeInfo = "";
			String typeElement = "classe";

			if (classe.isInterface())
			{
				typeInfo = " (interface)";
				typeElement = "interface";
			}
			else if (classe.isRecord())
			{
				typeInfo = " (record)";
				typeElement = "record";
			}
			else if (classe.isEnum())
			{
				typeInfo = " (enum)";
				typeElement = "enum";
			}
			else if (classe.isAbstract())
			{
				typeInfo = " (abstract)";
			}

			System.out.println("\n=== Analyse de " + typeElement + " " + classe.getNom() + typeInfo + " ===\n");

			// Affichage UML
			afficherUML(classe);
		}
	}

	/**
	 * Affiche les associations détectées.
	 * 
	 * @param associations Liste des associations
	 */
	public void afficherAssociations(ArrayList<Association> associations)
	{
		if (!associations.isEmpty())
		{
			System.out.println("\n=== Associations détectées ===\n");
			for (Association assoc : associations)
			{
				System.out.println(assoc.toString());
			}
		}
	}

	/**
	 * Affiche les héritages.
	 * 
	 * @param lecture Objet Lecture contenant les informations d'héritage
	 */
	public void afficherHeritage(Lecture lecture)
	{
		lecture.afficherHeritage();
	}

	public void afficherImplementation(Lecture lecture)
	{
		lecture.afficherImplementation();
	}

	/**
	 * Affiche les détails d'une classe (attributs et méthodes).
	 * 
	 * @param classe La classe à afficher
	 */
	public void afficherDetailsClasse(Classe classe)
	{
		ArrayList<Attribut> attributs = classe.getLstAttribut();
		ArrayList<Methode> methodes = classe.getLstMethode();

		// Afficher les attributs
		if (!attributs.isEmpty())
		{
			int cpt = 1;
			for (Attribut a : attributs)
			{
				System.out.printf("attribut : %d\tnom : %-8s\ttype : %-8s\tvisibilité : %-8s\tportée : %s%n",
						cpt++, a.getNom(), a.getType(), a.getVisibilite(), a.getPortee());
			}
			System.out.println(); // Ligne vide après les attributs
		}

		// Afficher les méthodes
		if (!methodes.isEmpty())
		{
			for (Methode m : methodes)
			{
				String typeRetour = m.getRetour();
				String affichageType = "";

				if (m.getNomMethode().equals("Constructeur"))
				{
					System.out.println("méthode : Constructeur\tvisibilité : " + m.getVisibilite());
				}
				else
				{
					if (typeRetour.equals("void"))
					{
						affichageType = "aucun";
					}
					else if (typeRetour.equals("int") || typeRetour.equals("Integer"))
					{
						affichageType = "entier";
					}
					else if (typeRetour.equals("double") || typeRetour.equals("Double"))
					{
						affichageType = "réel";
					}
					else
					{
						affichageType = typeRetour;
					}

					String abstractTag = m.isAbstract() ? "\t{abstract}" : "";
					System.out.println("méthode : " + m.getNomMethode() + "\tvisibilité : " + m.getVisibilite()
							+ "\ttype de retour : " + affichageType + abstractTag);
				}

				// Afficher les paramètres
				if (m.getLstParametre().isEmpty())
				{
					System.out.println("paramètres : aucun");
				}
				else
				{
					boolean premier = true;
					for (Parametre p : m.getLstParametre())
					{
						if (premier)
						{
							String[] parts = p.getContenue().split(" type : ");
							String nomParam = parts[0].substring(parts[0].indexOf(":") + 2);
							String typeParam = parts.length > 1 ? parts[1] : "";
							System.out.printf("paramètres : \tnom : %-8s\ttype : %s%n", nomParam, typeParam);
							premier = false;
						}
						else
						{
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

	/**
	 * Affiche une classe au format UML.
	 * 
	 * @param classe La classe à afficher
	 */
	private void afficherUML(Classe classe)
	{
		ArrayList<Attribut> attributs = classe.getLstAttribut();
		ArrayList<Methode> methodes = classe.getLstMethode();

		// Calculer la largeur de la boîte
		String nomClasse = classe.getNom();
		String stereotype = "";
		String nomInterface = classe.getNomInterface();

		if (classe.isInterface())
		{
			stereotype = "<< interface";
		}
		else if (classe.isRecord())
		{
			stereotype = "<< record";
		}
		else if (classe.isEnum())
		{
			stereotype = "<< enumeration";
		}
		else if (classe.isAbstract())
		{
			stereotype = "<< abstract";
		}

		String classeHerite = "";

		if (classe.getTypeClasse().contains("implements"))
		{
			classeHerite += "{ " + nomInterface + " }";

			if (!stereotype.isEmpty())
			{
				stereotype += ", implémente";
			}
			else
			{
				stereotype = "<< implémente";
			}
		}

		if (classe.getIsHeritage())
		{
			classeHerite += "[ " + classe.getClasseParente() + " ]";

			if (!stereotype.isEmpty())
			{
				stereotype += ", herite";
			}
			else
			{
				stereotype = "<< herite";
			}
		}

		int largeurMax = Math.max(50, Math.max(nomClasse.length(), stereotype.length()) + 10);

		if (!classeHerite.isEmpty())
		{
			largeurMax = Math.max(largeurMax, classeHerite.length() + 4);
		}

		for (Attribut a : attributs)
		{
			largeurMax = Math.max(largeurMax, a.toString().length() + 4);
		}

		for (Methode m : methodes)
		{
			largeurMax = Math.max(largeurMax, m.toString().length() + 4);
		}

		int largeur = largeurMax;
		String ligne = "-".repeat(largeur);

		System.out.println(ligne);
		if (!stereotype.isEmpty())
		{
			stereotype += " >>";
			System.out.println(centrer(stereotype, largeur));
		}

		System.out.println(centrer(nomClasse, largeur));
		if (!classeHerite.isEmpty())
		{
			System.out.println(centrer(classeHerite, largeur));
		}
		System.out.println(ligne);

		// Afficher les attributs
		if (!attributs.isEmpty())
		{
			for (Attribut a : attributs)
			{
				// Pour les enums, afficher uniquement le nom sans symbole ni type
<<<<<<< HEAD:etape4/src/vue/VueTerminal.java
				if (classe.isEnum())
				{
					System.out.println(a.getNomAttribut());
				}
				else
				{
=======
				if (classe.isEnum()) {
					System.out.println(a.getNom());
				} else {
>>>>>>> c656893b9c6cae87f0fc61a6f6fb977db65205db:etape4/src/metier/Retroconception.java
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

	/**
	 * Centre un texte dans une ligne de largeur donnée.
	 * 
	 * @param texte   Le texte à centrer
	 * @param largeur La largeur de la ligne
	 * @return Le texte centré
	 */
	private String centrer(String texte, int largeur) 
	{
		int espaces = (largeur - texte.length()) / 2;
		return " ".repeat(Math.max(0, espaces)) + texte;
	}
}
