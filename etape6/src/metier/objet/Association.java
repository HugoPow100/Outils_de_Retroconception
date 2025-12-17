package metier.objet;

/**
 * Représente une association UML entre deux classes.
 * Une association est caractérisée par une multiplicité à l’origine
 * et à la destination, ainsi qu’un caractère unidirectionnel ou
 * bidirectionnel pour l'IHM plus tard.
 *
 * Cette classe étend {@link Liaison} et représente une association 
 * UML entre deux classes, en y ajoutant le sens (uni/bidirectionnel) 
 * et les multiplicités entre origine et destination.
 *
 * Chaque instance d’Association possède un identifiant unique
 * généré automatiquement.
 */
public class Association extends Liaison
{
	/** Compteur global pour attribuer un numéro unique à chaque association. */
	private static int nbAssoc;

	private boolean      unidirectionnel = false;
	private Multiplicite multOrig               ;
	private Multiplicite multDest               ;
	private int          num                    ;


	/**
	 * Construit une nouvelle association entre deux classes, avec leurs multiplicités
	 * respectives et la direction éventuelle de l’association.
	 *
	 * @param classeDest        classe située à la destination de l’association
	 * @param classeOrig        classe située à l’origine de l’association
	 * @param multDest          multiplicité côté destination
	 * @param multOrig          multiplicité côté origine
	 * @param unidirectionnel   true si l’association est unidirectionnelle, false sinon
	 */
	public Association(Classe classeDest, Classe classeOrig,
			           Multiplicite multDest, Multiplicite multOrig, boolean unidirectionnel)
	{
		super(classeDest, classeOrig);

		this.multDest        = multDest       ;
		this.multOrig        = multOrig       ;
		this.unidirectionnel = unidirectionnel;
		this.num             = ++nbAssoc      ;
	}

	public Multiplicite getMultDest      () { return this.multDest       ; }
	public Multiplicite getMultOrig      () { return this.multOrig       ; }
	public boolean      isUnidirectionnel() { return this.unidirectionnel; }
	public int          getNum           () { return this.num            ; }

	public void setMultDest       (Multiplicite multDest)   { this.multDest        = multDest       ; }
	public void setMultOrig       (Multiplicite multOrig)   { this.multOrig        = multOrig       ; }
	public void setUnidirectionnel(boolean unidirectionnel) { this.unidirectionnel = unidirectionnel; }


	
	/**
	 * Retourne une représentation textuelle complète de l’association,
	 * incluant le sens, les classes concernées et leurs multiplicités.
	 *
	 * @return description lisible de l’association
	 */
	public String toString()
	{
		String sens    = (this.unidirectionnel   ) ? "unidirectionnelle" : "bidirectionnelle";
		String origine = (this.classeOrig != null) ? this.classeOrig.getNom() : "?";
		String dest    = (this.classeDest != null) ? this.classeDest.getNom() : "?";
		String multO   = (this.multOrig   != null) ? this.multOrig.toString() : "?";
		String multD   = (this.multDest   != null) ? this.multDest.toString() : "?";

		return String.format("Association %d : %s de %s(%s) vers %s(%s)",
				this.getNum(), sens, origine, multO, dest, multD);
	}
}
