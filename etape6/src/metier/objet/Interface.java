package metier.objet;

/**
 * Représente la relation d'implémentation d'une interface par une classe.
 *
 * Cette classe étend {@link Liaison} et modélise le fait qu'une classe 
 * (classeOrig) implémente une interface (classeDest).
 */
public class Interface extends Liaison
{
	/**
	 * Construit une relation d'implémentation entre une classe et une 
	 * interface.
	 *
	 * @param classeDest L'interface implémentée
	 * @param classeOrig La classe qui implémente l'interface
	 */
	public Interface(Classe classeDest, Classe classeOrig)
	{
		super(classeDest, classeOrig);
	}

	public String toString() 
	{
		return classeOrig.getNom() + " implémente " + classeDest.getNom();
	}
}
