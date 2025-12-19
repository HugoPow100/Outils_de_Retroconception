package vue.liaison;

import java.awt.*;
import java.util.List;

/**
 * Rendu graphique des liaisons
 */
public class RenduLiaison {
    
    private final GestionnaireIntersections gestionnaireIntersections;
    
    public RenduLiaison() {
        this.gestionnaireIntersections = new GestionnaireIntersections();
    }
    
    /**
     * Dessine une ligne avec des ponts aux intersections
     */
    public void drawLineWithBridges(Graphics2D g, Point p1, Point p2, List<Point> intersections, Stroke normalStroke) {
        final int BRIDGE_SIZE = 8;
        
        boolean horizontal = (p1.y == p2.y);
        
        if (horizontal) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            return;
        }
        
        if (intersections.isEmpty()) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            return;
        }
        
        intersections.sort((a, b) -> {
            if (p1.y < p2.y) {
                return Integer.compare(a.y, b.y);
            } else {
                return Integer.compare(b.y, a.y);
            }
        });
        
        Point current = p1;
        
        for (Point inter : intersections) {
            g.setStroke(normalStroke);
            if (current.y < inter.y) {
                g.drawLine(current.x, current.y, inter.x, inter.y - BRIDGE_SIZE / 2);
            } else {
                g.drawLine(current.x, current.y, inter.x, inter.y + BRIDGE_SIZE / 2);
            }
            
            g.setStroke(new BasicStroke(1));
            g.drawArc(inter.x - BRIDGE_SIZE / 2, inter.y - BRIDGE_SIZE / 2, 
                     BRIDGE_SIZE, BRIDGE_SIZE, 90, 180);
            
            if (current.y < inter.y) {
                current = new Point(inter.x, inter.y + BRIDGE_SIZE / 2);
            } else {
                current = new Point(inter.x, inter.y - BRIDGE_SIZE / 2);
            }
        }
        
        g.setStroke(normalStroke);
        g.drawLine(current.x, current.y, p2.x, p2.y);
    }
    
    /**
     * Dessine une flèche vide (héritage/interface)
     * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    public void dessinerFlecheVide(Graphics2D g, Point a, int s) {
        final int f = 10, h = f >> 1;
        // pts[side] = {x1_offset, y1_offset, x2_offset, y2_offset} pour les 2 points du triangle
        int[][] pts = {
            {-h, -f, h, -f},  // HAUT: flèche pointant vers le haut
            {f, -h, f, h},    // DROITE: flèche pointant vers la droite
            {-h, f, h, f},    // BAS: flèche pointant vers le bas
            {-f, -h, -f, h}   // GAUCHE: flèche pointant vers la gauche
        };
        if (s < 0 || s > 3) return;
        int[] x = {a.x+pts[s][0], a.x, a.x+pts[s][2]};
        int[] y = {a.y+pts[s][1], a.y, a.y+pts[s][3]};
        g.drawPolygon(x, y, 3);
    }
    
    /**
     * Dessine une flèche d'association (2 lignes)
     * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    public void dessinerFlecheAssociation(Graphics2D g, Point a, int s) {
        final int f = 10, h = f >> 1;
        // d[side] = {dx, dy1, dy2} pour les 2 lignes de la flèche
        int[][] d = {
            {-h, -f, h},   // HAUT: flèche vers le haut
            {f, -h, h},    // DROITE: flèche vers la droite
            {-h, f, h},    // BAS: flèche vers le bas
            {-f, -h, h}    // GAUCHE: flèche vers la gauche
        };
        if (s < 0 || s > 3) return;
        if (s == 0 || s == 2) {
            // Vertical
            g.drawLine(a.x, a.y, a.x + d[s][0], a.y + d[s][1]);
            g.drawLine(a.x, a.y, a.x + d[s][2], a.y + d[s][1]);
        } else {
            // Horizontal
            g.drawLine(a.x, a.y, a.x + d[s][0], a.y + d[s][1]);
            g.drawLine(a.x, a.y, a.x + d[s][0], a.y + d[s][2]);
        }
    }
}
