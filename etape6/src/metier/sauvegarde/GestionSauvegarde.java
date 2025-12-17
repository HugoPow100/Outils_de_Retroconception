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

public class GestionSauvegarde 
{

    //------------------------//
    //       ATTRIBUTS        //
    //------------------------//

    private String cheminDossier;
    private Controleur ctrl;


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

            while ((ligne = reader.readLine()) != null) 
            {
                if (ligne.contains("/") || ligne.contains("\\")) 
                {
                    this.cheminDossier = ligne.trim();
                    continue;
                } 
                else
                {
                    // Trouver la position des espaces
                    int premierEspace  = ligne.indexOf(' ');
                    int deuxiemeEspace = ligne.indexOf(' ', premierEspace + 1);

                    // Extraire les morceaux
                    String nomClass    = ligne.substring(0, premierEspace);
                    String xStr        = ligne.substring(premierEspace + 1, deuxiemeEspace).trim();
                    String yStr        = ligne.substring(deuxiemeEspace + 1).trim();
                    //System.out.println(nomClass + " " + xStr + " " + yStr);

                    int    x           = Integer.parseInt(xStr);
                    int    y           = Integer.parseInt(yStr);

                    hashCoordonnees.put(nomClass, new int[] { x, y });
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
	public List<LiaisonVue> lectureLiaison(String dossierFichSelec, int ligneDepartLecture)
	{
		List<LiaisonVue> lstLiaisons = new ArrayList<>();

		String chemin = Path.of(ConstantesChemins.SAUVEGARDES , dossierFichSelec).toString();

		try (BufferedReader br = new BufferedReader(new FileReader(chemin))) 
		{
			String ligne;
			int index = 0;

			//  On avance jusqu'à la ligne donnée par ligneDepartLecture
			while (index < ligneDepartLecture && br.readLine() != null) 
			{
				index++;
			}

			// on commence a lire :D
			while ((ligne = br.readLine()) != null) 
			{

				ligne = ligne.trim();
				if (ligne.isEmpty()) continue;
				if (ligne.startsWith("#")) continue;

				String[] tabLigne = ligne.split("\\s+");
				if (tabLigne.length < 10) continue;

				String typeLiaison       = tabLigne[0].trim();
				String blocOrig          = tabLigne[2].trim();
				String blocDest          = tabLigne[5].trim();

				String Bidirectionnalite = typeLiaison.substring(typeLiaison.indexOf("_") +1).trim();


				if(typeLiaison.equals("association_uni") || 
					typeLiaison.equals("association_bi"))
				{
					if(Bidirectionnalite.equals("uni"))
					{
							LiaisonVue liaisonVue = new LiaisonVue(blocOrig, blocDest, "association",
																	true,
																	tabLigne[8].trim(),
																	tabLigne[9].trim()
																	);
					}
					else
					{
						LiaisonVue liaisonVue = new LiaisonVue(blocOrig, blocDest, "association",
																	false,
																	tabLigne[8].trim(),
																	tabLigne[9].trim()
																	);
					}

					
				}
				else
				{
					LiaisonVue liaisonVue =  new LiaisonVue(blocOrig, blocDest, typeLiaison);
				}

				lstLiaisons.add(liaisonVue);
			}

		} 
		catch (IOException | NumberFormatException e) 
		{
			e.printStackTrace();
		}

		return lstLiaisons;
	}

    /**
    * Récupère une map de coordonnées 
    * @param dossierFichSelec L'intitulé du projet sur lequel baser les coordonées
    * @return Une {@link Map<String, int[]>} des noms des Classes et leurs coordonées (x et y)
    */
    public void sauvegarderClasses(List<BlocClasse> listBlocClasses, String cheminProjet)
    {

        String fichierLectureEcriture = Path.of(ConstantesChemins.DONNEES, "projets.xml").toString();

        String   nomProjetASauv         = getIntituleFromLien(cheminDossier);

        // Vérifier si le projet est déjà sauvegardé
        if (this.projetEstSauvegarde(cheminProjet)) 
        {
            // Le projet existe : modifier uniquement le fichier de coordonnées
            sauvegarderCoordProjet(listBlocClasses, nomProjetASauv, cheminProjet);
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
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    /**
    * Sauvegarde les coordonées des {@link BlocClasse}s donnés en paramètres pour écrire sur le .xml correspondant (en fonction de l'intitulé projet récupéré) 
    * @param listBlocClasses Une {@link List} de {@link BlocClasse}s du projet
    * @param nomProjet Le nom du projet à 
    */
    public void sauvegarderCoordProjet(List<BlocClasse> listBlocClasses, String nomProjet, String cheminProjet)
    {
        
        Path cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomProjet + ".xml");
        File file = new File(cheminPath.toString());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) 
        {
            // Écrire l'en-tête avec le chemin du projet
            bw.write(cheminProjet);
            bw.newLine();

            // Écrire les coordonnées de tous les blocs
            for (BlocClasse blocClasse : listBlocClasses) 
            {
                bw.write(blocClasse.getNom().trim() + " " + 
                         blocClasse.getX() + " " + 
                         blocClasse.getY());
                bw.newLine();
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

    /*public HashMap<String, BlocClasse> chargerSauvegardeCoord(String nomFichier,  HashMap<String, BlocClasse> mapBlocClasse)
    {
        System.out.println("nom du fichier à sauvegarder : " + nomFichier);
        HashMap<String, BlocClasse> mapNouvBlocClasse = new HashMap<>();

        /*String   basePath               = System.getProperty("user.dir");

        //System.out.println(basePath);
        String   cheminPath             = basePath + "data/donnees/sauvegardes/";

        //System.out.println(cheminPath);

        File file = new File(cheminPath + nomFichier + ".xml");

        Path cheminPath = Path.of(ConstantesChemins.SAUVEGARDES, nomFichier + ".xml");
        File file = new File(cheminPath.toString());

        //System.out.println("nomfich :" + nomFichier);

        try (Scanner scanner = new Scanner(file)) 
        {
            while (scanner.hasNextLine()) 
            {
                String ligne = scanner.nextLine();
                //System.out.println(ligne);

                if(!ligne.contains("/"))
                {

                    String[] tabClass = ligne.split("\\s+");
                    
                    BlocClasse blocClasse   = mapBlocClasse.get(tabClass[0].trim());
                    int    posX             = Integer.parseInt(tabClass[1].trim());
                    int    posY             = Integer.parseInt(tabClass[2].trim());

                    blocClasse.setX(posX);
                    blocClasse.setY(posY);

                    mapNouvBlocClasse.put(blocClasse.getNom(), blocClasse);
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            e.getMessage();
        }

        return mapNouvBlocClasse;
    }*/
    
    public String getIntituleFromLien(String paraCheminDossier) {

        /*String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "data/donnees/projets.xml";*/

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

    public String getLienFromIntitule(String intituleProjet) {

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

    public boolean projetEstSauvegarde(String cheminProjet) {

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
            // Ajouter le projet à projets.xml
            /*String basePath = System.getProperty("user.dir");
            String fichierPath = basePath + "data/donnees/projets.xml";*/

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
