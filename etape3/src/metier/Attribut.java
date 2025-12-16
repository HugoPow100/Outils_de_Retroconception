package metier;

public class Attribut {

	private String nomAttribut;
	private String type;
	private String visibilite;
	private String portee;

	public Attribut(String nomAttribut, String type, String visibilite, String portee)
	{
		this.nomAttribut = nomAttribut;
		this.type = type;
		this.visibilite = visibilite;
		this.portee = portee;
	}
	

	public String getNom() {
		return this.nomAttribut;
	}

	public String getType() {
		return this.type;
	}

	public String getVisibilite() {
		return this.visibilite;
	}

	public String getPortee() {
		return this.portee;
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
