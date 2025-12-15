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
        String cheminDossier = basePath + "/etape5/donnees/sauvegardes/" + dossierFichSelec;

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

        int      indiceslash            = cheminProjet.lastIndexOf("/");
        String   nomProjetASauv         = cheminProjet.substring(indiceslash +1 ).trim();

        String   fichierLectureEcriture = cheminPath + "projets.xml";
        int      nbrDossierMemeNom      = 0;
  
        try (BufferedReader br = new BufferedReader(new FileReader(fichierLectureEcriture))) 
        {
            String ligne;

            while ((ligne = br.readLine()) != null) 
            {
      
                int    indicePremierSlash = ligne.lastIndexOf("/");
                int    indiceTab          = ligne.indexOf("\t", indicePremierSlash);

                String nomDossierDejaSauv;

                if(indiceTab != -1)
                {
                    nomDossierDejaSauv = ligne.substring(indicePremierSlash +1, indiceTab);
                }
                else
                {
                    nomDossierDejaSauv = ligne.substring(indicePremierSlash +1).trim();
                }
                
                if(nomDossierDejaSauv.equals(nomProjetASauv))
                {
                    nbrDossierMemeNom ++;
                }

            }

        } 
        catch (FileNotFoundException e) 
        {
            // Si le fichier n'existe pas encore, on peut l'ignorer
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }



        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fichierLectureEcriture, true))) 
        {
            String ligneAAjouter = cheminProjet + "\t" + nomProjetASauv ;
            String nomProjet     = nomProjetASauv;

            if(nbrDossierMemeNom > 0)
            {
                ligneAAjouter +=  "_" + (nbrDossierMemeNom + 1);
                nomProjet     +=  "_" + (nbrDossierMemeNom + 1);
            }

            bw.write(ligneAAjouter);
            bw.newLine();

            sauvegarderCoordProjet(listBlocClasses, nomProjet, cheminProjet);

        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private void sauvegarderCoordProjet(List<BlocClasse> listBlocClasses, String nomProjet, String cheminProjet)
    {
        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/sauvegardes/";

        File file = new File(cheminPath + nomProjet + ".xml");

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) 
        {

            bw.write(cheminProjet);
            bw.newLine();

            for(BlocClasse blocClasse: listBlocClasses )
            {
                bw.write(   blocClasse.getNom().trim() + " " + 
                            blocClasse.getX()          + " " + 
                            blocClasse.getY())  ;

                bw.newLine();
            }
            

        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }


 

    public HashMap<String, BlocClasse> chargerSaugardeCoord(String nomFichier,  HashMap<String, Classe> mapClass)
    {
        String   basePath               = System.getProperty("user.dir");
        String   cheminPath             = basePath + "/donnees/sauvegardes/";

        File file = new File(cheminPath + nomFichier + ".xml");

        HashMap<String, BlocClasse> mapBlocsParNom = new HashMap<>();

        try (Scanner scanner = new Scanner(file)) 
        {
            while (scanner.hasNextLine()) 
            {
                String ligne = scanner.nextLine();

                if(!ligne.contains("/"))
                {
                    String[] tabClass = ligne.split("\\s+");
                    
                    Classe classe     = mapClass.get(tabClass[0].trim());
                    int    posX       = Integer.parseInt(tabClass[1].trim());
                    int    posY       = Integer.parseInt(tabClass[2].trim());

                    BlocClasse bloc = this.ctrl.creerBlocAPartirDeClasse(classe, posX, posY);
                    ctrl.getLstBlocs().add(bloc);
                    mapBlocsParNom.put(classe.getNom(), bloc);
                }
            }
        }
        catch (Exception e) 
        {
            e.getMessage();
        }

        return mapBlocsParNom;
    }
}
