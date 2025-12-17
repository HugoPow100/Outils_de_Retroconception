package metier.lecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import metier.objet.*;

/**
 * Classe responsable de la génération des associations entre classes.
 */
public class GenerateurAssociation
{
	private HashMap  <String, Classe> hashMapClasses ;
	private ArrayList<Association   > lstAssociations;

	public GenerateurAssociation(HashMap<String, Classe> hashMapClasses)
	{
		this.hashMapClasses  = hashMapClasses              ;
		this.lstAssociations = new ArrayList<Association>();
	}

	/**
	 * Génère toutes les associations entre les classes.
	 * @return Liste des associations générées
	 */
	public ArrayList<Association> generer()
	{
		ArrayList<Attribut> attributsARetirer = new ArrayList<Attribut>();

		for (String nomFichier : hashMapClasses.keySet())
		{
			Classe classeOrig = hashMapClasses.get(nomFichier);

			// Compteur : clé = type nettoyé, valeur = nb occurrences
			Map<String, Integer> compteur = new HashMap<>();
			
			// Liste pour savoir si ce type était multi-instance
			ArrayList<String> listeMultiInstance = new ArrayList<>();

			for (Attribut attr : classeOrig.getLstAttribut())
			{
				String typeOriginal = attr.getType().trim();
				String typeNettoye  = UtilitaireType.nettoyerType(typeOriginal).trim();

				if (UtilitaireType.estMultiInstance(typeOriginal))
				{
					listeMultiInstance.add(typeNettoye);
					attributsARetirer .add(attr       );
				}
				else // Simple instance
				{
					compteur.put(typeNettoye, compteur.getOrDefault(typeNettoye, 0) + 1);
					if (hashMapClasses.containsKey(typeNettoye + ".java"))
					{
						attributsARetirer.add(attr);
					}
				}
			}

			// -------- MULTI-INSTANCE --------
			traiterMultiInstance(classeOrig, listeMultiInstance);

			// -------- SIMPLE INSTANCE --------
			traiterSimpleInstance(classeOrig, compteur);

			// Nettoyage des associations
			nettoyerAssociations();
			
			// Suppression des attributs de relation
			classeOrig.getLstAttribut().removeAll(attributsARetirer);
		}

		return this.lstAssociations;
	}

	/**
	 * Traite les associations multi-instance (tableaux, List, Set, Map).
	 */
	private void traiterMultiInstance(Classe classeOrig, ArrayList<String> listeMultiInstance)
	{
		for (String typeDest : listeMultiInstance)
		{
			// Vérifier que la classe existe
			if (!hashMapClasses.containsKey(typeDest + ".java"))
				continue;

			Classe classeDest = hashMapClasses.get(typeDest + ".java");

			Multiplicite multOrig = new Multiplicite(1, 1);
			Multiplicite multDest = new Multiplicite(0, "*");

			// Si la classe apparaît en paramètre d'une méthode => multiplicité 1..*
			for (Methode methode : classeOrig.getLstMethode())
			{
				for (Parametre param : methode.getLstParametre())
				{
					if (UtilitaireType.nettoyerType(param.getTypePara()).equals(typeDest))
						multDest = new Multiplicite(1, "*");
				}
			}

			// Vérifier si bidirectionnel
			boolean bidirectionnel = estBidirectionnel(classeOrig, classeDest);

			lstAssociations.add(new Association(classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
		}
	}

	/**
	 * Traite les associations simple instance.
	 */
	private void traiterSimpleInstance(Classe classeOrig, Map<String, Integer> compteur)
	{
		for (Map.Entry<String, Integer> entry : compteur.entrySet())
		{
			String typeDest = entry.getKey  ();
			int    max      = entry.getValue();

			// Vérifier que la classe existe
			if (!hashMapClasses.containsKey(typeDest + ".java"))
				continue;

			Classe classeDest = hashMapClasses.get(typeDest + ".java");

			Multiplicite multOrig = new Multiplicite(1, 1  );
			Multiplicite multDest = new Multiplicite(1, max);

			// Vérifier si bidirectionnel
			boolean bidirectionnel = estBidirectionnel(classeOrig, classeDest);

			lstAssociations.add(new Association(classeDest, classeOrig, multDest, multOrig, !bidirectionnel));
		}
	}

	/**
	 * Vérifie si une association est bidirectionnelle.
	 */
	private boolean estBidirectionnel(Classe classeOrig, Classe classeDest)
	{
		// Vérifier dans les attributs
		for (Attribut attrDest : classeDest.getLstAttribut())
		{
			String typeAttrDest = UtilitaireType.nettoyerType(attrDest.getType().trim());
			if (typeAttrDest.equals(classeOrig.getNom()))
			{
				return true;
			}
		}

		// Vérifier dans les paramètres des méthodes
		for (Methode methodeDest : classeDest.getLstMethode())
		{
			for (Parametre paramDest : methodeDest.getLstParametre())
			{
				if (UtilitaireType.nettoyerType(paramDest.getTypePara()).equals(classeOrig.getNom()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Nettoie la liste des associations pour supprimer les doublons 
	 * et transformer les doublons en associations bidirectionnelles.
	 */
	private void nettoyerAssociations()
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

		// Remplacer la liste par la liste unique
		lstAssociations = new ArrayList<Association>(uniques.values());
	}

	public ArrayList<Association> getLstAssociations()
	{
		return this.lstAssociations;
	}
}