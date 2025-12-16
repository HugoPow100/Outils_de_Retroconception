package metier;
import java.util.ArrayList;
import java.util.HashMap;


public class Retroconception 
{
	public static void main(String[] args) 
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java Retroconception <fichier.java ou dossier>");
			return;
		}

		String cheminFichier = args[0];

		Lecture lecture = new Lecture(cheminFichier);
		HashMap<String, ArrayList<Classe>> hashMapClasses = lecture.getHashMapClasses();

		for (String nomFichier : hashMapClasses.keySet())
		{
			ArrayList<Classe> classes = hashMapClasses.get(nomFichier);
			for (Classe classe : classes)
			{
				System.out.println("\n=== Analyse de la classe " + classe.getNom() + " ===\n");
				
				// Affichage détaillé
				afficherDetailsClasse(classe);
				
				System.out.println();
			}
		}
	}

	private static void afficherDetailsClasse(Classe classe)
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
					
					System.out.println("méthode : " + m.getNomMethode() + "\tvisibilité : " + m.getVisibilite() + "\ttype de retour : " + affichageType);
				}				// Afficher les paramètres
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
}
