package metier;

public class Parametre
{
	private String nomPara;
	private String typePara;

	public Parametre(String nomPara, String typePara)
	{
		this.nomPara  = nomPara;
		this.typePara = typePara;
	}

	public String getNomPara() 
	{ 
		return this.nomPara; 
	}

	public String getTypePara() 
	{
		return this.typePara;
	}
	
	public void setNomPara(String nomPara) 
	{
		this.nomPara = nomPara;
	}
	public void setTypePara(String typePara) 
	{
		this.typePara = typePara;
	}
	
	public String getContenue() 
	{
		return "nom : " + this.nomPara + " type : " + this.typePara;
	}

	
}
