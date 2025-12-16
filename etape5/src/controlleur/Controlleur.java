package controlleur;

import java.util.*;
import java.io.File;
import metier.lecture.*;
import metier.objet.*;
import metier.sauvegarde.*;
import vue.BlocClasse;
import vue.FenetrePrincipale;
import vue.LiaisonVue;

/**
* Contrôleur qui met en relation le métier et la vue IHM.
* A accès à la fenêtre principale de la vue, et à la classe de lecture
* @author Jules, Thibault, Hugo
*/
public class Controlleur 
{
    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

    private Lecture             lecture;
    private List<LiaisonVue>    lstLiaisons;
    private FenetrePrincipale   fenetrePrincipale;
    private GestionSauvegarde   gestionSauvegarde;

    private List<BlocClasse>    lstBlocs;

    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//

    /**
    * Constructeur du controlleur
    * @param classe La classe sur laquelle se base le BlocClasse 
    */
    public Controlleur(FenetrePrincipale fenetrePrincipale) 
    {
        this.fenetrePrincipale  = fenetrePrincipale;
        this.lstLiaisons        = new ArrayList<>();
        this.gestionSauvegarde  = new GestionSauvegarde(this);

        this.lstBlocs           = new ArrayList<>();
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    /**
    * Charge un projet de classes .java pour convertir son contenu en liste de BlocClasse.
    * @param cheminProjet Le chemin du projet
    * @return La liste des BlocClasse generés
    */
    public List<BlocClasse> chargerProjetEnBlocsClasses(String cheminProjet) 
    {
        lecture = new Lecture(cheminProjet);
        lstBlocs.clear();
        lstLiaisons.clear();
        lstBlocs.clear();

        // hasmap pour associer les noms de classes aux blocs
        HashMap<String, BlocClasse> mapBlocsParNom  = new HashMap<>();
        HashMap<String, Classe>     hashMapclasses  = lecture.getHashMapClasses();

        String nomDeSauvegardeProjet = estSauvegarde(cheminProjet, null);
        
        System.out.println(nomDeSauvegardeProjet);

        
        int posX    = 50;
        int posY    = 50;

        for (Classe classe : hashMapclasses.values()) 
        {
            if (classe != null) 
            {
                BlocClasse bloc = creerBlocAPartirDeClasse(classe, posX, posY);
                this.lstBlocs.add(bloc);
                mapBlocsParNom.put(classe.getNom(), bloc);

                posX += 250;
                if (posX > 1000) 
                {
                    posX    = 50;
                    posY    += 200;
                }
            }
        }
    

        if(!nomDeSauvegardeProjet.equals(""))
        {
            mapBlocsParNom = gestionSauvegarde.chargerSauvegardeCoord(nomDeSauvegardeProjet, mapBlocsParNom);   
        }
        

        // Créer les lstLiaisons depuis associations, heritages, et interfaces
        creerLiaisonsDepuisAssoc        (lecture.getLstAssociation(), mapBlocsParNom);

        creerLiaisonsDepuisHerit        (lecture.getLstHeritage(), mapBlocsParNom);
        creerLiaisonsDepuisInterface(lecture.getLstInterface(), mapBlocsParNom);

        creerLiaisonsDepuisInterface  (lecture.getLstInterface(), mapBlocsParNom);

        fenetrePrincipale.optimiserPositionsClasses();

        return this.lstBlocs;
    }

    /**
    * Crée un BlocClasse à partir d'une Classe
    * @param classe La classe sur laquelle se base le BlocClasse 
    * @param x Abcisse du bloc à créer
    * @param y Ordonnée du bloc à créer
    * @return Le bloc classe créé
    */
    public BlocClasse creerBlocAPartirDeClasse(Classe classe, int x, int y) 
    {
        BlocClasse bloc = new BlocClasse(classe.getNom(), x, y);

        // Définir si c'est une interface
        bloc.setInterface(classe.isInterface());

        // Traitement de la liste des attributs
        List<String> attributsStr = new ArrayList<>();
        for (Attribut att : classe.getLstAttribut()) {
            String sRet;

            String visibilite = att.getVisibilite();

            switch (visibilite) {
                case "public" -> visibilite = "+";
                case "private" -> visibilite = "-";
                case "package" -> visibilite = "#";
                case "protected" -> visibilite = "~";
            }

            sRet = visibilite + " ";
            
            sRet += att.getNom() + " : ";
            
            sRet += att.getType();

            if (att.isConstant()) {
                sRet += " {frozen}";
            }

            if (att.getPortee().equals("classe")) {
                // Pour souligner
                sRet = "\u001B[4m" + sRet + "\u001B[0m";
            }

            attributsStr.add(sRet);
        }

        // Traitement de la liste des méthodes
        List<String> methodesStr = new ArrayList<>();
        for (Methode met : classe.getLstMethode()) 
        {
            String visibilite = met.getVisibilite();

            switch (visibilite) {
                case "public" -> visibilite = "+";
                case "private" -> visibilite = "-";
                case "package" -> visibilite = "#";
                case "protected" -> visibilite = "~";
            }

            String nomMet = met.getNomMethode();
            String retour = met.getRetour();
            methodesStr.add(visibilite + " " + nomMet + "() : " + retour);
        }

        bloc.setAttributsAffichage(attributsStr);
        bloc.setMethodesAffichage(methodesStr);

        return bloc;
    }

    /**
    * Ajoute des liasons à lstLiaisons en se basant sur une liste d'{@link Association}s
    * @param lstAssoc La list d'{@link Association}s sur laquelle baser les lstLiaisons
    * @param mapBlocsParNom {@link HashMap<String, BlocClasse>} de String, BlocClasse avec le nom de chaque bloc et chaque bloc
    */
    private void creerLiaisonsDepuisAssoc(List<Association> lstAssoc, HashMap<String, BlocClasse> mapBlocsParNom) {
        for (Association assoc : lstAssoc) {
            String multOrig = (assoc.getMultOrig() != null) ? assoc.getMultOrig().toString() : "";
            String multDest = (assoc.getMultDest() != null) ? assoc.getMultDest().toString() : "";
            BlocClasse blocOrigine = mapBlocsParNom.get(assoc.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(assoc.getClasseDest().getNom());

            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "association", assoc.isUnidirectionnel(),
                    multOrig, multDest);
            lstLiaisons.add(liaison);
        }
    }

    /**
    * Ajoute des liasons à lstLiaisons en se basant sur une liste d'{@link Heritage}s
    * @param lstAssoc La list d'{@link Heritage}s sur laquelle baser les lstLiaisons
    * @param mapBlocsParNom {@link HashMap<String, BlocClasse>} de String, BlocClasse avec le nom de chaque bloc et chaque bloc
    */
    private void creerLiaisonsDepuisHerit(List<Heritage> lstHerit, HashMap<String, BlocClasse> mapBlocsParNom) {

        for (Heritage herit : lstHerit) {
            BlocClasse blocOrigine = mapBlocsParNom.get(herit.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(herit.getClasseDest().getNom());
            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "heritage");
            lstLiaisons.add(liaison);
        }
    }

    /**
    * Ajoute des liasons à lstLiaisons en se basant sur une liste d'{@link Interface}s
    * @param lstAssoc La list d'{@link Interface}s sur laquelle baser les lstLiaisons
    * @param mapBlocsParNom {@link HashMap<String, BlocClasse>} de String, BlocClasse avec le nom de chaque bloc et chaque bloc
    */
    private void creerLiaisonsDepuisInterface(List<Interface> lstInter, HashMap<String, BlocClasse> mapBlocsParNom) {

        for (Interface inter : lstInter) {
            BlocClasse blocOrigine = mapBlocsParNom.get(inter.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(inter.getClasseDest().getNom());
            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "interface");
            lstLiaisons.add(liaison);
            System.out.println("Interface ajoutée : " + inter);
        }
    }


    /**
     * Verifie que il existe une sauvegarde d'un projet deja existant
     * @param paraCheminDossier
     * @return
     */
    public String estSauvegarde(String paraCheminDossier, String nomFichierCoord)
    {

        if(paraCheminDossier!= null)
        {
            String   basePath               = System.getProperty("user.dir");
            String   cheminPath             = basePath + "/donnees/projets.xml";

            try(Scanner scan = new Scanner(new File(cheminPath))) 
            {
                while(scan.hasNextLine())
                {
                    String ligne = scan.nextLine();
                    
                    String[] tabCheminProjet = ligne.split("\t");

                    if(tabCheminProjet[0].equals(paraCheminDossier.trim()))
                    {
                        return tabCheminProjet[1].trim();
                    }
                }
                
            } 
            catch (Exception e) 
            {
                e.getMessage();
            }

        }
        else
        {
            String   basePath               = System.getProperty("user.dir");
            String   cheminPath             = basePath + "/donnees/sauvegardes/";

            File dossier = new File(cheminPath);

            if (!dossier.exists() || !dossier.isDirectory()) 
            {
                System.out.println("Dossier de sauvegardes introuvable");
                return "";
            }

            File[] fichiers = dossier.listFiles();

            if (fichiers == null) 
            {
                System.out.println("Aucun fichier dans le dossier");
                return "";
            }

            for (File f : fichiers) 
            {
                if (f.isFile()) 
                {
                    System.out.println("Fichier trouvé : " + f.getName());

                    if (f.getName().equals(nomFichierCoord.trim())) 
                    {
                        return f.getName();
                    }
                }
            }
        }
        
        return "";
    }

    public void sauvegarderClasses(List<BlocClasse> blocClasses, String cheminProjet) {
        gestionSauvegarde.sauvegarderClasses(blocClasses, cheminProjet);
    }

    //-----------//
    //  GETTERS  //
    //-----------//

    public List<LiaisonVue> getLiaisons() {
        return lstLiaisons;
    }

    public void ajouterBlockList(BlocClasse block)
    {
        this.lstBlocs.add(block);
    }

    
}
