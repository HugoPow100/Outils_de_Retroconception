package metier;

public class Association extends Liaison {

    private boolean unidirectionnel = false;
    private Multiplicite multOrig;
    private Multiplicite multDest;
    private int num;

    private static int nbAssoc;

    public Association(Classe classeDest, Classe classeOrig,
            Multiplicite multDest, Multiplicite multOrig, boolean unidirectionnel) {
        super(classeDest, classeOrig);
        this.multDest = multDest;
        this.multOrig = multOrig;
        this.unidirectionnel = unidirectionnel;
        num = ++nbAssoc;
    }

    public boolean isUnidirectionnel() {
        return unidirectionnel;
    }

    public void rendreBidirectionnel() 
    {
        this.unidirectionnel = false;
    }

    public void rendreUnidirectionnel() 
    {
        this.unidirectionnel = true;
    }

    public Multiplicite getMultOrig() {
        return multOrig;
    }

    public void setMultOrig(Multiplicite multOrig) {
        this.multOrig = multOrig;
    }

    public Multiplicite getMultDest() {
        return multDest;
    }

    public void setMultDest(Multiplicite multDest) {
        this.multDest = multDest;
    }

    @Override
    public String toString() {
        String sens = (this.unidirectionnel) ? "unidirectionnelle" : "bidirectionnelle";
        String origine = (this.classeOrig != null) ? this.classeOrig.getNom() : "?";
        String dest = (this.classeDest != null) ? this.classeDest.getNom() : "?";
        String multO = (this.multOrig != null) ? this.multOrig.toString() : "?";
        String multD = (this.multDest != null) ? this.multDest.toString() : "?";

        return String.format("Association %d : %s de %s(%s) vers %s(%s)",
                this.getNum(), sens, origine, multO, dest, multD);
    }

    public int getNum() {
        return this.num;
    }

}
