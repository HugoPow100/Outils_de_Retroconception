package metier;

public class Attribut {

	private String nomAttribut;
	private String type;
	private String visibilite;
	private String porte;

	public Attribut(String nomAttribut, String type, String visibilite, String porte)
	{
		this.nomAttribut = nomAttribut;
		this.type = type;
		this.visibilite = visibilite;
		this.porte = porte;
	}

	public String getNomAttribut() {
		return this.nomAttribut;
	}

	public String getTypeAttribut() {
		return this.type;
	}

	public String getVisibilite() {
		return this.visibilite;
	}

	public String getPorte() {
		return this.porte;
	}

	public String toString()
    {
        String sRet;

        sRet = "";

        switch (this.visibilite) {
            case "public"    -> sRet = "+ ";
            case "private"   -> sRet = "- ";
            case "package"   -> sRet = "~ ";
            case "protected" -> sRet = "# ";
        }
        
        sRet += this.nomAttribut + "\t:";

        sRet += this.type;

        return sRet;
    }
}
