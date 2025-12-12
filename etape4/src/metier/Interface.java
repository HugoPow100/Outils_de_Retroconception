package metier;

public class Interface extends Liaison
{
	public Interface(Classe classeDest, Classe classeOrig)
	{
		super(classeDest, classeOrig);
	}

	public String toString() {
		return classeDest.getNom() + " implémente " + classeOrig.getNom();
	}
}
