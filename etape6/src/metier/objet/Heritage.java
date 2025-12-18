package metier.objet;

/**
 * Représente une relation d'héritage entre deux classes.
 *
 * Cette classe étend {@link Liaison} et modélise le fait qu'une classe
 * (classeOrig) hérite d'une autre classe (classeDest).
 */
public class Heritage extends Liaison 
{
	/**
	 * Construit une relation d'héritage entre une classe d'origine et une classe
	 * destination.
	 *
	 * @param classeDest La classe parente (superclasse)
	 * @param classeOrig La classe qui hérite (sous-classe)
	 */
	public Heritage(Classe classeDest, Classe classeOrig) 
	{
		super(classeDest, classeOrig);
	}

	public String toString() 
	{
		return classeOrig.getNom() + " hérite de " + classeDest.getNom();
	}
}