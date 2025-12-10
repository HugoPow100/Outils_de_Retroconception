package vue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LiaisonVue 
{

    private String type; // association, heritage, interface
    private boolean unidirectionnel;
    private String multOrig;
    private String multDest;

    private BlocClasse blocOrigine;
    private BlocClasse blocDestination;

    private Point ancrageOrigine;
    private Point ancrageDestination;

    // Stockage des positions des points d'ancrage (0-100, pourcentage le long du bord)
    private double posRelOrigine;  // Position relative sur le bord de l'origine
    private double posRelDestination; // Position relative sur le bord de la destination
    
    // Côtés choisis pour les connexions (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT)
    private int sideOrigine;
    private int sideDestination;
    
    // Rayon pour cliquer sur les points d'ancrage
    private static final int ANCHOR_RADIUS = 5;

    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type) 
    {
        this.blocOrigine     = blocOrigine;
        this.blocDestination = blocDestination;
        this.type            = type;
        this.multOrig        = "";
        this.multDest        = "";
        this.unidirectionnel = true;
        this.sideOrigine = 0; // DROITE par défaut
        this.sideDestination = 2; // GAUCHE par défaut
        this.posRelOrigine = 0.5;
        this.posRelDestination = 0.5;
    }

    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type, boolean unidirectionnel, String multOrig, String multDest) 
    {
        this.blocOrigine       = blocOrigine;
        this.blocDestination   = blocDestination;
        this.type              = type;
        this.unidirectionnel   = unidirectionnel;
        this.multOrig          = multOrig;
        this.multDest          = multDest;
        this.sideOrigine       = 0;
        this.sideDestination   = 2;
        this.posRelOrigine     = 0.5;
        this.posRelDestination = 0.5;
    }

    // 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
    private Point getPointOnSide(BlocClasse bloc, int side, double posRel) 
    {
        int x = bloc.getX();
        int y = bloc.getY();
        int w = bloc.getLargeur();
        int h = bloc.getHauteurCalculee();

        // éviter les coins
        posRel = Math.max(0.1, Math.min(0.9, posRel));

        switch(side) 
        {
            case 0: // DROITE
                return new Point(x + w, y + (int)(h * posRel));
            case 1: // BAS
                return new Point(x + (int)(w * posRel), y + h);
            case 2: // GAUCHE
                return new Point(x, y + (int)(h * posRel));
            case 3: // HAUT
                return new Point(x + (int)(w * posRel), y);
        }
        return new Point(x, y);
    }

    // 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
    private int getClosestSide(Point mouse, BlocClasse bloc) 
    {
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
        if (minDist == distBas)    return 1;
        if (minDist == distGauche) return 2;
        return 3;
    }

    private double getRelativePosFromMouse(Point mouse, BlocClasse bloc, int side) 
    {
        int x = bloc.getX();
        int y = bloc.getY();
        int w = bloc.getLargeur();
        int h = bloc.getHauteurCalculee();
        
        double posRel = 0.5;
        
        switch(side) {
            case 0: // DROITE
                posRel = (double)(mouse.y - y) / h;
                break;
            case 1: // BAS
                posRel = (double)(mouse.x - x) / w;
                break;
            case 2: // GAUCHE
                posRel = (double)(mouse.y - y) / h;
                break;
            case 3: // HAUT
                posRel = (double)(mouse.x - x) / w;
                break;
        }
        
        return Math.max(0.1, Math.min(0.9, posRel));
    }


    private List<Point> createOrthogonalPath(Point start, Point end, int startSide, int endSide) {
        List<Point> path = new ArrayList<>();
        path.add(start);

        int padding = 20;
        int midX = start.x;
        int midY = start.y;
        int midX2 = end.x;
        int midY2 = end.y;

        // Déterminer la direction de sortie selon le côté du départ
        if (startSide == 0) { // DROITE
            midX = start.x + padding;
        } else if (startSide == 2) { // GAUCHE
            midX = start.x - padding;
        } else if (startSide == 1) { // BAS
            midY = start.y + padding;
        } else { // HAUT
            midY = start.y - padding;
        }

        // Déterminer la direction d'arrivée
        if (endSide == 0) { // DROITE
            midX2 = end.x + padding;
        } else if (endSide == 2) { // GAUCHE
            midX2 = end.x - padding;
        } else if (endSide == 1) { // BAS
            midY2 = end.y + padding;
        } else { // HAUT
            midY2 = end.y - padding;
        }

        // Premier segment : sortir du bloc initial
        if (startSide == 0 || startSide == 2) { // DROITE ou GAUCHE
            path.add(new Point(midX, start.y));
        } else { // BAS ou HAUT
            path.add(new Point(start.x, midY));
        }

        // Segment intermédiaire : aller vers la destination avec le padding
        if (startSide == 0 || startSide == 2) { // Sortie horizontale
            path.add(new Point(midX, midY2));
            path.add(new Point(midX2, midY2));
        } else { // Sortie verticale
            path.add(new Point(midX2, midY));
            path.add(new Point(midX2, midY2));
        }

        path.add(end);
        return path;
    }

    private Point calculateMultiplicityPosition(Point anchor, int side, int textWidth, int textHeight) 
    {
        int offsetX = 0;
        int offsetY = 0;
        
        switch(side) {
            case 0: // DROITE - texte à droite de l'ancrage
                offsetX = 25;
                offsetY = -5;
                break;
            case 1: // BAS - texte en bas de l'ancrage
                offsetX = 5;
                offsetY = 15;
                break;
            case 2: // GAUCHE - texte à gauche de l'ancrage
                offsetX = -textWidth - 25;
                offsetY = -5;
                break;
            case 3: // HAUT - texte au-dessus de l'ancrage
                offsetX = 5;
                offsetY = -25;
                break;
        }
        
        return new Point(anchor.x + offsetX, anchor.y + offsetY);
    }

    public void dessiner(Graphics2D g)
    {
        // Calculer les points d'ancrage basés sur les côtés et positions relatives
        ancrageOrigine = getPointOnSide(blocOrigine, sideOrigine, posRelOrigine);
        ancrageDestination = getPointOnSide(blocDestination, sideDestination, posRelDestination);

        // Créer le chemin orthogonal
        List<Point> path = createOrthogonalPath(ancrageOrigine, ancrageDestination, sideOrigine, sideDestination);

        // Trait plein simple pour association
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        if (this.type.equals("association") && unidirectionnel) {
            dessinerFlecheSelonSide(g, ancrageDestination, sideDestination);
        }
        
        // Les points d'ancrage sont invisibles mais draggables
        
        // Afficher les multiplicités pour les associations
        if (this.type.equals("association")) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            FontMetrics fm = g.getFontMetrics();

            // Multiplicité origine
            if (multOrig != null && !multOrig.isEmpty()) {
                int textWidth = fm.stringWidth(multOrig);
                int textHeight = fm.getAscent();
                
                // Calculer la position basée sur le côté
                Point initialPos = calculateMultiplicityPosition(ancrageOrigine, sideOrigine, textWidth, textHeight);
                
                g.drawString(multOrig, initialPos.x, initialPos.y);
            }

            // Multiplicité destination
            if (multDest != null && !multDest.isEmpty()) {
                int textWidth = fm.stringWidth(multDest);
                int textHeight = fm.getAscent();
                
                // Calculer la position basée sur le côté
                Point initialPos = calculateMultiplicityPosition(ancrageDestination, sideDestination, textWidth, textHeight);
                
                g.drawString(multDest, initialPos.x, initialPos.y);
            }
        }
    }

    /**
     * Dessine une flèche basée sur le côté d'arrivée
     * side: 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
     */
    private void dessinerFlecheSelonSide(Graphics2D g, Point anchor, int side) {
        int flecheSize = 10;
        int x = anchor.x;
        int y = anchor.y;
        
        int px1, py1, px2, py2;
        
        switch(side) {
            case 0: // DROITE - flèche pointant vers la droite
                px1 = x + flecheSize;
                py1 = y - flecheSize / 2;
                px2 = x + flecheSize;
                py2 = y + flecheSize / 2;
                break;
            case 1: // BAS - flèche pointant vers le bas
                px1 = x - flecheSize / 2;
                py1 = y + flecheSize;
                px2 = x + flecheSize / 2;
                py2 = y + flecheSize;
                break;
            case 2: // GAUCHE - flèche pointant vers la gauche
                px1 = x - flecheSize;
                py1 = y - flecheSize / 2;
                px2 = x - flecheSize;
                py2 = y + flecheSize / 2;
                break;
            case 3: // HAUT - flèche pointant vers le haut
                px1 = x - flecheSize / 2;
                py1 = y - flecheSize;
                px2 = x + flecheSize / 2;
                py2 = y - flecheSize;
                break;
            default:
                return;
        }
        
        g.drawLine(x, y, px1, py1);
        g.drawLine(x, y, px2, py2);
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

    /**
     * Vérifie si une position de souris est sur le point d'ancrage d'origine
     */
    public boolean isOnOriginAnchor(Point mouse) {
        if (ancrageOrigine == null) return false;
        return mouse.distance(ancrageOrigine) <= ANCHOR_RADIUS;
    }

    /**
     * Vérifie si une position de souris est sur le point d'ancrage de destination
     */
    public boolean isOnDestinationAnchor(Point mouse) {
        if (ancrageDestination == null) return false;
        return mouse.distance(ancrageDestination) <= ANCHOR_RADIUS;
    }

    /**
     * Déplace le point d'ancrage d'origine
     */
    public void dragOriginAnchor(Point mouse) {
        // Déterminer le côté le plus proche
        int closestSide = getClosestSide(mouse, blocOrigine);
        sideOrigine = closestSide;
        posRelOrigine = getRelativePosFromMouse(mouse, blocOrigine, closestSide);
    }

    /**
     * Déplace le point d'ancrage de destination
     */
    public void dragDestinationAnchor(Point mouse) {
        // Déterminer le côté le plus proche
        int closestSide = getClosestSide(mouse, blocDestination);
        sideDestination = closestSide;
        posRelDestination = getRelativePosFromMouse(mouse, blocDestination, closestSide);
    }

    /**
     * Définit le côté de connexion pour l'origine
     */
    public void setSideOrigine(int side) {
        this.sideOrigine = side;
    }

    /**
     * Définit le côté de connexion pour la destination
     */
    public void setSideDestination(int side) {
        this.sideDestination = side;
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
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMultOrig() {
        return multOrig;
    }

    public void setMultOrig(String multOrig) {
        this.multOrig = multOrig;
    }

    public String getMultDest() {
        return multDest;
    }

    public void setMultDest(String multDest) {
        this.multDest = multDest;
    }

    @Override
    public String toString() {
        return "LiaisonVue{" +
                "blocOrigine=" + blocOrigine.getClass().getSimpleName() +
                ", blocDestination=" + blocDestination.getClass().getSimpleName() +
                '}';
    }
}