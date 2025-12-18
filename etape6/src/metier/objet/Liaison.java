package metier.objet;

/**
 * Représente une relation entre deux classes dans un diagramme.
 *
 * Une Liaison relie une classe source (classeOrig) à une classe cible
 * (classeDest).
 * Cette classe sert de base pour les relations spécialisées comme
 * {@link Heritage} ou {@link Interface}.
 */
public class Liaison 
{
	protected Classe classeOrig;
	protected Classe classeDest;

	public Liaison(Classe classeDest, Classe classeOrig) 
	{
		this.classeDest = classeDest;
		this.classeOrig = classeOrig;
	}

	public Classe getClasseOrig() { return classeOrig ; }
	public Classe getClasseDest() { return classeDest ; }
}
