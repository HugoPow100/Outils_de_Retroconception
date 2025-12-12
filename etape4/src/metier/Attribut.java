package metier;

public class Attribut {

	private String nomAttribut;
	private String type;
	private String visibilite;
	private String porte;
	private boolean isConstant;

	public Attribut(String nomAttribut, String type, String visibilite, String porte, boolean isConstant)
	{
		this.nomAttribut = nomAttribut;
		this.type = type;
		this.visibilite = visibilite;
		this.porte = porte;
		this.isConstant = isConstant;
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

	public boolean isConstant() {
		return this.isConstant;
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
        
        sRet += this.nomAttribut + "\t: ";

        sRet += this.type;

        // Ajouter {frozen} pour les constantes
        if (this.isConstant) {
            sRet += " {frozen}";
        }

        // Souligner les attributs statiques (portée = "classe") avec code ANSI
        if (this.porte.equals("classe")) {
            // Code ANSI pour souligner : \u001B[4m au début, \u001B[0m à la fin
            sRet = "\u001B[4m" + sRet + "\u001B[0m";
        }

        return sRet;
    }
}
