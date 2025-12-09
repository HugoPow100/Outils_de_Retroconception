package metier;

public class Multiplicite {

    // Integer.MAX_VALUE = * en affichage
    private int debut;
    private int fin;

    public Multiplicite(int debut, int fin) 
	{
		if (debut < 0)
			this.debut = 0;
		else
			this.debut = debut;

		if (fin < 0)
			this.fin   = 0;
		else
			this.fin   = fin;
    }

    public Multiplicite(int debut, String fin) 
	{
		if (debut < 0)
			this.debut = 0;
		else
			this.debut = debut;

		if (fin.trim().equals("*"))
			this.fin   = Integer.MAX_VALUE;
		else
			this.fin   = Integer.parseInt(fin);
    }

    public String toString() 
	{
        if (fin == Integer.MAX_VALUE)
            return debut + "..*";
        return debut + ".." + fin;
    }
}