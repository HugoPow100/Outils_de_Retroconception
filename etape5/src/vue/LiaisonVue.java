package vue;

import java.awt.*;

public class LiaisonVue {

    private BlocClasse blocOrigine;
    private BlocClasse blocDestination;

    private Point ancrageOrig;
    private Point ancrageDest;

    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination) {
        this.blocOrigine = blocOrigine;
        this.blocDestination = blocDestination;
    }

    public void dessiner(Graphics2D g) {
        // Récupérer les points de connexion (centres des blocs)
        Point ancrageOrig = new Point(blocOrigine.getX() + blocOrigine.getLargeur(),blocOrigine.getY() + 15); // En haut du bloc

        Point ancrageDest= new Point(blocDestination.getX(),blocDestination.getY() + 15);

        // Trait plein simple pour association
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawLine(ancrageOrig.x, ancrageOrig.y, ancrageDest.x, ancrageDest.y);
        dessinerFlecheSimple(g, ancrageOrig.x, ancrageOrig.y, ancrageDest.x, ancrageDest.y);
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
}
