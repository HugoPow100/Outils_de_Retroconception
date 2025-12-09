package controlleur;

import java.util.*;
import metier.*;
import vue.BlocClasse;
import vue.LiaisonVue;

public class Controlleur {
    private Lecture lecture;
    private List<LiaisonVue> liaisons;

    public Controlleur() {
        this.liaisons = new ArrayList<>();
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

        // Créer les liaisons (placeholders pour le moment)
        creerLiaisonsPlaceholder(blocs, mapBlocsParNom);

        return blocs;
    }

    private void creerLiaisonsPlaceholder(List<BlocClasse> blocs, HashMap<String, BlocClasse> mapBlocsParNom) {
        // TODO: À remplacer par les liaisons du métier une fois prêtes
        // Exemple de liaisons placeholders
        if (blocs.size() >= 2) {
            LiaisonVue liaison1 = new LiaisonVue(blocs.get(0), blocs.get(1), "heritage");
            liaisons.add(liaison1);
        }
        if (blocs.size() >= 3) {
            LiaisonVue liaison2 = new LiaisonVue(blocs.get(1), blocs.get(2), "association");
            liaisons.add(liaison2);
        }
        if (blocs.size() >= 4) {
            LiaisonVue liaison3 = new LiaisonVue(blocs.get(2), blocs.get(3), "interface");
            liaisons.add(liaison3);
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


    private BlocClasse creerBlocAPartirDeClasse(Classe classe, int x, int y) {
        BlocClasse bloc = new BlocClasse(classe.getNom(), x, y);
        
        List<String> attributsStr = new ArrayList<>();
        for (Attribut att : classe.getLstAttribut()) {
            String visibilite = att.getVisibilite();


            switch (visibilite)
            {
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

            switch (visibilite)
            {
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

    
}
