package metier.sauvegarde;

import controleur.Controleur;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import metier.util.*;
import vue.BlocClasse;
import vue.liaison.LiaisonVue;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GestionSauvegarde 
{

	//------------------------//
	//       ATTRIBUTS        //
	//------------------------//

	private String cheminDossier;
	private Controleur ctrl;
	private final String S_LIAISON = "---- Liaisons ----";


	//-------------------------//
	//      CONSTRUCTEUR       //
	//-------------------------//
	
	public GestionSauvegarde(Controleur ctrl) 
	{
		this.ctrl            = ctrl;
	}

	//----------------------//
	//      METHODES        //
	//----------------------//

	/**
	* Récupère une map de coordonnées 
	* @param dossierFichSelec L'intitulé du projet sur lequel baser les coordonées
	* @return Une {@link Map<String, int[]>} des noms des Classes et leurs coordonées (x et y)
	*/
	public Map<String, int[]> lireCoordoneesXml(String dossierFichSelec) 
	{
		//Récupére depuis util le chemin pour sauvegarde/dossier
		String cheminDossier = Path.of(ConstantesChemins.SAUVEGARDES , dossierFichSelec).toString();

		System.out.println("emplacement du fichier à charger : " + dossierFichSelec);

		Map<String, int[]> hashCoordonnees = new HashMap<String, int[]>();

		try (BufferedReader reader = new BufferedReader(new FileReader(cheminDossier))) 
		{
			String ligne;
			boolean lectureClasses = false;

			while ((ligne = reader.readLine()) != null) 
			{
				// Ignorer les lignes vides
				if (ligne.trim().isEmpty())
				{
					continue;
				}

				// Enregistrer le chemin du projet
				if (ligne.contains("/") || ligne.contains("\\")) 
				{
					this.cheminDossier = ligne.trim();
					continue;
				}

				// Détecter le début de la section Classes
				if (ligne.startsWith("---- Classes ----"))
				{
					lectureClasses = true;
					continue;
				}

				// Détecter le début de la section Liaisons (fin de Classes)
				if (ligne.startsWith("---- Liaisons ----"))
				{
					lectureClasses = false;
					break;
				}

				// Ignorer les en-têtes commentées (commençant par #)
				if (ligne.startsWith("#"))
				{
					continue;
				}

				if (lectureClasses)
				{
					// Vérifier si c'est une ligne de bloc classe (contient des tabs et au moins 5 colonnes)
					if (ligne.contains("\t") && !ligne.startsWith("-") && !ligne.startsWith("+") && !ligne.startsWith("#"))
					{
						String[] parts = ligne.split("\t");
						
						if (parts.length >= 3)
						{
							try
							{
								String nomClass = parts[0].trim();
								int x = Integer.parseInt(parts[1].trim());
								int y = Integer.parseInt(parts[2].trim());

								hashCoordonnees.put(nomClass, new int[] { x, y });
							}
							catch (NumberFormatException e)
							{
								// Ignorer les lignes qui ne sont pas des blocs (comme les attributs/méthodes)
								continue;
							}
						}
					}
				}
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return hashCoordonnees;
	}

	/**
	 * Lit les liaisons à partir d'un fichier dans data/sauvegardes/ à partir d'une ligne donnée.
	*
	* La méthode parcourt le fichier ligne par ligne, ignore les lignes vides et les commentaires
	* (commençant par '#'). Chaque ligne valide est découpée en colonnes selon les tabulation
	* pour créer un objet {@link LiaisonVue}.
	*
	* @param dossierFichSelec le nom du fichier.xml contenant les liaisons, situé dans le dossier de sauvegarde
	* @param ligneDepartLecture l'indice de la ligne à partir de laquelle commencer la lecture des liaisons
	* @return une liste de {@link LiaisonVue} correspondant aux liaisons lues
	*/
	public List<LiaisonVue> lectureLiaison(String dossierFichSelec, Map<String, BlocClasse> hashMapBlocClass)
	{
		List<LiaisonVue> lstLiaisons = new ArrayList<>();

		String chemin = Path.of(ConstantesChemins.SAUVEGARDES , this.getIntituleFromLien(dossierFichSelec)).toString() + ".xml";
		

		try (BufferedReader br = new BufferedReader(new FileReader(chemin))) 
		{
			String ligne;
			boolean lectureLiaisons = false;


			// on commence a lire :D
			while ((ligne = br.readLine()) != null) 
			{

				ligne = ligne.trim();
				
				if (ligne.isEmpty())       continue;
				if (ligne.startsWith("#")) continue;

				// Détection du début des liaisons
				if (ligne.equals(S_LIAISON)) 
				{
					lectureLiaisons = true;
					continue;
				}

				// Tant qu’on n’est pas dans la section liaisons, on ignore
				if (!lectureLiaisons) continue;

				String[] tabLigne = ligne.split("\t");
				if (tabLigne.length < 8) continue;

				String     typeLiaison   = tabLigne[0].trim();
				BlocClasse blocOrig      = hashMapBlocClass.get(tabLigne[2].trim());
				BlocClasse blocDest      = hashMapBlocClass.get(tabLigne[5].trim());

				System.out.println("Ligne analysée : " + typeLiaison + ", orig : " + blocOrig.getNom() + ", dest : " + blocDest.getNom());

				LiaisonVue liaisonVue;

				if(typeLiaison.equals("association_uni") || 
					typeLiaison.equals("association_bi"))
				{
					if(typeLiaison.equals("association_uni"))
					{
						 liaisonVue = new LiaisonVue(blocOrig, blocDest, "association",
																	true,
																	tabLigne[8].trim(),
																	tabLigne[9].trim()
																	);
					}
					else
					{
						 liaisonVue = new LiaisonVue(blocOrig, blocDest, "association",
																	false,
																	tabLigne[8].trim(),
																	tabLigne[9].trim()
																	);
	
					}

					lstLiaisons.add(liaisonVue);
					
				}
				else
				{
					liaisonVue =  new LiaisonVue(blocOrig, blocDest, typeLiaison);
					lstLiaisons.add(liaisonVue);

				}

					liaisonVue.setPosRelOrig(Double.parseDouble(tabLigne[4].trim()));
					liaisonVue.setPosRelDest(Double.parseDouble(tabLigne[7].trim()));
					

					int numPositionPointOrig = convertirPosition(tabLigne[3].trim());
					int numPositionPointDest = convertirPosition(tabLigne[6].trim());

					liaisonVue.setSideOrigine(numPositionPointOrig);
					liaisonVue.setSideDestination(numPositionPointDest);

			}

		} 
		catch (IOException | NumberFormatException e) 
		{
			e.printStackTrace();
		}

		return lstLiaisons;
	}

	private int convertirPosition (String position) 
	{
		return switch (position) 
		{
			case "TOP"    -> 0;
			case "RIGHT"  -> 1;
			case "BOTTOM" -> 2;
			case "LEFT"   -> 3;
			default       -> -1;
		};
    };
	/**
	* Charge les blocs de classe depuis un fichier de sauvegarde au nouveau format
	* @param nomProjet Le nom du projet à charger
	* @return Une {@link Map<String, BlocClasse>} contenant tous les blocs chargés
	*/
	public Map<String, BlocClasse> chargerBlocsClasses(String nomProjet)
	{
		Map<String, BlocClasse> mapBlocClasses = new HashMap<>();
		
		Path cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomProjet + ".xml");
		File file = new File(cheminPath.toString());

		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			String ligne;
			boolean lectureClasses = false;
			BlocClasse blocCourant = null;

			while ((ligne = reader.readLine()) != null)
			{
				// Ignorer les lignes vides
				if (ligne.trim().isEmpty())
				{
					continue;
				}

				// Ignorer la ligne de chemin du projet
				if (ligne.contains("/") || ligne.contains("\\"))
				{
					this.cheminDossier = ligne.trim();
					continue;
				}

				// Détecter le début de la section Classes
				if (ligne.startsWith("---- Classes ----"))
				{
					lectureClasses = true;
					continue;
				}

				// Ignorer les en-têtes commentées (commençant par #)
				if (ligne.startsWith("#"))
				{
					continue;
				}

				// Détecter le début de la section Liaisons
				if (ligne.startsWith("---- Liaisons ----"))
				{
					lectureClasses = false;
					continue;
				}

				if (lectureClasses)
				{
					// Vérifier si c'est une ligne de bloc classe (contient des tabs et au moins 5 colonnes)
					if (ligne.contains("\t") && !ligne.startsWith("-") && !ligne.startsWith("+") && !ligne.startsWith("#"))
					{
						String[] parts = ligne.split("\t");
						
						if (parts.length >= 6)
						{
							// C'est une ligne de définition de bloc
							String nomBloc = parts[0].trim();
							int x = Integer.parseInt(parts[1].trim());
							int y = Integer.parseInt(parts[2].trim());
							int largeur = Integer.parseInt(parts[3].trim());
							int hauteur = Integer.parseInt(parts[4].trim());
							boolean estInterface = Boolean.parseBoolean(parts[5].trim());

							// Créer le bloc
							blocCourant = new BlocClasse(nomBloc, x, y);
							blocCourant.setInterface(estInterface);
							
							mapBlocClasses.put(nomBloc, blocCourant);
						}
					}
					else if ((ligne.startsWith("-") || ligne.startsWith("+") || ligne.startsWith("#") || ligne.startsWith("~")) && blocCourant != null)
					{
						// C'est un attribut ou une méthode
						if (ligne.contains("(") && ligne.contains(")"))
						{
							// C'est une méthode
							List<String> methodes = blocCourant.getMethodes();
							methodes.add(ligne);
						}
						else
						{
							// C'est un attribut
							List<String> attributs = blocCourant.getAttributs();
							attributs.add(ligne);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mapBlocClasses;
	}

	/**
	* Récupère une map de coordonnées 
	* @param dossierFichSelec L'intitulé du projet sur lequel baser les coordonées
	* @return Une {@link Map<String, int[]>} des noms des Classes et leurs coordonées (x et y)
	*/
	public void sauvegarderClasses(List<BlocClasse> listBlocClasses, List <LiaisonVue> listLiaison, String cheminProjet)
	{
		// Vérifier que cheminProjet n'est pas null
		if (cheminProjet == null || cheminProjet.isEmpty()) {
			System.out.println("Erreur: cheminProjet est null ou vide dans sauvegarderClasses");
			return;
		}

		String fichierLectureEcriture = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();

		String   nomProjetASauv         = getIntituleFromLien(cheminProjet);

		// Vérifier si le projet est déjà sauvegardé
		if (this.projetEstSauvegarde(cheminProjet)) 
		{
			// Le projet existe : modifier uniquement le fichier de coordonnées
			sauvegarderCoordProjet(listBlocClasses, nomProjetASauv, cheminProjet);
			sauvegarderLiaison(listLiaison, nomProjetASauv);
		} 
		else 
		{
			// Le projet n'existe pas : ajouter une nouvelle ligne à projets.xml
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichierLectureEcriture, true))) 
			{
				String ligneAAjouter = cheminProjet + "\t" + nomProjetASauv;
				bw.write(ligneAAjouter + "\n");
				
				// Créer le fichier de coordonnées pour la première fois
				sauvegarderCoordProjet(listBlocClasses, nomProjetASauv, cheminProjet);
				sauvegarderLiaison(listLiaison, nomProjetASauv);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	/**
	* Sauvegarde les blocs de classe avec leurs coordonnées, dimensions, attributs et méthodes
	* @param listBlocClasses Une {@link List} de {@link BlocClasse}s du projet
	* @param nomProjet Le nom du projet
	* @param cheminProjet Le chemin complet du projet
	*/
	public void sauvegarderCoordProjet(List<BlocClasse> listBlocClasses, String nomProjet, String cheminProjet)
	{
		// Vérifier que les paramètres ne sont pas null
		if (listBlocClasses == null || nomProjet == null || nomProjet.isEmpty() || cheminProjet == null || cheminProjet.isEmpty()) {
			System.out.println("Erreur: paramètres invalides dans sauvegarderCoordProjet");
			return;
		}
		
		Path cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomProjet + ".xml");
		File file = new File(cheminPath.toString());

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) 
		{
			// Écrire l'en-tête avec le chemin du projet
			bw.write(cheminProjet);
			bw.newLine();
			bw.write("---- Classes ----");
			bw.newLine();
			bw.write("#nomBloc\tx\ty\tlargeur\thauteur\testInterface");
			bw.newLine();

			// Écrire tous les blocs de classe avec leurs attributs et méthodes
			for (BlocClasse blocClasse : listBlocClasses) 
			{
				// Écrire la ligne d'en-tête du bloc
				bw.write(blocClasse.getNom().trim() + "\t" + 
						blocClasse.getX() + "\t" + 
						blocClasse.getY() + "\t" +
						blocClasse.getLargeur() + "\t" +
						blocClasse.getHauteur() + "\t" +
						blocClasse.estInterface());
				bw.newLine();

				// Écrire les attributs
				List<String> attributs = blocClasse.getAttributs();
				if (attributs != null && !attributs.isEmpty()) 
				{
					for (String attribut : attributs) 
					{
						bw.write(attribut);
						bw.newLine();
					}
				}

				// Écrire les méthodes
				List<String> methodes = blocClasse.getMethodes();
				if (methodes != null && !methodes.isEmpty()) 
				{
					for (String methode : methodes) 
					{
						bw.write(methode);
						bw.newLine();
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	* Sauvegarde les liaisons dans le fichier .xml du projet
	* @param listLiaison Une {@link List} de {@link LiaisonVue}s du projet
	* @param nomProjet Le nom du projet
	*/
	public void sauvegarderLiaison(List<LiaisonVue> listLiaison, String nomProjet)
	{
		Path cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomProjet + ".xml");
		File file = new File(cheminPath.toString());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) 
        {
            //
            bw.write("---- Liaisons ----");
            bw.newLine();
            bw.write("#typeLiaison\tid\tblocOrig\tcoteOrig\tposRelOrig\tblocDest\tcoteDest\tposRelDest\tmultiOrig\tmultiDest");
            bw.newLine();


            int id = 0;
            // Écrire les informations de toutes les liaisons
            for (LiaisonVue liaisonVue : listLiaison) 
            {	
				String type = liaisonVue.getType();
				if(type.equals("association")){
					if(liaisonVue.isUnidirectionnel()){
						type = type + "_uni";
					} else {type = type + "_bi";}
				}
				
                bw.write(type + "\t" + 
                         id + "\t" + 
                         liaisonVue.getBlocOrigine().getNom() + "\t" + 
                         liaisonVue.getSideOrig() + "\t" + 
                         liaisonVue.getNivOrig() + "\t" + 
                         liaisonVue.getBlocDestination().getNom() + "\t" + 
                         liaisonVue.getSideDest() + "\t" + 
                         liaisonVue.getNivDest() + "\t" +
						 liaisonVue.getMultOrig() + "\t" + 
                         liaisonVue.getMultDest() );
                bw.newLine();
                id++;
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

	public boolean fichierDeSauvegardeExiste(String nomIntitule) 
	{
		String cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomIntitule + ".xml").toString();
		File file = new File(cheminPath);
		return file.exists();
	}
	
	public String getIntituleFromLien(String paraCheminDossier) 
	{

		String fichierPath = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();

		try(Scanner scan = new Scanner(new File(fichierPath))) 
		{
			while(scan.hasNextLine())
			{
				String ligne = scan.nextLine();
				
				String[] tabLigne = ligne.split("\t");

				if(tabLigne[0].equals(paraCheminDossier.trim()))
				{
					String intitule = tabLigne[1].trim();
					// Extraire juste le nom du projet si c'est un chemin complet
					int indiceslash = Math.max(intitule.lastIndexOf("/"), intitule.lastIndexOf("\\"));
					if (indiceslash >= 0) {
						intitule = intitule.substring(indiceslash + 1);
					}
					return intitule;
				}
			}
			
		} 
		catch (Exception e) 
		{
			e.getMessage();
		}

		return "";
	}

	public String getLienFromIntitule(String intituleProjet) 
	{

		String fichierPath = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();

		try(Scanner scan = new Scanner(new File(fichierPath))) 
		{
			while(scan.hasNextLine())
			{
				String ligne = scan.nextLine();
				
				String[] tabLigne = ligne.split("\t");

				if(tabLigne.length >= 2 && tabLigne[1].trim().equals(intituleProjet.trim()))
				{
					return tabLigne[0].trim();
				}
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return "";
	}

	public boolean projetEstSauvegarde(String cheminProjet) 
	{

		String fichierPath = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();
		
		try(Scanner scan = new Scanner(new File(fichierPath))) 
		{
			while(scan.hasNextLine())
			{
				String ligne = scan.nextLine();
				
				String[] tabLigne = ligne.split("\t");

				// Vérifier si le chemin du projet correspond
				if(tabLigne.length >= 1 && tabLigne[0].equals(cheminProjet.trim()))
				{
					return true;
				}
			}
			
		} 
		catch (Exception e) 
		{
			e.getMessage();
		}

		return false;
	}

	public void sauvegardeProjetXml(String cheminFichier)
	{
		int indiceslash = Math.max(cheminFichier.lastIndexOf("/"), cheminFichier.lastIndexOf("\\"));
		String nomProjet = cheminFichier.substring(indiceslash + 1).trim();
		
		// Vérifier si le projet est déjà enregistré
		if (!projetEstSauvegarde(cheminFichier))
		{
	
			String fichierPath = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();

			File fichier = new File(fichierPath);
			
			try
			{
				// Créer les dossiers parents s'ils n'existent pas
				File parent = fichier.getParentFile();
				if (parent != null && !parent.exists())
				{
					parent.mkdirs();
				}
				
				// TIENS HUGO CEST ICI
				// Création du fichier s'il n'existe pas
				if (!fichier.exists())
				{
					fichier.createNewFile();
				}
				
				// Ajouter la ligne au fichier
				try (FileWriter writer = new FileWriter(fichier, true))
				{
					writer.write(cheminFichier + "\t" + nomProjet + System.lineSeparator());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	//-----------//
	//  GETTERS  //
	//-----------//

	public String getCheminDossier() 
	{
		return this.cheminDossier;
	}
}
//test