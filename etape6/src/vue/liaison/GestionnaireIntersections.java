package vue.liaison;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestion des intersections entre liaisons
 */
public class GestionnaireIntersections {
    
    /**
     * Détecte si deux segments orthogonaux se croisent
     */
    public Point getSegmentIntersection(Point a1, Point a2, Point b1, Point b2) {
        boolean a_horizontal = (a1.y == a2.y);
        boolean a_vertical = (a1.x == a2.x);
        boolean b_horizontal = (b1.y == b2.y);
        boolean b_vertical = (b1.x == b2.x);
        
        if (!(a_horizontal && b_vertical) && !(a_vertical && b_horizontal)) {
            return null;
        }
        
        Point h1, h2, v1, v2;
        if (a_horizontal) {
            h1 = a1; h2 = a2;
            v1 = b1; v2 = b2;
        } else {
            h1 = b1; h2 = b2;
            v1 = a1; v2 = a2;
        }
        
        int ix = v1.x;
        int iy = h1.y;
        
        int minHx = Math.min(h1.x, h2.x);
        int maxHx = Math.max(h1.x, h2.x);
        int minVy = Math.min(v1.y, v2.y);
        int maxVy = Math.max(v1.y, v2.y);
        
        if (ix >= minHx + 1 && ix <= maxHx - 1 && iy >= minVy + 1 && iy <= maxVy - 1) {
            return new Point(ix, iy);
        }
        
        return null;
    }
    
    /**
     * Vérifie si deux chemins partagent des segments communs
     */
    public boolean pathsShareSegments(List<Point> path1, List<Point> path2) {
        if (path1.size() < 2 || path2.size() < 2) return false;
        
        for (int i = 0; i < path1.size() - 1; i++) {
            Point p1a = path1.get(i);
            Point p1b = path1.get(i + 1);
            
            for (int j = 0; j < path2.size() - 1; j++) {
                Point p2a = path2.get(j);
                Point p2b = path2.get(j + 1);
                
                if (segmentsOverlap(p1a, p1b, p2a, p2b)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Vérifie si deux segments sont colinéaires et se chevauchent
     */
    private boolean segmentsOverlap(Point a1, Point a2, Point b1, Point b2) {
        if (a1.y == a2.y && b1.y == b2.y && a1.y == b1.y) {
            int minA = Math.min(a1.x, a2.x);
            int maxA = Math.max(a1.x, a2.x);
            int minB = Math.min(b1.x, b2.x);
            int maxB = Math.max(b1.x, b2.x);
            return maxA >= minB && maxB >= minA;
        }
        
        if (a1.x == a2.x && b1.x == b2.x && a1.x == b1.x) {
            int minA = Math.min(a1.y, a2.y);
            int maxA = Math.max(a1.y, a2.y);
            int minB = Math.min(b1.y, b2.y);
            int maxB = Math.max(b1.y, b2.y);
            return maxA >= minB && maxB >= minA;
        }
        
        return false;
    }
}
