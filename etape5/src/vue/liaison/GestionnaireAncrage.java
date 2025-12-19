package vue.liaison;

import vue.BlocClasse;
import java.awt.Point;

/**
 * Gestion des points d'ancrage sur les blocs
 */
public class GestionnaireAncrage {
    
    private static final int ANCHOR_RADIUS = 20;
    
    /**
     * Renvoie un point sur un côté d'un bloc
     * side 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    public static Point getPointOnSide(BlocClasse bloc, int side, double posRel) {
        final int x = bloc.getX();
        final int y = bloc.getY();
        final int w = bloc.getLargeur();
        final int h = bloc.getHauteurCalculee();
        
        posRel = Math.max(0.0, Math.min(1.0, posRel));
        
        switch(side) {
            case 0: return new Point(x + (int)(w * posRel), y);           // HAUT
            case 1: return new Point(x + w, y + (int)(h * posRel));       // DROITE
            case 2: return new Point(x + (int)(w * posRel), y + h);       // BAS
            case 3: return new Point(x, y + (int)(h * posRel));           // GAUCHE
        }
        return new Point(x, y);
    }
    
    /**
     * Renvoie le côté le plus proche d'un point
     * 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    public static int getClosestSide(Point mouse, BlocClasse bloc) {
        int x = bloc.getX();
        int y = bloc.getY();
        int w = bloc.getLargeur();
        int h = bloc.getHauteurCalculee();
        
        int distDroite = Math.abs(mouse.x - (x + w));
        int distGauche = Math.abs(mouse.x - x);
        int distBas = Math.abs(mouse.y - (y + h));
        int distHaut = Math.abs(mouse.y - y);
        
        int minDist = Math.min(Math.min(distDroite, distGauche), Math.min(distBas, distHaut));
        
        if (minDist == distHaut) return 0;   // HAUT
        if (minDist == distDroite) return 1; // DROITE
        if (minDist == distBas) return 2;    // BAS
        return 3;                            // GAUCHE
    }
    
    /**
     * Renvoie la position relative d'un point sur un côté
     */
    public static double getRelativePosFromMouse(Point mouse, BlocClasse bloc, int side) {
        int x = bloc.getX();
        int y = bloc.getY();
        int w = bloc.getLargeur();
        int h = bloc.getHauteurCalculee();
        
        double posRel = 0.5;
        
        switch(side) {
            case 0:
            case 2:
                posRel = (double)(mouse.x - x) / w;
                break;
            case 1:
            case 3:
                posRel = (double)(mouse.y - y) / h;
                break;
        }
        
        return Math.max(0.0, Math.min(1.0, posRel));
    }
    
    /**
     * Vérifie si une position de souris est sur un ancrage
     */
    public static boolean isOnAnchor(Point ancrage, Point mouse, double zoom, int panX, int panY, int w, int h) {
        if (ancrage == null) return false;
        if (zoom == 1.0 && panX == 0 && panY == 0) return mouse.distance(ancrage) <= ANCHOR_RADIUS;
        
        final double hw = w * 0.5, hh = h * 0.5;
        double dx = mouse.x - (ancrage.x * zoom + hw - hw + panX);
        double dy = mouse.y - (ancrage.y * zoom + hh - hh + panY);
        return (dx * dx + dy * dy) <= ANCHOR_RADIUS * ANCHOR_RADIUS;
    }
    
    /**
     * Calcule la position de la multiplicité
     * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    public static Point calculateMultiplicityPosition(Point a, int s, int tw, int th) {
        int[][] o = {
            {5, -25},           // HAUT: texte au-dessus
            {25, -5},           // DROITE: texte à droite
            {5, 15},            // BAS: texte en dessous
            {-tw - 25, -5}      // GAUCHE: texte à gauche
        };
        return s<0||s>3 ? a : new Point(a.x+o[s][0], a.y+o[s][1]);
    }
    
    /**
     * Calcule la priorité de centrage d'une position
     */
    public static int calculateCenterPriority(double pos) {
        final double TOLERANCE = 0.01;
        
        if (Math.abs(pos - 0.5) < TOLERANCE) return 0;
        if (Math.abs(pos - 0.25) < TOLERANCE || Math.abs(pos - 0.75) < TOLERANCE) return 1;
        if (Math.abs(pos - 0.125) < TOLERANCE || Math.abs(pos - 0.375) < TOLERANCE ||
            Math.abs(pos - 0.625) < TOLERANCE || Math.abs(pos - 0.875) < TOLERANCE) return 2;
        
        return 1000;
    }
}
