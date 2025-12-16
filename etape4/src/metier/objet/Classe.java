package metier.objet;

import java.util.ArrayList;

/**
 * Représente une classe Java.
 *
 * Contient ses attributs, méthodes, type (class, interface, enum, record).
 */
public class Classe 
{
	private String  nom          ;
	private String  typeClasse   ;
	private String  classeParente;
	private boolean isHeritage   ;
	private String  nomInterface ;

	private ArrayList<Attribut> lstAttribut;
	private ArrayList<Methode > lstMethode ;

	/**
	 * Constructeur complet d'une classe analysée.
	 *
	 * @param nom           nom de la classe
	 * @param classeParente nom de la classe héritée (si applicable)
	 * @param typeClasse    type de classe (class, abstract, ect)
	 * @param isHeritage    true si la classe hérite d'une autre
	 * @param nomInterface  nom de l'interface implémentée
	 * @param lstAttribut   liste d'attributs trouvés dans la classe
	 * @param lstMethode    liste de méthodes trouvées dans la classe
	 */
	public Classe(String              nom          ,
	              String              classeParente,
	              String              typeClasse   ,
	              boolean             isHeritage   ,
	              String              nomInterface ,
	              ArrayList<Attribut> lstAttribut  ,
	              ArrayList<Methode>  lstMethode     )
	{
		this.nom           = nom          ;
		this.classeParente = classeParente;
		this.typeClasse    = typeClasse   ;
		this.isHeritage    = isHeritage   ;
		this.nomInterface  = nomInterface ;
		this.lstAttribut   = lstAttribut  ;
		this.lstMethode    = lstMethode   ;
	}

	public String              getNom          () { return this.nom          ; }
	public String              getClasseParente() { return this.classeParente; }
	public boolean             getIsHeritage   () { return this.isHeritage   ; }
	public String              getTypeClasse   () { return this.typeClasse   ; }
	public String              getNomInterface () { return this.nomInterface ; }
	public ArrayList<Attribut> getLstAttribut  () { return this.lstAttribut  ; }
	public ArrayList<Methode > getLstMethode   () { return this.lstMethode   ; }

	public boolean isEnum() {return this.typeClasse.contains("enum");}

	public boolean isAbstract() {return this.typeClasse.contains("abstract");}

	public boolean isInterface() {return this.typeClasse.contains("interface");}

	public boolean isRecord() {return this.typeClasse.contains("record");}
}