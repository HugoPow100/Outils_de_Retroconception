package metier.objet;

/**
 * Représente un attribut trouvé dans une classe Java.
 * 
 * Cette classe stocke :
 * - le nom de l'attribut,
 * - son type,
 * - sa visibilité (public, private, protected, package),
 * - sa portée (instance ou classe, pour indiquer si c'est un attribut statique),
 * - le fait qu'il soit constant.
 *
 * La méthode toString fournit une représentation textuelle
 * dans un style proche de l'UML :
 * +  pour public
 * -  pour private
 * #  pour protected
 * ~  pour package
 *
 * Les attributs statiques (portée "classe") sont soulignés avec des codes ANSI.
 * Les constantes ajoutent l'indication {frozen}.
 */
public class Attribut
{
	private String  nomAttribut;
	private String  type       ;
	private String  visibilite ;
	private String  portee     ;
	private boolean isConstant ;

	/**
	 * Constructeur d'un attribut.
	 *
	 * @param nomAttribut nom de l'attribut
	 * @param type        type de l'attribut
	 * @param visibilite  visibilité UML
	 * @param portee      portée de l'attribut (instance ou classe)
	 * @param isConstant  true si l'attribut est une constante
	 */
	public Attribut(String nomAttribut, String type, String visibilite, String portee, boolean isConstant)
	{
		this.nomAttribut = nomAttribut;
		this.type        = type       ;
		this.visibilite  = visibilite ;
		this.portee      = portee     ;
		this.isConstant  = isConstant ;
	}

	public String  getNom			() { return this.nomAttribut; }
	public String  getType			() { return this.type       ; }
	public String  getVisibilite  	() { return this.visibilite ; }
	public String  getPortee     	() { return this.portee     ; }
	public boolean isConstant     	() { return this.isConstant ; }

	public String  toString()
	{
		String sRet;

		sRet = "";

		switch (this.visibilite)
		{
			case "public"    -> sRet = "+ ";
			case "private"   -> sRet = "- ";
			case "package"   -> sRet = "~ ";
			case "protected" -> sRet = "# ";
		}
		
		sRet += this.nomAttribut + "\t: ";

		sRet += this.type;

		// Ajouter {frozen} pour les constantes
		if (this.isConstant) sRet += " {frozen}";

		// Souligner les attributs statiques (portée = "classe") avec code ANSI
		if (this.portee.equals("classe"))
		{
			// Code ANSI pour souligner : \u001B[4m au début, \u001B[0m à la fin
			sRet = "\u001B[4m" + sRet + "\u001B[0m";
		}

		return sRet;
	}
}
