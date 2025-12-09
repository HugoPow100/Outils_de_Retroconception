package metier;

public class Liaison 
{
    protected String nomAttribut; // Non utilisé en affichage
    protected Classe classeOrig;
    protected Classe classeDest;

    public Liaison(String nomAttribut, Classe classeDest, Classe classeOrig) 
    {
        this.classeDest = classeDest;
        this.classeOrig = classeOrig;
        this.nomAttribut = nomAttribut;
    }

    public String getNomAttribut() 
    {
        return nomAttribut;
    }

    public void setNomAttribut(String nomAttribut) 
    {
        this.nomAttribut = nomAttribut;
    }

    public Classe getClasseOrig() 
    {
        return classeOrig;
    }

    public void setClasseOrig(Classe classeOrig) 
    {
        this.classeOrig = classeOrig;
    }

    public Classe getClasseDest() 
    {
        return classeDest;
    }

    public void setClasseDest(Classe classeDest) 
    {
        this.classeDest = classeDest;
    }

}
