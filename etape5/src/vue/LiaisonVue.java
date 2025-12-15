package vue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
* Classe qui gère l'affichage visuel des liens entre 2 {@link BlocClasse}s
* @author Jules
*/
public class LiaisonVue 
{

    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

    private String              type; // association, heritage, interface
    private boolean             unidirectionnel;
    private String              multOrig;
    private String              multDest;

    private BlocClasse          blocOrigine;
    private BlocClasse          blocDestination;

    private Point               ancrageOrigine;
    private Point               ancrageDestination;

    // Stockage des positions des points d'ancrage (0-100, pourcentage le long du bord)
    private double              posRelOrigine;  // Position relative sur le bord de l'origine
    private double              posRelDestination; // Position relative sur le bord de la destination
    
    // Côtés choisis pour les connexions (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT)
    private int                 sideOrigine;
    private int                 sideDestination;
    
    // Rayon pour cliquer sur les points d'ancrage
    private static final int    ANCHOR_RADIUS = 10;
    
    // Référence à la liste des blocs pour le routage avec évitement
    private List<BlocClasse>    tousLesBlocs = new ArrayList<>();


    //--------------------------//
    //      CONSTRUCTEURS       //
    //--------------------------//

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

    //----------------------//
    //      METHODES        //
    //----------------------//

    /**
    * Renvoie un point sur un coté d'un {@link BlocClasse}, en fonction d'un coté et d'une position relative
    * @param bloc Le bloc sur lequel se baser
    * @param side Un entier de 0 à 3. 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
    * @param posRel La position relative de 0 à 100 (pourcentage le long du bord) sur le coté du bloc 
    * @return Un {@link Point}
    */
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

    /**
    * Renvoie un entier qui représente le coté le plus proche d'un point depuis un {@link BlocClasse}
    * @param mouse Le point de l'endroit de la souris
    * @param bloc Le bloc sur lequel baser le ccoté
    * @return Un entier de 0 à 3 qui représente le coté d'un blocClasse. 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
    */
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

    /**
    * Renvoie la position relative d'un bloc (de 0 à 100) en fonction d'un bloc, un point proche et un coté donné.
    * @param mouse Le point de l'endroit de la souris
    * @param bloc Le bloc sur lequel baser le ccoté
    * @param side Le coté sur lequel baser la position : un entier de 0 à 3. 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
    * @return La position relative d'un coté d'un bloc de 0 à 100 
    */
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

    /**
    * Crée une liste de points qui représente un chemin d'un point Start à un point End.
    * @param start Point de départ
    * @param end Point d'arrivée
    * @param startSide Coté de départ du bloc (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT)
    * @param endSide Coté d'arrivée (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT) 
    * @return Une {@link List} de {@link Point}s, qui représente le chemin.
    */
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

        // Segment intermédiaire : aller vers la destination avec contournement
        if (startSide == 0 || startSide == 2) { // Sortie horizontale
            // Point intermédiaire horizontal
            int intermediateY = midY2;
            
            // Vérifier s'il y a une collision sur la ligne horizontale
            List<BlocClasse> obstaclesHorizontaux = getObstaclesOnHorizontalLine(midX, midX2, intermediateY);
            
            if (!obstaclesHorizontaux.isEmpty()) {
                // Contourner en passant par le haut ou le bas
                int deflectY = getDeflectionY(intermediateY, obstaclesHorizontaux);
                path.add(new Point(midX, deflectY));
                path.add(new Point(midX2, deflectY));
                path.add(new Point(midX2, intermediateY));
            } else {
                path.add(new Point(midX, intermediateY));
                path.add(new Point(midX2, intermediateY));
            }
        } else { // Sortie verticale
            // Point intermédiaire vertical
            int intermediateX = midX2;
            
            // Vérifier s'il y a une collision sur la ligne verticale
            List<BlocClasse> obstaclesVerticals = getObstaclesOnVerticalLine(intermediateX, midY, midY2);
            
            if (!obstaclesVerticals.isEmpty()) {
                // Contourner en passant par la gauche ou la droite
                int deflectX = getDeflectionX(intermediateX, obstaclesVerticals);
                path.add(new Point(deflectX, midY));
                path.add(new Point(deflectX, midY2));
                path.add(new Point(intermediateX, midY2));
            } else {
                path.add(new Point(intermediateX, midY));
                path.add(new Point(intermediateX, midY2));
            }
        }

