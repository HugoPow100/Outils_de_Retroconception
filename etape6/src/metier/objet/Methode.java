package metier.objet;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une méthode d'une classe.
 *
 * Cette classe stocke le nom, le type de retour, la visibilité, 
 * l'abstraction et la liste des paramètres d'une méthode. Elle fournit 
 * également des méthodes pour manipuler et afficher ces informations.
 */
public class Methode
{
	private String          nomMethode  ;
	private String          retour      ;
	private String          visibilite  ;
	private boolean         isAbstract  ;
	private List<Parametre> lstParametre;

	public Methode(String nomMethode, String retour, String visibilite, 
	               boolean isAbstract, List<Parametre> lstParametre)
	{
		this.nomMethode   = nomMethode  ;
		this.visibilite   = visibilite  ;
		this.retour       = retour      ;
		this.isAbstract   = isAbstract  ;
		this.lstParametre = lstParametre;
	}

	public String          getNomMethode  () { return nomMethode  ; }
	public String          getVisibilite  () { return visibilite  ; }
	public String          getRetour      () { return retour      ; }
	public boolean         isAbstract     () { return isAbstract  ; }
	public List<Parametre> getLstParametre() { return lstParametre; }

	public void setNomMethode  (String nomMethode           ) { this.nomMethode   = nomMethode  ; }
	public void setVisibilite  (String visibilite           ) { this.visibilite   = visibilite  ; }
	public void setRetour      (String retour               ) { this.retour       = retour      ; }
	public void setAbstract    (boolean isAbstract          ) { this.isAbstract   = isAbstract  ; }
	public void setLstParametre(List<Parametre> lstParametre) { this.lstParametre = lstParametre; }

	public void ajouterParametre(Parametre p) { this.lstParametre.add(p); }

	public String toString()
	{
		String sRet = "";

		switch (this.visibilite)
		{
			case "public"    -> sRet = "+ ";
			case "private"   -> sRet = "- ";
			case "package"   -> sRet = "# ";
			case "protected" -> sRet = "~ ";
		}

		sRet += this.nomMethode + " (";

		for (int i = 0; i < this.lstParametre.size(); i++)
		{
			String param = this.lstParametre.get(i).getContenue();

			// Extraire le nom et le type du paramètre
			String[] parts = param.split(" type : ");
			if (parts.length >= 2)
			{
				String nomParam = parts[0].substring(parts[0].indexOf(":") + 2);
				String typeParam = parts[1];
				sRet += " " + nomParam + " : " + typeParam + " ";
			}

			if (i < this.lstParametre.size() - 1)
				sRet += ", ";
		}

		sRet += ")";
		String signature = sRet;

		if (!this.retour.isEmpty() && !this.retour.equals("void"))
		{
			sRet = String.format("%-40s : %s", signature, this.retour);
		}
		else sRet = signature;

		if (this.isAbstract)
			sRet += " {abstract}";

		return sRet;
	}

}
