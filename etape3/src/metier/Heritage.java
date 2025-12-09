package metier;

public class Heritage extends Liaison
{

    public Heritage(Classe classeDest, Classe classeOrig) {
        super( classeDest, classeOrig);
    }

    @Override
    public String toString() {
        return classeOrig.getNom() + " hérite de " + classeDest.getNom();
    }

}