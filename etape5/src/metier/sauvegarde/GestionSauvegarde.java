package metier.sauvegarde;

import metier.objet.Classe;
import controlleur.Controlleur;
import vue.BlocClasse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class GestionSauvegarde 
{

    private Map<String, int[]> hashCoordonnees;
    private String cheminDossier;
    private Controlleur ctrl;

    public GestionSauvegarde(Controlleur ctrl) 
    {
        this.ctrl            = ctrl;
        this.hashCoordonnees = new HashMap<String, int[]>();
    }

    // dossierFichSelec cette variable prend le dossier selctioner et le fichier
    // corespondant au fichier enregister pour ces coordoner sauvegarder avant de
    // fermer le programe
    public void lecture(String dossierFichSelec) 
    {
        // Ces ligne permet de s'adapter a n'importe quelle environement a partir du
        // chemin absolue
        String basePath = System.getProperty("user.dir");
        String cheminDossier = basePath + "/donnees/sauvegardes/" + dossierFichSelec;

        try (BufferedReader reader = new BufferedReader(new FileReader(cheminDossier))) 
        {
            String ligne;

            while ((ligne = reader.readLine()) != null) 
            {
                if (ligne.contains("/")) 
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

                    this.hashCoordonnees.put(nomClass, new int[] { x, y });
                }
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    public Map<String, int[]> gethashCoordonnees() 
    {
        return this.hashCoordonnees;
    }

    public String getCheminDossier() 
    {
        return this.cheminDossier;
    }

    

    public void sauvegarderClasses(List<BlocClasse> listBlocClasses, String cheminProjet) 
    {
        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/";
        String   fichierLectureEcriture = cheminPath + "projets.xml";

        int      indiceslash            = cheminProjet.lastIndexOf("/");
        String   nomProjetASauv         = cheminProjet.substring(indiceslash + 1).trim();

        // Vérifier si le projet est déjà sauvegardé
        if (this.projetEstSauvegarde(nomProjetASauv)) 
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



    public void sauvegarderCoordProjet(List<BlocClasse> listBlocClasses, String nomProjet, String cheminProjet)
    {
        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/sauvegardes/";
        File file                       = new File(cheminPath + nomProjet + ".xml");

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

    public boolean fichierDeSauvegardeExiste(String nomIntitule) {
        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/sauvegardes/" ;

        File file = new File(cheminPath + nomIntitule + ".xml");

        return file.exists();
    }

    public HashMap<String, BlocClasse> chargerSauvegardeCoord(String nomFichier,  HashMap<String, BlocClasse> mapBlocClasse)
    {
        HashMap<String, BlocClasse> mapNouvBlocClasse = new HashMap<>();

        String   basePath               = System.getProperty("user.dir");

        //System.out.println(basePath);
        String   cheminPath             = basePath + "/donnees/sauvegardes/";

        //System.out.println(cheminPath);

        File file = new File(cheminPath + nomFichier + ".xml");

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
    }
    
    public String getIntituleFromLien(String paraCheminDossier) {

        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/projets.xml";

        try(Scanner scan = new Scanner(new File(cheminPath))) 
        {
            while(scan.hasNextLine())
            {
                String ligne = scan.nextLine();
                
                String[] tabLigne = ligne.split("\t");

                System.out.println(tabLigne);
                if(tabLigne[0].equals(paraCheminDossier.trim()))
                {
                    return tabLigne[1].trim();
                }
            }
            
        } 
        catch (Exception e) 
        {
            e.getMessage();
        }

        return "";
    }

    public boolean projetEstSauvegarde(String nomIntituleSauvegarde) {

        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/projets.xml";

        try(Scanner scan = new Scanner(new File(cheminPath))) 
        {
            while(scan.hasNextLine())
            {
                String ligne = scan.nextLine();
                
                String[] tabLigne = ligne.split("\t");

                System.out.println(tabLigne);
                if(tabLigne[1].equals(nomIntituleSauvegarde.trim()))
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
        int indiceslash = cheminFichier.lastIndexOf("/");
        String nomProjet = cheminFichier.substring(indiceslash + 1).trim();
        
        // Vérifier si le projet est déjà enregistré
        if (!projetEstSauvegarde(nomProjet))
        {
            // Ajouter le projet à projets.xml
            String basePath = System.getProperty("user.dir");
            String fichierPath = basePath + "/donnees/projets.xml";
            File fichier = new File(fichierPath);
            
            try
            {
                // Création du dossier parent si nécessaire
                if (!fichier.getParentFile().exists())
                {
                    fichier.getParentFile().mkdirs();
                }
                
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
}
