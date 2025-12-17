package metier.lecture;
import metier.objet.*;

/**
 * Classe utilitaire pour manipuler les types Java.
 */
public class UtilitaireType
{
	/**
	 * Nettoie le type pour retirer List<>, Set<> ou [].
	 * @param type Le type à nettoyer
	 * @return     Le type nettoyé
	 */
	public static String nettoyerType(String type)
	{
		type = type.trim();
		if (type.endsWith("[]")) return type.substring(0, type.length() - 2);
		if (type.startsWith("List<") && type.endsWith(">")) return type.substring(5, type.length() - 1);
		if (type.startsWith("Set<")  && type.endsWith(">")) return type.substring(4, type.length() - 1);
		if (type.startsWith("Map<")  && type.endsWith(">")) 
		{
			// Pour les Map, extraire le type de la valeur
			String contenu = type.substring(4, type.length() - 1);
			int virgule = contenu.lastIndexOf(',');

			if (virgule != -1) 
			{
				return contenu.substring(virgule + 1).trim();
			}
		}
		return type;
	}

	/**
	 * Retourne true si le type peut contenir plusieurs instances (tableau ou collection).
	 * @param type Le type à vérifier
	 * @return true si multi-instance, false sinon
	 */
	public static boolean estMultiInstance(String type)
	{
		type = type.trim();
		return type.endsWith("[]")     || type.startsWith("List<")    || 
		       type.startsWith("Set<") || type.startsWith("Map<")      ;
	}
}
