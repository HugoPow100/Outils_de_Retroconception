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
     */
    public void dessinerFlecheVide(Graphics2D g, Point a, int s) {
        final int f = 10, h = f >> 1;
        int[][] pts = {{f,-h,f,h},{-h,f,h,f},{-f,-h,-f,h},{-h,-f,h,-f}};
        if (s < 0 || s > 3) return;
        int[] x = {a.x+pts[s][0], a.x, a.x+pts[s][2]};
        int[] y = {a.y+pts[s][1], a.y, a.y+pts[s][3]};
        g.drawPolygon(x, y, 3);
    }
    
    /**
     * Dessine une flèche d'association (2 lignes)
     */
    public void dessinerFlecheAssociation(Graphics2D g, Point a, int s) {
        final int f = 10, h = f >> 1;
        int[][] d = {{f,-h,h},{-h,f,h},{-f,-h,h},{-h,-f,h}};
        if (s < 0 || s > 3) return;
        g.drawLine(a.x, a.y, a.x + d[s][0], a.y + d[s][1]);
        g.drawLine(a.x, a.y, a.x + d[s][0], a.y + d[s][2]);
    }
}
