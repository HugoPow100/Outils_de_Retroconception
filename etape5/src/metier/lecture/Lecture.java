package metier.lecture;


import metier.Classe;
import metier.Attribut;
import metier.Methode;
import metier.Parametre;
import metier.Heritage;
import metier.Association;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Lecture
{
    private UtilLecture             utilLecture;
    private AnalyseAssociation      analyseAssociation;
    private ScannerClasse           scannerClasse;

	private HashMap<String, Classe> hashMapClasses;
	private ArrayList<Classe>       lstClasse;
	private ArrayList<Attribut>     lstAttribut;
	private ArrayList<Methode>      lstMethode;
	private ArrayList<Heritage>     lstHeritage;
	private ArrayList<Association>  lstAssociations;
	private ArrayList<String>       lstNomFichier;

	public Lecture(String nom) 
    {
        this.utilLecture        = new UtilLecture(this);
        this.analyseAssociation = new AnalyseAssociation(this.utilLecture);
		this.scannerClasse      = new ScannerClasse(this);

		this.hashMapClasses  = new HashMap<String, Classe>();
		this.lstAttribut     = new ArrayList<Attribut>();
		this.lstMethode      = new ArrayList<Methode>();
		this.lstHeritage     = new ArrayList<Heritage>();
		this.lstNomFichier   = new ArrayList<String>();
		this.lstAssociations = new ArrayList<Association>();

		analyserFichier(nom);
	}

	public void analyserFichier(String paraCheminFichier) 
	{
		Scanner scFic;

		File f = new File(paraCheminFichier);

		List<String> lstCheminFich = new ArrayList<String>();
		this.lstClasse = new ArrayList<Classe>();

		// ----- Si c'est un répertoire -----
		if (f.isDirectory()) // fichier ou dossier ( dossier pour Directory )
		{
			this.hashMapClasses = new HashMap<>();

			// Récupére tous les fichiers du dossier
			File[] tabFichiers = f.listFiles();

			if (tabFichiers != null)
			{
				for (File file : tabFichiers) 
				{
					lstCheminFich.add(file.getAbsolutePath()); // récupere tous les chemins de chaque fichier

					Path p = file.toPath();
					String nomFichier = String.valueOf(p.getFileName()).replace(".java", "");

					this.lstNomFichier.add(nomFichier);
				}

				// Libére la mémoire
				tabFichiers = null;
			}
		}

		try
		{

			if (!lstCheminFich.isEmpty()) 
			{
				for (String chemin : lstCheminFich) 
				{
					scFic = new Scanner(new FileInputStream(chemin), "UTF8");

					Path p = Paths.get(chemin);
					String nomFichier = String.valueOf(p.getFileName());

					Classe classePourCeFichier;
					classePourCeFichier = this.scannerClasse.scanne(scFic, nomFichier);

                    this.hashMapClasses.put(nomFichier, classePourCeFichier);

                    scFic.close();
                }
                
				lstCheminFich.clear();
			} 
			else 
			{
				scFic = new Scanner(new FileInputStream(paraCheminFichier), "UTF8");

				Path p = Paths.get(paraCheminFichier);
				String nomFichier = String.valueOf(p.getFileName());

				Classe classePourCeFichier;
				classePourCeFichier = this.scannerClasse.scanne(scFic, nomFichier);

				this.hashMapClasses.put(nomFichier, classePourCeFichier);

				scFic.close();
			}

			this.analyseAssociation.genererAssociation(this.hashMapClasses);

			for (Classe classe : hashMapClasses.values())
			{
				String nomParent = classe.getClasseParente();

				if (nomParent != null && !nomParent.isEmpty())
				{
					Classe classParent = getClasse(nomParent);

					if (classParent != null)
					{
						Heritage heritage = new Heritage(classParent, classe);
						lstHeritage.add(heritage);

						System.out.println(heritage);
					}
					// Cette classe a bien une classe parente
					// Maintenant cherche la classe parente et crée Heritage
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}

    // Recupere une classe en fonction du nom
	private Classe getClasse(String nomFichier)
	{
		for (Classe classe : hashMapClasses.values())
		{
			if (classe.getNom().equals(nomFichier))
				return classe;
		}
		return null;
	}


    public void setHashMapClasses(HashMap<String, Classe> hashMapClasses) {
        this.hashMapClasses = hashMapClasses;
    }

    public void setLstClasse(ArrayList<Classe> lstClasse) {
        this.lstClasse = lstClasse;
    }

    public void setLstAttribut(ArrayList<Attribut> lstAttribut) {
        this.lstAttribut = lstAttribut;
    }

    public void setLstMethode(ArrayList<Methode> lstMethode) {
        this.lstMethode = lstMethode;
    }

    public void setLstHeritage(ArrayList<Heritage> lstHeritage) {
        this.lstHeritage = lstHeritage;
    }

    public void setLstAssociations(ArrayList<Association> lstAssociations) {
        this.lstAssociations = lstAssociations;
    }

    public void setLstNomFichier(ArrayList<String> lstNomFichier) {
        this.lstNomFichier = lstNomFichier;
    }

    public HashMap<String, Classe> getHashMapClasses() {
        return hashMapClasses;
    }

    public ArrayList<Classe> getLstClasse() {
        return lstClasse;
    }

    public ArrayList<Attribut> getLstAttribut() {
        return lstAttribut;
    }

    public ArrayList<Methode> getLstMethode() {
        return lstMethode;
    }

    public ArrayList<Heritage> getLstHeritage() {
        return lstHeritage;
    }

    public ArrayList<Association> getLstAssociations() {
        return lstAssociations;
    }

    public ArrayList<String> getLstNomFichier() {
        return lstNomFichier;
    }

    public UtilLecture getUtilLecture() {
        return utilLecture;
    }

    
}