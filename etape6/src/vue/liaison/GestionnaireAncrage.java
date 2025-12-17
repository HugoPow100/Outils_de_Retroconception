package vue.liaison;

import vue.BlocClasse;
import java.awt.Point;

/**
 * Gestion des points d'ancrage sur les blocs
 */
public class GestionnaireAncrage {
    
    private static final int ANCHOR_RADIUS = 10;
    
    /**
     * Renvoie un point sur un côté d'un bloc
     */
    public static Point getPointOnSide(BlocClasse bloc, int side, double posRel) {
        final int x = bloc.getX();
        final int y = bloc.getY();
        final int w = bloc.getLargeur();
        final int h = bloc.getHauteurCalculee();
        
        posRel = Math.max(0.0, Math.min(1.0, posRel));
        
        switch(side) {
            case 0: return new Point(x + w, y + (int)(h * posRel));
            case 1: return new Point(x + (int)(w * posRel), y + h);
            case 2: return new Point(x, y + (int)(h * posRel));
            case 3: return new Point(x + (int)(w * posRel), y);
        }
        return new Point(x, y);
    }
    
    /**
     * Renvoie le côté le plus proche d'un point
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
        
        if (minDist == distDroite) return 0;
        if (minDist == distBas) return 1;
        if (minDist == distGauche) return 2;
        return 3;
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
                posRel = (double)(mouse.y - y) / h;
                break;
            case 1:
            case 3:
                posRel = (double)(mouse.x - x) / w;
                break;
        }
        
        return Math.max(0.1, Math.min(0.9, posRel));
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
     */
    public static Point calculateMultiplicityPosition(Point a, int s, int tw, int th) {
        int[][] o = {{25,-5},{5,15},{-tw-25,-5},{5,-25}};
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
