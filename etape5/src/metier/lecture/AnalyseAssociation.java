package metier.lecture;

import metier.Attribut;
import metier.Classe;
import metier.Multiplicite;
import metier.Methode;
import metier.Parametre;
import metier.Association;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyseAssociation 
{
    private UtilLecture util;

    public AnalyseAssociation(UtilLecture utilLecture) 
    {
        this.util = utilLecture;
    }


    public ArrayList<Association> genererAssociation(HashMap<String, Classe> hashMapClasses)
	{
		ArrayList<Association>  lstAssociations   = new ArrayList<>();
		ArrayList<Attribut>     attributsARetirer = new ArrayList<>();

		for (String nomFichier : hashMapClasses.keySet()) 
		{
			Classe classeOrig = hashMapClasses.get(nomFichier);

			// Compteur : clé = type nettoyé, valeur = nb occurrences				
			Map<String, Integer> compteur = new HashMap<>();
			
			// Array pour savoir si ce type était multi-instance
			ArrayList<String> listeMultiInstance = new ArrayList<>();

			for (Attribut attr : classeOrig.getLstAttribut()) 
			{
				String typeOriginal = attr.getType().trim();
				String typeNettoye  = util.nettoyerType(typeOriginal).trim();
				
				/*if (!hashMapClasses.containsKey(typeNettoye))
							continue;*/

				if (util.estMultiInstance(typeOriginal)) 
				{
					// Ajoute tel quel
					listeMultiInstance.add(typeNettoye);
					attributsARetirer.add(attr);
				}
				else // Simple instance
				{
					compteur.put(typeNettoye, compteur.getOrDefault(typeNettoye, 0) + 1);
					if (hashMapClasses.containsKey(typeNettoye + ".java")) {
						attributsARetirer.add(attr);
					}
				}
			}

			// -------- MULTI-INSTANCE --------
			for (String typeDest : listeMultiInstance) 
			{
				// Vérifier que la classe existe dans la HashMap
				if (!hashMapClasses.containsKey(typeDest + ".java")) {
					continue; // Ignorer si la classe n'existe pas
				}

				Classe classeDest     = hashMapClasses.get(typeDest + ".java");

				Multiplicite multOrig = new Multiplicite(1,1);
				Multiplicite multDest = new Multiplicite(0, "*");  // valeur par défaut


				// Si la classe apparaît en paramètre d'une méthode => multiplicité 1..*
				for (Methode methode : classeOrig.getLstMethode()) 
				{
					for (Parametre param : methode.getLstParametre()) 
					{
						// comparer le type paramètre avec le nom de classe destination
						if (util.nettoyerType(param.getTypePara()).equals(typeDest)) 
						{
							multDest = new Multiplicite(1, "*");
						}
					}
				}


				// Vérifier si la classe destination référence aussi la classe origine
				boolean bidirectionnel = false;
				for (Attribut attrDest : classeDest.getLstAttribut()) 
				{
					String typeAttrDest = util.nettoyerType(attrDest.getType().trim());
					if (typeAttrDest.equals(classeOrig.getNom())) 
					{
						bidirectionnel = true;
						break;
					}
				}

				// Vérifier aussi dans les paramètres des méthodes de la classe destination
				if (!bidirectionnel) 
				{
					for (Methode methodeDest : classeDest.getLstMethode()) 
					{
						for (Parametre paramDest : methodeDest.getLstParametre()) 
						{
							if (util.nettoyerType(paramDest.getTypePara()).equals(classeOrig.getNom())) 
							{
								bidirectionnel = true;
								break;
							}
						}
						if (bidirectionnel) break;
					}
				}

			lstAssociations.add(new Association(
				classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
			}

			// -------- SIMPLE INSTANCE --------
			for (Map.Entry<String,Integer> entry : compteur.entrySet()) 
			{

				String   typeDest   = entry.getKey();
				int      max        = entry.getValue();

                // Vérifier que la classe existe dans la HashMap
                if (!hashMapClasses.containsKey(typeDest + ".java")) 
                {
                    continue; // Ignorer si la classe n'existe pas
                }

                Classe   classeDest = hashMapClasses.get(typeDest + ".java");				
                Multiplicite multOrig  = new Multiplicite(1,1);
					Multiplicite multDest  = new Multiplicite(1, max);

					// Vérifier si la classe destination référence aussi la classe origine
					boolean bidirectionnel = false;
					for (Attribut attrDest : classeDest.getLstAttribut()) 
                    {
						String typeAttrDest = util.nettoyerType(attrDest.getType().trim());
						if (typeAttrDest.equals(classeOrig.getNom())) 
                        {
							bidirectionnel = true;
							break;
						}
					}
					// Vérifier aussi dans les paramètres des méthodes de la classe destination
					if (!bidirectionnel) 
                    {
						for (Methode methodeDest : classeDest.getLstMethode()) 
                        {
							for (Parametre paramDest : methodeDest.getLstParametre()) 
                            {
								if (util.nettoyerType(paramDest.getTypePara()).equals(classeOrig.getNom())) 
                                {
									bidirectionnel = true;
									break;
								}
							}
							if (bidirectionnel) break;
						}
					}

					lstAssociations.add(new Association(
						classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
			}
				util.nettoyerAssociations(lstAssociations);
			// SUPPRESSION DES ATTRIBUTS DE RELATION
			classeOrig.getLstAttribut().removeAll(attributsARetirer);

		}
        
		return lstAssociations;
	}
}
