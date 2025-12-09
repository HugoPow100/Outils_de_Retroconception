package metier;

public class Interface extends Liaison {

    public Interface(String nomAttribut, Classe classeDest, Classe classeOrig) {
        super(nomAttribut, classeDest, classeOrig);
    }

    @Override
    public String toString() {
        return classeDest.getNom() + " implémente " + classeOrig.getNom();
    }

}
