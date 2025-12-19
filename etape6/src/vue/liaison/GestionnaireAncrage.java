package vue.liaison;

import java.awt.Point;
import vue.BlocClasse;

/**
 * Gestion des points d'ancrage et positions sur les blocs de classes.
 * Permet de calculer les points pour les liaisons et les multiplicitées.
 */
public class GestionnaireAncrage
{
	/** Rayon autour du point d'ancrage pour le clic */
	private static final int ANCHOR_RADIUS = 10;

	/**
	 * Renvoie un point sur un côté d'un bloc.
	 * @param bloc Le bloc cible
	 * @param cote Côté : 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
	 * @param posRel Position relative sur le côté (0.0 à 1.0)
	 * @return Point exact sur le côté
	 */
	public static Point getPointSurCote(BlocClasse bloc, int cote, double posRel)
	{
		int x = bloc.getX();
		int y = bloc.getY();
		int w = bloc.getLargeur();
		int h = bloc.getHauteurCalculee();

		posRel = Math.max(0.0, Math.min(1.0, posRel));

		switch(cote)
		{
			case 0: return new Point(x + (int)(w * posRel), y);     // HAUT
			case 1: return new Point(x + w, y + (int)(h * posRel)); // DROITE
			case 2: return new Point(x + (int)(w * posRel), y + h); // BAS
			case 3: return new Point(x, y + (int)(h * posRel));     // GAUCHE
		}

		return new Point(x, y);
	}

	/**
	 * Renvoie le côté le plus proche d'un point donné.
	 * @param souris Position de la souris
	 * @param bloc Bloc cible
	 * @return côté le plus proche (0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE)
	 */
	public static int getCoteLePlusProche(Point souris, BlocClasse bloc)
	{
		int x = bloc.getX();
		int y = bloc.getY();
		int w = bloc.getLargeur();
		int h = bloc.getHauteurCalculee();

		int distHaut   = Math.abs(souris.y - y);
		int distDroite = Math.abs(souris.x - (x + w));
		int distBas    = Math.abs(souris.y - (y + h));
		int distGauche = Math.abs(souris.x - x);

		int minDist = Math.min(Math.min(distHaut, distDroite), Math.min(distBas, distGauche));

		if (minDist == distHaut)   return 0;
		if (minDist == distDroite) return 1;
		if (minDist == distBas)    return 2;
		return 3;
	}

	/**
	 * Calcule la position relative d'un point sur un côté du bloc.
	 * @param souris Position de la souris
	 * @param bloc Bloc cible
	 * @param cote Côté
	 * @return position relative normalisée [0.0,1.0]
	 */
	public static double getPosRelativeDepuisSouris(Point souris, BlocClasse bloc, int cote)
	{
		int x = bloc.getX();
		int y = bloc.getY();
		int w = bloc.getLargeur();
		int h = bloc.getHauteurCalculee();

		double posRel = 0.5;

		switch(cote)
		{
			case 0: // HAUT
			case 2: // BAS
				posRel = (double)(souris.x - x) / w;
				break;
			case 1: // DROITE
			case 3: // GAUCHE
				posRel = (double)(souris.y - y) / h;
				break;
		}

		return Math.max(0.0, Math.min(1.0, posRel));
	}

	/**
	 * Vérifie si la position de la souris est sur un ancrage.
	 */
	public static boolean estSurAncrage(Point ancrage, Point souris, double zoom, 
                                        int      panX, int     panY,       int w, int h)
	{
		if (ancrage == null) return false;

		if (zoom == 1.0 && panX == 0 && panY == 0)
		{
			return souris.distance(ancrage) <= ANCHOR_RADIUS;
		}

		double dx = souris.x - (ancrage.x * zoom + panX);
		double dy = souris.y - (ancrage.y * zoom + panY);

		return (dx * dx + dy * dy) <= ANCHOR_RADIUS * ANCHOR_RADIUS;
	}

	/**
	 * Calcule la position de la multiplicité par rapport à un point.
	 * @param a Point de référence
	 * @param cote Côté (0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE)
	 * @param largeurTexte Largeur du texte
	 * @param hauteurTexte Hauteur du texte
	 */
	public static Point calculerPositionMultiplicite(Point a, int cote, 
                                                     int  largeurTexte, int hauteurTexte)
	{
		int[][] decalages = {
			{5, -25},                  // HAUT
			{25, -5},                  // DROITE
			{5, 15},                   // BAS
			{-largeurTexte - 25, -5}   // GAUCHE
		};

		return cote < 0 || cote > 3 ? a : new Point(a.x + decalages[cote][0], 
                                                    a.y + decalages[cote][1]);
	}

	/**
	 * Détermine la priorité de centrage d'une position relative.
	 */
	public static int calculerPrioriteCentre(double pos)
	{
		final double TOLERANCE = 0.01;

		if (Math.abs(pos - 0.5  ) < TOLERANCE) return 0;
		if (Math.abs(pos - 0.25 ) < TOLERANCE || Math.abs(pos - 0.75 ) < TOLERANCE) return 1;
		if (Math.abs(pos - 0.125) < TOLERANCE || Math.abs(pos - 0.375) < TOLERANCE ||
			Math.abs(pos - 0.625) < TOLERANCE || Math.abs(pos - 0.875) < TOLERANCE) return 2;

		return 1000;
	}

	/**
	 * Calcule la position du rôle par rapport au bloc et à la multiplicité.
	 */
	public static Point calculerPositionRole(Point a, int cote, int largeurTexte, int hauteurTexte, int hauteurMult)
	{
		final int ECART    = 6;  // écart vertical entre multiplicité et rôle
		final int DECALAGE = 25; // distance horizontale du bloc

		int x = a.x;
		int y = a.y;

		switch(cote)
		{
			case 0: // HAUT
				x -= DECALAGE + largeurTexte;
				y -= DECALAGE;
				break;
			case 1: // DROITE
				x += DECALAGE;
				y += hauteurMult + ECART;
				break;
			case 2: // BAS
				x -= DECALAGE + largeurTexte;
				y += DECALAGE;
				break;
			case 3: // GAUCHE
				x -= DECALAGE + largeurTexte;
				y += hauteurMult + ECART;
				break;
		}

		return new Point(x, y);
	}
}
