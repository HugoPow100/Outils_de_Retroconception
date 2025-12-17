package metier.objet;

/**
 * Représente un paramètre d'une méthode ou d'un constructeur.
 *
 * Un paramètre possède un nom et un type.
 * Cette classe permet de stocker et récupérer ces informations pour
 * les utiliser lors de l'analyse de classes ou de la génération de 
 * diagrammes.
 */
public class Parametre
{
	private String nomPara;
	private String typePara;

	public Parametre(String nomPara, String typePara)
	{
		this.nomPara  = nomPara;
		this.typePara = typePara;
	}

	public String getNomPara () { return this.nomPara ; }
	public String getTypePara() { return this.typePara; }

	public void setNomPara (String nomPara ) { this.nomPara  = nomPara ; }
	public void setTypePara(String typePara) { this.typePara = typePara; }

	public String getContenue()
	{
		return "nom : " + this.nomPara + " type : " + this.typePara;
	}
}
