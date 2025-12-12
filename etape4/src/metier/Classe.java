package metier;

import java.util.ArrayList;

public class Classe 
{

	private String             nom;
	private String typeClasse;
	private String classeParente;
	private boolean isHeritage;
	private String nomInterface;

	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	public Classe(String nom, 
	              String classeParente, 
				  String typeClasse,
	              boolean isHeritage,
				  String nomInterface,
	              ArrayList<Attribut> lstAttribut, 
	              ArrayList<Methode> lstMethode)
	{
		this.nom         = nom;
		this.classeParente = classeParente;
		this.typeClasse = typeClasse;
		this.isHeritage = isHeritage;
		this.nomInterface = nomInterface;
		this.lstAttribut = lstAttribut;
		this.lstMethode  = lstMethode;

		
		// if (isInterface)
		// {
		// 	System.out.println("Interface détectée : " + nom);
		// }
		// if (isRecord)
		// {
		// 	System.out.println("Record détecté : " + nom);
		// }
		// if (isEnum)
		// {
		// 	System.out.println("Enum détecté : " + nom);
		// }
	}

	public String getClasseParente() { return this.classeParente; }

	public boolean getIsHeritage() { return this.isHeritage; }

	public String getTypeClasse() { return this.typeClasse; }

	public ArrayList<Attribut> getLstAttribut()	{ return this.lstAttribut; }

	public ArrayList<Methode > getLstMethode () { return this.lstMethode;  }

	public boolean isEnum()
	{
		return this.typeClasse.contains("enum");
	}

	public boolean isAbstract()
	{
		return this.typeClasse.contains("abstract");
	}

	public boolean isInterface()
	{
		return this.typeClasse.contains("interface");
	}

	public boolean isRecord()
	{
		return this.typeClasse.contains("record");
	}

	public String getNom() { return this.nom; }

	public String getNomInterface() { return this.nomInterface; }
}