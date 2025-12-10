package metier;

public class Liaison 
{
    protected Classe classeOrig;
    protected Classe classeDest;

    public Liaison(Classe classeDest, Classe classeOrig) 
    {
        this.classeDest = classeDest;
        this.classeOrig = classeOrig;
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
