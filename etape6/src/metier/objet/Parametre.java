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
	private String nom ;
	private String type;

	public Parametre(String nom, String type)
	{
		this.nom  = nom ;
		this.type = type;
	}

	public String getNomPara () { return this.nom ; }
	public String getTypePara() { return this.type; }

	public void setNomPara (String nom ) { this.nom  = nom ; }
	public void setTypePara(String type) { this.type = type; }

	public String getContenue()
	{
		return "nom : " + this.nom + " type : " + this.type;
	}
}
