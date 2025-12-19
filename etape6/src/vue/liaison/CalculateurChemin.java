package vue.liaison;

import vue.BlocClasse;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcul des chemins orthogonaux pour les liaisons
 */
public class CalculateurChemin
{	
	private final DetecteurObstacles detecteurObstacles;
	
	public CalculateurChemin(DetecteurObstacles detecteur)
	{
		this.detecteurObstacles = detecteur;
	}
	
	/**
	 * Crée une liste de points qui représente un chemin orthogonal
	 * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
	 */
	public List<Point> creerCheminOrthogonal(Point debut, Point fin, int coteDebut, int coteFin)
	{
		List<Point> chemin = new ArrayList<>();
		chemin.add(debut);
		
		int margeInterne  = 30;
		Point pointSortie = calculerPointDecale(debut, coteDebut, margeInterne);
		Point pointEntree = calculerPointDecale(fin,   coteFin,   margeInterne);
		
		chemin.add(pointSortie);
		
		// HAUT(0) et BAS(2) = sortie verticale, DROITE(1) et GAUCHE(3) = sortie horizontale
		boolean sortieHorizontale = (coteDebut == 1 || coteDebut == 3);
		boolean entreeHorizontale = (coteFin   == 1 || coteFin == 3);
		
	// Vérifier si c'est une paire de côtés opposés
		boolean opposeVertical    = (coteDebut == 2 && coteFin == 0) 
		                         || (coteDebut == 0 && coteFin == 2);

		boolean opposeHorizontal  = (coteDebut == 1 && coteFin == 3) 
		                         || (coteDebut == 3 && coteFin == 1);
		
		
		// Détecter si les côtés opposés sont parfaitement alignés pour une ligne droite
		if (opposeVertical && debut.x == fin.x)
		{
			// Alignés verticalement: ligne droite directe
			chemin.clear();
			chemin.add(debut);
			chemin.add(fin  );
			return chemin;
		}
		else if (opposeHorizontal && debut.y == fin.y)
		{
			// Alignés horizontalement: ligne droite directe
			chemin.clear();
			chemin.add(debut);
			chemin.add(fin  );
			return chemin;
		}
		else if (opposeVertical)
		{
			// BAS↔HAUT non alignés: un seul point intermédiaire (2 segments)
			Point intermediaire = eviterCoin(pointSortie.x, pointEntree.y, pointSortie.y, true);
			chemin.add(intermediaire);
		}
		else if (opposeHorizontal)
		{
			// DROITE↔GAUCHE non alignés: un seul point intermédiaire (2 segments)
			Point intermediaire = eviterCoin(pointSortie.y, pointEntree.x, pointSortie.x, false);
			chemin.add(intermediaire);
		}
		else if (sortieHorizontale && entreeHorizontale)
		{
			// Les deux sorties sont horizontales (même côté)
			if (pointSortie.x != pointEntree.x)
			{
				int coinY;
				if (coteDebut == 3 && coteFin == 3)
				{
					coinY = Math.min(pointSortie.y, pointEntree.y);
				}
				else if (coteDebut == 1 && coteFin == 1)
				{
					coinY = Math.max(pointSortie.y, pointEntree.y);
				}
				else
				{
					coinY = (pointSortie.y + pointEntree.y) / 2;
				}
				chemin.add(new Point(pointSortie.x, coinY));
				chemin.add(new Point(pointEntree.x, coinY));
			}
		}
		else if (!sortieHorizontale && !entreeHorizontale)
		{
			// Les deux sorties sont verticales, il faut un coude horizontal
			if (pointSortie.y != pointEntree.y)
			{
				int coinX;
				if (coteDebut == 0 && coteFin == 0)
				{
					coinX = Math.min(pointSortie.x, pointEntree.x);
				}
				else if (coteDebut == 2 && coteFin == 2)
				{
					coinX = Math.max(pointSortie.x, pointEntree.x);
				}
				else
				{
					coinX = (pointSortie.x + pointEntree.x) / 2;
				}
				chemin.add(new Point(coinX, pointSortie.y));
				chemin.add(new Point(coinX, pointEntree.y));
			}
		}
		else if (sortieHorizontale && !entreeHorizontale)
		{
			// Sortie horizontale, entrée verticale
			chemin.add(new Point(pointSortie.x, pointEntree.y));
		}
		else
		{
			// Sortie verticale, entrée horizontale
			chemin.add(new Point(pointEntree.x, pointSortie.y));
		}
		
		chemin.add(pointEntree);
		chemin.add(fin);
		
		return nettoyerPointsRedondants(chemin);
	}
	
	/**
	 * Calcule le point décalé selon le côté
	 * cote 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
	 */
	private Point calculerPointDecale(Point pt, int cote, int margeInterne)
	{
		switch(cote)
		{
			case 0: return new Point(pt.x, pt.y - margeInterne); // HAUT
			case 1: return new Point(pt.x + margeInterne, pt.y); // DROITE
			case 2: return new Point(pt.x, pt.y + margeInterne); // BAS
			case 3: return new Point(pt.x - margeInterne, pt.y); // GAUCHE
			default: return pt;
		}
	}
	
