package vue;

import java.awt.*;

public class LiaisonVue {

    private BlocClasse blocOrigine;
    private BlocClasse blocDestination;
    private String type; // "heritage", "interface", "association"

    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type) {
        this.blocOrigine = blocOrigine;
        this.blocDestination = blocDestination;
        this.type = type;
    }

    public void dessiner(Graphics2D g) {
        // Récupérer les points de connexion (centres des blocs)
        int x1 = blocOrigine.getX() + blocOrigine.getLargeur() / 2;
        int y1 = blocOrigine.getY() + 15; // En haut du bloc

        int x2 = blocDestination.getX() + blocDestination.getLargeur() / 2;
        int y2 = blocDestination.getY() + 15;

        // Définir le style selon le type de liaison
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));

        switch (type.toLowerCase()) {
            case "heritage":
                dessinerHeritage(g, x1, y1, x2, y2);
                break;
            case "interface":
                dessinerInterface(g, x1, y1, x2, y2);
                break;
            case "association":
                dessinerAssociation(g, x1, y1, x2, y2);
                break;
            default:
                dessinerAssociation(g, x1, y1, x2, y2);
        }
    }

    private void dessinerHeritage(Graphics2D g, int x1, int y1, int x2, int y2) {
        // Trait plein avec triangle blanc
        g.drawLine(x1, y1, x2, y2);
        dessinerFlecheTriangle(g, x1, y1, x2, y2, true);
    }

    private void dessinerInterface(Graphics2D g, int x1, int y1, int x2, int y2) {
        // Trait pointillé avec triangle blanc
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                10, new float[]{5}, 0));
        g.drawLine(x1, y1, x2, y2);
        dessinerFlecheTriangle(g, x1, y1, x2, y2, true);
    }

    private void dessinerAssociation(Graphics2D g, int x1, int y1, int x2, int y2) {
        // Trait plein simple
        g.setStroke(new BasicStroke(1));
        g.drawLine(x1, y1, x2, y2);
        dessinerFlecheSimple(g, x1, y1, x2, y2);
    }

    private void dessinerFlecheTriangle(Graphics2D g, int x1, int y1, int x2, int y2, boolean vide) {
        // Calculer l'angle de la flèche
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int flecheSize = 15;

        // Points du triangle
        int px1 = x2;
        int py1 = y2;
        int px2 = (int) (x2 - flecheSize * Math.cos(angle - Math.PI / 6));
        int py2 = (int) (y2 - flecheSize * Math.sin(angle - Math.PI / 6));
        int px3 = (int) (x2 - flecheSize * Math.cos(angle + Math.PI / 6));
        int py3 = (int) (y2 - flecheSize * Math.sin(angle + Math.PI / 6));

        int[] xPoints = {px1, px2, px3};
        int[] yPoints = {py1, py2, py3};

        if (vide) {
            g.setColor(Color.WHITE);
            g.fillPolygon(xPoints, yPoints, 3);
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints, yPoints, 3);
        } else {
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    private void dessinerFlecheSimple(Graphics2D g, int x1, int y1, int x2, int y2) {
        // Calculer l'angle de la flèche
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int flecheSize = 10;

        // Points de la flèche
        int px2 = (int) (x2 - flecheSize * Math.cos(angle - Math.PI / 6));
        int py2 = (int) (y2 - flecheSize * Math.sin(angle - Math.PI / 6));
        int px3 = (int) (x2 - flecheSize * Math.cos(angle + Math.PI / 6));
        int py3 = (int) (y2 - flecheSize * Math.sin(angle + Math.PI / 6));

        g.drawLine(x2, y2, px2, py2);
        g.drawLine(x2, y2, px3, py3);
    }

    // Getters et Setters
    public BlocClasse getBlocOrigine() {
        return blocOrigine;
    }

    public void setBlocOrigine(BlocClasse blocOrigine) {
        this.blocOrigine = blocOrigine;
    }

    public BlocClasse getBlocDestination() {
        return blocDestination;
    }

    public void setBlocDestination(BlocClasse blocDestination) {
        this.blocDestination = blocDestination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