        path.add(end);
        return path;
    }
    
    /**
    * Renvoie la liste les blocs qui coupent une ligne horizontale
    * @param x1 Abcisse du début de la ligne
    * @param x2 Abcisse de la fin de la ligne
    * @param y Ordonnée de la ligne
    * @return Une {@link List} de {@link BlocClasse}s, qui représente les blocs qui sont sur la ligne donnée.
    */
    private List<BlocClasse> getObstaclesOnHorizontalLine(int x1, int x2, int y) {
        List<BlocClasse> obstacles = new ArrayList<>();
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int margin = 5;
        
        for (BlocClasse bloc : tousLesBlocs) {
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // Vérifier si la ligne y traverse le bloc
            if (y >= by - margin && y <= by + bh + margin &&
                !(maxX < bx - margin || minX > bx + bw + margin)) {
                obstacles.add(bloc);
            }
        }
        
        return obstacles;
    }
    
    /**
    * Renvoie la liste les blocs qui coupent une ligne verticale
    * @param x Abcisse de la ligne
    * @param y Ordonnée du début de la ligne
    * @param y Ordonnée de la fin de la ligne
    * @return Une {@link List} de {@link BlocClasse}s, qui représente les blocs qui sont sur la ligne donnée.
    */
    private List<BlocClasse> getObstaclesOnVerticalLine(int x, int y1, int y2) 
    {
        List<BlocClasse> obstacles = new ArrayList<>();
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int margin = 5;
        
        for (BlocClasse bloc : tousLesBlocs) {
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // Vérifier si la ligne x traverse le bloc
            if (x >= bx - margin && x <= bx + bw + margin &&
                !(maxY < by - margin || minY > by + bh + margin)) {
                obstacles.add(bloc);
            }
        }
        
        return obstacles;
    }
    
    /**
    * Calcule une position Y pour contourner les obstacles horizontaux
    * @param originalY
    * @param 
    * @param 
    * @return 
    */
    private int getDeflectionY(int originalY, List<BlocClasse> obstacles) {
        int topMin = Integer.MAX_VALUE;
        int bottomMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            topMin = Math.min(topMin, bloc.getY());
            bottomMax = Math.max(bottomMax, bloc.getY() + bloc.getHauteurCalculee());
        }
        
        // Choisir le côté avec plus d'espace
        int spaceAbove = topMin - 100; // Supposer 100 de marge
        int spaceBelow = 1000 - bottomMax; // Supposer 1000 de hauteur totale
        
        if (spaceAbove > spaceBelow) {
            return topMin - 20; // Passer au-dessus
        } else {
            return bottomMax + 20; // Passer en-dessous
        }
    }
    
    /**
     * Calcule une position X pour contourner les obstacles verticaux
     */
    private int getDeflectionX(int originalX, List<BlocClasse> obstacles) {
        int leftMin = Integer.MAX_VALUE;
        int rightMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            leftMin = Math.min(leftMin, bloc.getX());
            rightMax = Math.max(rightMax, bloc.getX() + bloc.getLargeur());
        }
        
        // Choisir le côté avec plus d'espace
        int spaceLeft = leftMin - 100; // Supposer 100 de marge
        int spaceRight = 1000 - rightMax; // Supposer 1000 de largeur totale
        
        if (spaceLeft > spaceRight) {
            return leftMin - 20; // Passer à gauche
        } else {
            return rightMax + 20; // Passer à droite
        }
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

        // Définir le style de trait selon le type de liaison
        g.setColor(Color.BLACK);
        if (this.type.equals("interface")) {
            // Pointillés pour les interfaces
            float[] dashPattern = {5.0f, 5.0f};
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
        } else {
            // Trait plein pour association et héritage
            g.setStroke(new BasicStroke(1));
        }
        
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        if (this.type.equals("association") && unidirectionnel) {
            dessinerFlecheAssociation(g, ancrageDestination, sideDestination);
        }


        if (this.type.equals("heritage") || this.type.equals("interface")) {
            dessinerFlecheVide(g, ancrageDestination, sideDestination);
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

        private void dessinerFlecheVide(Graphics2D g, Point anchor, int side) {
            int flecheSize = 10;
            int x = anchor.x;
            int y = anchor.y;
            int px1, py1, px2, py2, px3, py3;
            
            switch(side) {
                case 0: // DROITE
                    px1 = x + flecheSize;
                    py1 = y - flecheSize / 2;
                    px2 = x + flecheSize;
                    py2 = y + flecheSize / 2;
                    px3 = x;
                    py3 = y;
                    break;
                case 1: // BAS
                    px1 = x - flecheSize / 2;
                    py1 = y + flecheSize;
                    px2 = x + flecheSize / 2;
                    py2 = y + flecheSize;
                    px3 = x;
                    py3 = y;
                    break;
                case 2: // GAUCHE
                    px1 = x - flecheSize;
                    py1 = y - flecheSize / 2;
                    px2 = x - flecheSize;
                    py2 = y + flecheSize / 2;
                    px3 = x;
                    py3 = y;
                    break;
                case 3: // HAUT
                    px1 = x - flecheSize / 2;
                    py1 = y - flecheSize;
                    px2 = x + flecheSize / 2;
                    py2 = y - flecheSize;
                    px3 = x;
                    py3 = y;
                    break;
                default:
                    return;
            }
            
            int[] xPoints = {px1, px3, px2};
            int[] yPoints = {py1, py3, py2};
            g.drawPolygon(xPoints, yPoints, 3);
        }


    /**
     * Dessine une flèche basée sur le côté d'arrivée
     * side: 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
     */
    private void dessinerFlecheAssociation(Graphics2D g, Point anchor, int side) {
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
    
    /**
     * Vérifie si une position de souris est sur le point d'ancrage d'origine
     */
    public boolean isOnOriginAnchor(Point mouse) {
        if (ancrageOrigine == null) return false;
        return mouse.distance(ancrageOrigine) <= ANCHOR_RADIUS;
    }
    
    /**
     * Vérifie si une position de souris est sur le point d'ancrage d'origine (avec zoom et pan)
     */
    public boolean isOnOriginAnchor(Point mouse, double zoomLevel, int panOffsetX, int panOffsetY, int panelWidth, int panelHeight) {
        if (ancrageOrigine == null) return false;
        
        // Convertir le point d'ancrage en coordonnées écran
        double screenX = ancrageOrigine.x * zoomLevel + panelWidth / 2 - panelWidth / (2 * zoomLevel) * zoomLevel + panOffsetX;
        double screenY = ancrageOrigine.y * zoomLevel + panelHeight / 2 - panelHeight / (2 * zoomLevel) * zoomLevel + panOffsetY;
        
        return Math.sqrt(Math.pow(mouse.x - screenX, 2) + Math.pow(mouse.y - screenY, 2)) <= ANCHOR_RADIUS;
    }

    /**
     * Vérifie si une position de souris est sur le point d'ancrage de destination
     */
    public boolean isOnDestinationAnchor(Point mouse) {
        if (ancrageDestination == null) return false;
        return mouse.distance(ancrageDestination) <= ANCHOR_RADIUS;
    }
    
    /**
     * Vérifie si une position de souris est sur le point d'ancrage de destination (avec zoom et pan)
     */
    public boolean isOnDestinationAnchor(Point mouse, double zoomLevel, int panOffsetX, int panOffsetY, int panelWidth, int panelHeight) {
        if (ancrageDestination == null) return false;
        
        // Convertir le point d'ancrage en coordonnées écran
        double screenX = ancrageDestination.x * zoomLevel + panelWidth / 2 - panelWidth / (2 * zoomLevel) * zoomLevel + panOffsetX;
        double screenY = ancrageDestination.y * zoomLevel + panelHeight / 2 - panelHeight / (2 * zoomLevel) * zoomLevel + panOffsetY;
        
        return Math.sqrt(Math.pow(mouse.x - screenX, 2) + Math.pow(mouse.y - screenY, 2)) <= ANCHOR_RADIUS;
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
     * Déplace le point d'ancrage d'origine (avec zoom et pan)
     */
    public void dragOriginAnchor(Point mouse, double zoomLevel, int panOffsetX, int panOffsetY, int panelWidth, int panelHeight) {
        // Convertir coordonnées écran en coordonnées logiques
        double logicalX = (mouse.x - panOffsetX - panelWidth / 2) / zoomLevel + panelWidth / (2 * zoomLevel);
        double logicalY = (mouse.y - panOffsetY - panelHeight / 2) / zoomLevel + panelHeight / (2 * zoomLevel);
        Point logicalMouse = new Point((int)logicalX, (int)logicalY);
        
        // Déterminer le côté le plus proche
        int closestSide = getClosestSide(logicalMouse, blocOrigine);
        sideOrigine = closestSide;
        posRelOrigine = getRelativePosFromMouse(logicalMouse, blocOrigine, closestSide);
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
     * Déplace le point d'ancrage de destination (avec zoom et pan)
     */
    public void dragDestinationAnchor(Point mouse, double zoomLevel, int panOffsetX, int panOffsetY, int panelWidth, int panelHeight) {
        // Convertir coordonnées écran en coordonnées logiques
        double logicalX = (mouse.x - panOffsetX - panelWidth / 2) / zoomLevel + panelWidth / (2 * zoomLevel);
        double logicalY = (mouse.y - panOffsetY - panelHeight / 2) / zoomLevel + panelHeight / (2 * zoomLevel);
        Point logicalMouse = new Point((int)logicalX, (int)logicalY);
        
        // Déterminer le côté le plus proche
        int closestSide = getClosestSide(logicalMouse, blocDestination);
        sideDestination = closestSide;
        posRelDestination = getRelativePosFromMouse(logicalMouse, blocDestination, closestSide);
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
    
    /**
     * Définit la liste de tous les blocs pour l'évitement
     */
    public void setTousLesBlocs(List<BlocClasse> blocs) {
        this.tousLesBlocs = blocs;
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