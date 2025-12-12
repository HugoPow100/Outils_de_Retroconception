package metier.lecture;

import metier.Association;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * UtilLecture
 * ------------
 * Fichier utilitaire contenant l’ensemble des méthodes de nettoyage
 * et de normalisation du code. Version reconstruite à partir de la
 * logique existante dans ton ancien projet.
 */
public class UtilLecture 
{
    private Lecture lecture;
    
    public UtilLecture(Lecture lecture) 
    {
        this.lecture = lecture;
    }
    
   /**
	 * Nettoie la liste des associations pour :
	 * - supprimer les doublons (ex : A→B et B→A)
	 * - transformer ces doublons en une seule association bidirectionnelle A↔B
	 * - conserver une seule association unique par couple de classes
	*/
	public void nettoyerAssociations(ArrayList<Association> lstAssociations) 
	{
		// Cette Map va contenir UNE seule association par paire de classes
		Map<String, Association> uniques = new HashMap<>();

		for (Association asso : lstAssociations) 
		{

			String origine = asso.getClasseOrig().getNom();
			String dest    = asso.getClasseDest().getNom();

			String key;

			if (origine.compareTo(dest) < 0) 
				key = origine + "-" + dest;
			else
				key = dest + "-" + origine;
		

			if (!uniques.containsKey(key)) 
			{
				// Première association → on la garde
				uniques.put(key, asso);
			}
			else 
			{
				// Une association opposée existe déjà
				Association exist = uniques.get(key);
			}
		}

		// On remplace la liste par la liste unique
		lecture.setLstAssociations(new ArrayList<>(uniques.values()));
	}


    /**
	 * Nettoie le type pour retirer List<>, Set<> ou [].
	 */
	public String nettoyerType(String type) 
	{
		type = type.trim();
		if (type.endsWith("[]")) return type.substring(0, type.length() - 2);
		if (type.startsWith("List<") && type.endsWith(">")) return type.substring(5, type.length() - 1);
		if (type.startsWith("Set<") && type.endsWith(">")) return type.substring(4, type.length() - 1);
		return type;
	}

    /**
	 * Retourne true si le type peut contenir plusieurs instances (tableau ou collection).
	 */
	public boolean estMultiInstance(String type) 
	{
		type = type.trim();
		return type.endsWith("[]") || type.startsWith("List<") || type.startsWith("Set<");
	}
}
