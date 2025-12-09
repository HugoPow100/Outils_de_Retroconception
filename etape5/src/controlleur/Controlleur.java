package controlleur;

import java.util.*;
import metier.*;
import vue.BlocClasse;

public class Controlleur {
    private Lecture lecture;

    public Controlleur() {
    }

    public List<BlocClasse> chargerProjetEnBlocsClasses(String cheminProjet) {
        lecture = new Lecture(cheminProjet);

        List<BlocClasse> blocs = new ArrayList<>();
        HashMap<String, ArrayList<Classe>> hashMapclasses = lecture.getHashMapClasses();
        
        int posX = 50;
        int posY = 50;

        for (ArrayList<Classe> listeClasses : hashMapclasses.values()) {
            for (Classe classe : listeClasses) {
                if (classe != null) {
                    BlocClasse bloc = creerBlocAPartirDeClasse(classe, posX, posY);
                    blocs.add(bloc);
                    
                    posX += 250;
                    if (posX > 1000) {
                        posX = 50;
                        posY += 200;
                    }
                }
            }
        }

        return blocs;
    }

    private BlocClasse creerBlocAPartirDeClasse(Classe classe, int x, int y) {
        BlocClasse bloc = new BlocClasse(classe.getNom(), x, y);
        
        List<String> attributsStr = new ArrayList<>();
        for (Attribut att : classe.getLstAttribut()) {
            String visibilite = att.getVisibilite();
            String nomAtt = att.getNomAttribut();
            String typeAtt = att.getTypeAttribut();
            attributsStr.add(visibilite + " " + nomAtt + " : " + typeAtt);
        }
        
        List<String> methodesStr = new ArrayList<>();
        for (Methode met : classe.getLstMethode()) {
            String visibilite = met.getVisibilite();
            String nomMet = met.getNomMethode();
            String retour = met.getRetour();
            methodesStr.add(visibilite + " " + nomMet + "() : " + retour);
        }
        
        bloc.setAttributsAffichage(attributsStr);
        bloc.setMethodesAffichage(methodesStr);
        
        return bloc;
    }
}
