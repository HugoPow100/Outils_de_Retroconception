package controlleur;

import metier.lecture.Lecture;
import java.util.*;
import metier.lecture.*;
import metier.GestionSauvegarde;
import metier.Association;
import metier.Classe;
import metier.Methode;
import metier.Interface;
import metier.Heritage;
import metier.Attribut;
import vue.BlocClasse;
import vue.LiaisonVue;
import vue.PanneauDiagramme;

public class Controlleur 
{
    private Lecture lecture;
    private List<LiaisonVue> liaisons;
    private PanneauDiagramme panneauDiagramme;
    private GestionSauvegarde gestionSauvegarde;

    public Controlleur(PanneauDiagramme panneauDiagramme) 
    {
        this.panneauDiagramme = panneauDiagramme;
        this.liaisons = new ArrayList<>();
        this.gestionSauvegarde = new GestionSauvegarde();
    }

    public List<BlocClasse> chargerProjetEnBlocsClasses(String cheminProjet) 
    {
        lecture = new Lecture(cheminProjet);
        liaisons.clear();

        List<BlocClasse> blocs = new ArrayList<>();
        HashMap<String, Classe> hashMapclasses = lecture.getHashMapClasses();

        // Créer une map pour associer les noms de classes aux blocs
        HashMap<String, BlocClasse> mapBlocsParNom = new HashMap<>();

        int posX = 50;
        int posY = 50;

        for (Classe classe : hashMapclasses.values()) {
            if (classe != null) {
                BlocClasse bloc = creerBlocAPartirDeClasse(classe, posX, posY);
                blocs.add(bloc);
                mapBlocsParNom.put(classe.getNom(), bloc);

                posX += 250;
                if (posX > 1000) {
                    posX = 50;
                    posY += 200;
                }
            }
        }

        // Créer les liaisons depuis associations, heritages, et interfaces
        creerLiaisonsDepuisAssoc(lecture.getLstAssociations(), mapBlocsParNom);

        creerLiaisonsDepuisHerit(lecture.getLstHeritage(), mapBlocsParNom);

        //creerLiaisonsDepuisInterface(lecture.getLstInterface(), mapBlocsParNom);

        panneauDiagramme.optimiserPositionsClasses();

        return blocs;
    }

    private BlocClasse creerBlocAPartirDeClasse(Classe classe, int x, int y) {
        BlocClasse bloc = new BlocClasse(classe.getNom(), x, y);

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

            // Souligner les attributs statiques (portée = "classe") avec code ANSI
            if (att.getPortee().equals("classe")) {
                // Code ANSI pour souligner : \u001B[4m au début, \u001B[0m à la fin
                sRet = "\u001B[4m" + sRet + "\u001B[0m";
            }

            attributsStr.add(sRet);
            
        }

        List<String> methodesStr = new ArrayList<>();
        for (Methode met : classe.getLstMethode()) {
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

    private void creerLiaisonsDepuisAssoc(List<Association> lstAssoc, HashMap<String, BlocClasse> mapBlocsParNom) {
        for (Association assoc : lstAssoc) {
            String multOrig = (assoc.getMultOrig() != null) ? assoc.getMultOrig().toString() : "";
            String multDest = (assoc.getMultDest() != null) ? assoc.getMultDest().toString() : "";
            BlocClasse blocOrigine = mapBlocsParNom.get(assoc.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(assoc.getClasseDest().getNom());

            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "association", assoc.isUnidirectionnel(),
                    multOrig, multDest);
            liaisons.add(liaison);
        }
    }

    private void creerLiaisonsDepuisHerit(List<Heritage> lstHerit, HashMap<String, BlocClasse> mapBlocsParNom) {

        for (Heritage herit : lstHerit) {
            BlocClasse blocOrigine = mapBlocsParNom.get(herit.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(herit.getClasseDest().getNom());
            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "heritage");
            liaisons.add(liaison);
        }
    }

    private void creerLiaisonsDepuisInterface(List<Interface> lstInter, HashMap<String, BlocClasse> mapBlocsParNom) {

        for (Interface inter : lstInter) {
            BlocClasse blocOrigine = mapBlocsParNom.get(inter.getClasseOrig().getNom());
            BlocClasse blocDestination = mapBlocsParNom.get(inter.getClasseDest().getNom());
            LiaisonVue liaison = new LiaisonVue(blocOrigine, blocDestination, "interface");
            liaisons.add(liaison);
        }
    }

    public List<LiaisonVue> getLiaisons() {
        return liaisons;
    }

    public void ajouterLiaison(LiaisonVue liaison) {
        liaisons.add(liaison);
    }

    public void supprimerLiaison(LiaisonVue liaison) {
        liaisons.remove(liaison);
    }

    public boolean estSauvegardee() {
        return false;
    }

    public void sauvegarderClasses(List<BlocClasse> blocClasses, String cheminProjet) {
        gestionSauvegarde.sauvegarderClasses(blocClasses, cheminProjet);
    }
}
