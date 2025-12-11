package controlleur;

import java.util.*;
import metier.*;
import vue.BlocClasse;
import vue.LiaisonVue;
import vue.PanneauDiagramme;

public class Controlleur {
    private Lecture lecture;
    private List<LiaisonVue> liaisons;
    private PanneauDiagramme panneauDiagramme;
    private GestionSauvegarde gestionSauvegarde;

    public Controlleur(PanneauDiagramme panneauDiagramme) {
        this.panneauDiagramme = panneauDiagramme;
        this.liaisons = new ArrayList<>();
        this.gestionSauvegarde = new GestionSauvegarde();
    }

    public List<BlocClasse> chargerProjetEnBlocsClasses(String cheminProjet) {
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
        creerLiaisonsDepuisAssoc(lecture.getLstAssociation(), mapBlocsParNom);

        creerLiaisonsDepuisHerit(lecture.getLstHeritage(), mapBlocsParNom);

        panneauDiagramme.optimiserPositionsClasses();

        return blocs;
    }

    private BlocClasse creerBlocAPartirDeClasse(Classe classe, int x, int y) {
        BlocClasse bloc = new BlocClasse(classe.getNom(), x, y);

        List<String> attributsStr = new ArrayList<>();
        for (Attribut att : classe.getLstAttribut()) {
            String visibilite = att.getVisibilite();

            switch (visibilite) {
                case "public" -> visibilite = "+";
                case "private" -> visibilite = "-";
                case "package" -> visibilite = "#";
                case "protected" -> visibilite = "~";
            }

            String nomAtt = att.getNomAttribut();
            String typeAtt = att.getTypeAttribut();
            attributsStr.add(visibilite + " " + nomAtt + " : " + typeAtt);
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
