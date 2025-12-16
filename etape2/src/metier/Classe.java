package metier;

import java.util.ArrayList;

public class Classe 
{

	private String             nom;
	private boolean            isAbstract;
	private boolean            isInterface;
	private boolean            isRecord;
	private boolean            isEnum;
	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode> lstMethode;

	public Classe(String nom, boolean isAbstract, boolean isInterface, boolean isRecord, boolean isEnum, ArrayList<Attribut> lstAttribut, ArrayList<Methode> lstMethode)
	{
		this.nom         = nom;
		this.isAbstract  = isAbstract;
		this.isInterface = isInterface;
		this.isRecord    = isRecord;
		this.isEnum      = isEnum;
		this.lstAttribut = lstAttribut;
		this.lstMethode  = lstMethode;

        if (isInterface) {
            System.out.println("Interface détectée : " + nom);
        }
        if (isRecord) {
            System.out.println("Record détecté : " + nom);
        }
        if (isEnum) {
            System.out.println("Enum détecté : " + nom);
        }
	}

	public ArrayList<Attribut> getLstAttribut()	{ return this.lstAttribut; }

	public ArrayList<Methode > getLstMethode () { return this.lstMethode;  }

	public String getNom() { return this.nom; }

	public boolean isAbstract() { return this.isAbstract; }

    public boolean isInterface() { return this.isInterface; }

    public boolean isRecord() { return this.isRecord; }

    public boolean isEnum() { return this.isEnum; }
}