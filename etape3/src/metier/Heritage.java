package metier;

public class Heritage extends Liaison {

    public Heritage(String nomAttribut, Classe classeDest, Classe classeOrig) {
        super(nomAttribut, classeDest, classeOrig);
    }

    @Override
    public String toString() {
        return classeOrig.getNom() + " hérite de " + classeDest.getNom();
    }

}
