package metier;

import java.util.ArrayList;

public class Classe 
{

	private String             nom;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	public Classe(String nom ,ArrayList<Attribut> lstAttribut, ArrayList<Methode> lstMethode)
	{
		this.nom      = nom;
		this.lstAttribut = lstAttribut;
		this.lstMethode  = lstMethode;
	}

	public ArrayList<Attribut> getLstAttribut()	{ return this.lstAttribut; }

	public ArrayList<Methode > getLstMethode () { return this.lstMethode;  }

	public String getNom() { return this.nom; }
}