	/**
	 * Nettoie les points redondants (3 points alignés → 2 points)
	 */
	private List<Point> nettoyerPointsRedondants(List<Point> chemin)
	{
		if (chemin.size() < 3) return chemin;
		
		List<Point> nettoye = new ArrayList<Point>();
		nettoye.add(chemin.get(0));
		
		for (int i = 1; i < chemin.size() - 1; i++)
		{
			Point precedent = nettoye.get(nettoye.size() - 1);

			Point actuel  = chemin.get(i    );
			Point suivant = chemin.get(i + 1);
			
			boolean tousHorizontal = (precedent.y == actuel.y && actuel.y == suivant.y);
			boolean tousVertical   = (precedent.x == actuel.x && actuel.x == suivant.x);
			
			if (!tousHorizontal && !tousVertical)
				nettoye.add(actuel);
		}
		
		nettoye.add(chemin.get(chemin.size() - 1));
		
		// Vérification de sécurité : éliminer les diagonales
		for (int i = 0; i < nettoye.size() - 1; i++)
		{
			Point p1 = nettoye.get(i);
			Point p2 = nettoye.get(i + 1);

			if (p1.x != p2.x && p1.y != p2.y)
			{
				nettoye.add(i + 1, new Point(p2.x, p1.y));
				i = -1;
			}
		}
		
		return nettoye;
	}
	
	/**
	 * Calcule la longueur totale d'un chemin
	 */
	public double calculerLongueurChemin(List<Point> chemin)
	{
		if (chemin.size() < 2) return 0;
		
		double longueurTotale = 0;
		for (int i = 0; i < chemin.size() - 1; i++)
		{
			Point p1 = chemin.get(i    );
			Point p2 = chemin.get(i + 1);
			longueurTotale += Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
		}

		return longueurTotale;
	}
	
	/**
	 * Vérifie si un chemin a des collisions avec les blocs
	 */
	public boolean cheminADesCollisions(List<Point> chemin)
	{
		for (int i = 0; i < chemin.size() - 1; i++)
		{
			Point p1 = chemin.get(i    );
			Point p2 = chemin.get(i + 1);
			
			if (p1.x == p2.x)
			{
				if (detecteurObstacles.aUnObstacle(false, Math.min(p1.y, p2.y), Math.max(p1.y, p2.y), p1.x))
					return true;
			}
			else if (p1.y == p2.y)
			{
				if (detecteurObstacles.aUnObstacle(true, Math.min(p1.x, p2.x), Math.max(p1.x, p2.x), p1.y))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Applique un décalage perpendiculaire à tous les segments du chemin
	 */
	public List<Point> appliquerDecalageAuChemin(List<Point> chemin, int decalage)
	{
		List<Point> cheminDecale = new ArrayList<Point>();
		
		for (int i = 0; i < chemin.size(); i++)
		{
			Point p = chemin.get(i);
			
			if (i == 0)
			{
				Point suivant = chemin.get(i + 1);

				if (suivant.x == p.x) 
					cheminDecale.add(new Point(p.x + decalage, p.y));
				else 
					cheminDecale.add(new Point(p.x, p.y + decalage));
			}
			else if (i == chemin.size() - 1)
			{
				Point precedent = chemin.get(i - 1);
				if (precedent.x == p.x)
					cheminDecale.add(new Point(p.x + decalage, p.y));
				else 
					cheminDecale.add(new Point(p.x, p.y + decalage));
			}
			else
			{
				Point precedent = chemin.get(i - 1);
				Point suivant   = chemin.get(i + 1);
				
				boolean precedentVertical = (precedent.x == p.x);
				boolean suivantVertical   = (suivant.x == p.x);
				
				if (precedentVertical && suivantVertical)
				{
					cheminDecale.add(new Point(p.x + decalage, p.y));
				}
				else if (!precedentVertical && !suivantVertical)
				{
					cheminDecale.add(new Point(p.x, p.y + decalage));
				}
				else
				{
					cheminDecale.add(new Point(p.x + decalage, p.y + decalage));
				}
			}
		}
		
		return cheminDecale;
	}

	/**
	 * Évite les coins parfaits en ajustant la position si trop proche d'une transition
	 */
	private Point eviterCoin(int pos, int coordFixe, int coordComparaison, boolean estVertical)
	{
		int distance = Math.abs(coordFixe - coordComparaison);
		if (distance < 25)
		{
			int ajustement = (coordFixe < coordComparaison) ? -20 : 20;
			if (estVertical)
			{
				return new Point(pos + ajustement, coordFixe);
			}
			else
			{
				return new Point(coordFixe, pos + ajustement);
			}
		}
		if (estVertical)
		{
			return new Point(pos, coordFixe);
		}
		else
		{
			return new Point(coordFixe, pos);
		}
	}

}