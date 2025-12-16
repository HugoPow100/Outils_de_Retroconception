package metier;

import java.util.ArrayList;

public class Classe 
{

	private String              nom;
	private boolean             isAbstract;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode>  lstMethode;

	public Classe(String nom, boolean isAbstract, ArrayList<Attribut> lstAttribut, ArrayList<Methode> lstMethode)
	{
		this.nom         = nom;
		this.isAbstract  = isAbstract;
		this.lstAttribut = lstAttribut;
		this.lstMethode  = lstMethode;
	}

	public ArrayList<Attribut> getLstAttribut()	{ return this.lstAttribut; }

	public ArrayList<Methode > getLstMethode () { return this.lstMethode;  }

	public String getNom() { return this.nom; }

	public boolean isAbstract() { return this.isAbstract; }
}