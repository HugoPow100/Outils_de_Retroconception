package vue.liaison;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import vue.BlocClasse;

/**
 * Classe qui gère l'affichage visuel des liens entre 2 {@link BlocClasse}s
 * Refactorisée avec délégation aux classes helper
 * @author Jules
 */
public class LiaisonVue {

    // Attributs principaux
    private String type; //association, interface, heritage
    private BlocClasse blocOrigine;
    private BlocClasse blocDestination;

    //POUR ASSOCIATION UNIQUEMENT
    private boolean unidirectionnel;
    private String multOrig;
    private String multDest;

    private Point ancrageOrigine;
    private Point ancrageDestination;
    private double posRelOrigine;
    private int sideDestination;
    // Références externes
    private List<BlocClasse> tousLesBlocs = new ArrayList<>();
    private List<LiaisonVue> toutesLesLiaisons = new ArrayList<>();
    
    // Classes helper
    private DetecteurObstacles detecteurObstacles;
    private CalculateurChemin calculateurChemin;
    private GestionnaireIntersections gestionnaireIntersections;
    private RenduLiaison renduLiaison;

    // Constructeurs
    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type) {
        this(blocOrigine, blocDestination, type, true, "", "");
    }

    // constructeur pour les type "ASSOCIATION"
    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type, 
                      boolean unidirectionnel, String multOrig, String multDest) {
        this.blocOrigine = blocOrigine;
        this.blocDestination = blocDestination;
        this.type = type;
        this.unidirectionnel = unidirectionnel;
        this.multOrig = multOrig;
        this.multDest = multDest;
        
        initializeHelpers();
        chooseBestSides();
    }
    
    private void initializeHelpers() {
        this.detecteurObstacles = new DetecteurObstacles(blocOrigine, blocDestination, tousLesBlocs);
        this.calculateurChemin = new CalculateurChemin(detecteurObstacles);
        this.gestionnaireIntersections = new GestionnaireIntersections();
        this.renduLiaison = new RenduLiaison();
    }

    public void recalculerAncrages() {
        chooseBestSides();
    }

    public void setToutesLesLiaisons(List<LiaisonVue> liaisons) {
        this.toutesLesLiaisons = liaisons;
    }
    
    public void setTousLesBlocs(List<BlocClasse> blocs) {
        this.tousLesBlocs = blocs;
        initializeHelpers();
    }

    /**
     * Choisit les meilleurs côtés et positions pour minimiser le nombre de segments
     */
    private void chooseBestSides() {
        int bestOrigSide = 0, bestDestSide = 0;
        double bestOrigPos = 0.5, bestDestPos = 0.5;
        int minSegments = Integer.MAX_VALUE;
        double minDistance = Double.MAX_VALUE;
        int bestCenterPriority = Integer.MAX_VALUE;
        boolean foundDirect = false;
        
        final int ox = blocOrigine.getX() + (blocOrigine.getLargeur() >> 1);
        final int oy = blocOrigine.getY() + (blocOrigine.getHauteurCalculee() >> 1);
        final int dx = blocDestination.getX() + (blocDestination.getLargeur() >> 1);
        final int dy = blocDestination.getY() + (blocDestination.getHauteurCalculee() >> 1);
        
        final boolean destIsRight = dx > ox, destIsLeft = dx < ox;
        final boolean destIsBelow = dy > oy, destIsAbove = dy < oy;
        
        // PHASE 1 : Chemins directs
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                boolean isNaturalPair = isNaturalSidePair(origSide, destSide, destIsRight, destIsLeft, destIsBelow, destIsAbove);
                boolean samePosPair = (origSide + destSide == 2) || (origSide + destSide == 4);
                
                if (samePosPair) {
                    for (double pos = 0.0; pos <= 1.0; pos += 0.02) {
                        if (isAnchorOccupied(blocOrigine, origSide, pos) || 
                            isAnchorOccupied(blocDestination, destSide, pos)) continue;
                        
                        Point start = GestionnaireAncrage.getPointOnSide(blocOrigine, origSide, pos);
                        Point end = GestionnaireAncrage.getPointOnSide(blocDestination, destSide, pos);
                        
                        DirectPathResult result = checkDirectPath(start, end, pos, isNaturalPair);
                        if (result.canDirect && updateBestIfBetter(result, 1, bestCenterPriority, minDistance, foundDirect)) {
                            bestOrigSide = origSide; bestDestSide = destSide;
                            bestOrigPos = pos; bestDestPos = pos;
                            minSegments = 1; minDistance = result.distance;
                            bestCenterPriority = result.priority;
                            foundDirect = true;
                        }
                    }
                } else {
                    for (double origPos = 0.0; origPos <= 1.0; origPos += 0.1) {
                        for (double destPos = 0.0; destPos <= 1.0; destPos += 0.1) {
                            if (isAnchorOccupied(blocOrigine, origSide, origPos) || 
                                isAnchorOccupied(blocDestination, destSide, destPos)) continue;
                            
                            Point start = GestionnaireAncrage.getPointOnSide(blocOrigine, origSide, origPos);
                            Point end = GestionnaireAncrage.getPointOnSide(blocDestination, destSide, destPos);
                            
                            if (start.x != end.x && start.y != end.y) continue;
                            
                            DirectPathResult result = checkDirectPath(start, end, (origPos + destPos) / 2, isNaturalPair);
                            if (result.canDirect && updateBestIfBetter(result, 1, bestCenterPriority, minDistance, foundDirect)) {
                                bestOrigSide = origSide; bestDestSide = destSide;
                                bestOrigPos = origPos; bestDestPos = destPos;
                                minSegments = 1; minDistance = result.distance;
                                bestCenterPriority = result.priority;
                                foundDirect = true;
                            }
                        }
                    }
                }
            }
        }
        
        if (foundDirect) {
            this.sideOrigine = bestOrigSide;
            this.sideDestination = bestDestSide;
            this.posRelOrigine = bestOrigPos;
            this.posRelDestination = bestDestPos;
            return;
        }
        
        // PHASE 2 : Chemins multi-segments
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                boolean isNaturalPair = isNaturalSidePair(origSide, destSide, destIsRight, destIsLeft, destIsBelow, destIsAbove);
                
                for (double origPos = 0.1; origPos <= 0.9; origPos += 0.1) {
                    for (double destPos = 0.1; destPos <= 0.9; destPos += 0.1) {
                        if (isAnchorOccupied(blocOrigine, origSide, origPos) || 
                            isAnchorOccupied(blocDestination, destSide, destPos)) continue;
                        
                        Point start = GestionnaireAncrage.getPointOnSide(blocOrigine, origSide, origPos);
                        Point end = GestionnaireAncrage.getPointOnSide(blocDestination, destSide, destPos);
                        
                        List<Point> testPath = calculateurChemin.createOrthogonalPath(start, end, origSide, destSide);
                        
                        if (!calculateurChemin.pathHasCollisions(testPath)) {
                            int numSegments = testPath.size() - 1;
                            double distance = calculateurChemin.calculatePathLength(testPath);
                            int naturalBonus = isNaturalPair ? -1000 : 0;
                            
                            if (numSegments < minSegments || 
                                (numSegments == minSegments && shouldUpdateMultiSegment(isNaturalPair, bestCenterPriority, distance, minDistance))) {
                                minSegments = numSegments;
                                minDistance = distance;
                                bestCenterPriority = naturalBonus;
                                bestOrigSide = origSide; bestDestSide = destSide;
                                bestOrigPos = origPos; bestDestPos = destPos;
                            }
                        }
                    }
                }
            }
        }
        
        this.sideOrigine = bestOrigSide;
        this.sideDestination = bestDestSide;
        this.posRelOrigine = bestOrigPos;
        this.posRelDestination = bestDestPos;
    }
    
    private boolean isNaturalSidePair(int oSide, int dSide, boolean right, boolean left, boolean below, boolean above) {
        return (oSide == 0 && dSide == 2 && right) || (oSide == 2 && dSide == 0 && left) ||
               (oSide == 1 && dSide == 3 && below) || (oSide == 3 && dSide == 1 && above);
    }
    
    private DirectPathResult checkDirectPath(Point start, Point end, double pos, boolean isNatural) {
        DirectPathResult result = new DirectPathResult();
        
        if (start.x == end.x) {
            result.canDirect = !detecteurObstacles.hasVerticalObstacleStrict(start.x, start.y, end.y);
            result.distance = Math.abs(end.y - start.y);
        } else if (start.y == end.y) {
            result.canDirect = !detecteurObstacles.hasHorizontalObstacleStrict(start.x, end.x, start.y);
            result.distance = Math.abs(end.x - start.x);
        }
        
        if (result.canDirect) {
            int centerPriority = GestionnaireAncrage.calculateCenterPriority(pos);
            int naturalBonus = isNatural ? -1000 : 0;
            result.priority = centerPriority + naturalBonus;
        }
        
        return result;
    }
    
    private boolean updateBestIfBetter(DirectPathResult result, int numSeg, int bestPriority, double bestDist, boolean foundDirect) {
        if (!foundDirect) return true;
        if (result.priority < bestPriority) return true;
        if (result.priority == bestPriority && result.distance < bestDist) return true;
        return false;
    }
    
    private boolean shouldUpdateMultiSegment(boolean isNatural, int bestPriority, double dist, double minDist) {
        if (isNatural && bestPriority >= 0) return true;
        if ((isNatural && bestPriority < 0) || (!isNatural && bestPriority >= 0)) return dist < minDist;
        return false;
    }
    
    private static class DirectPathResult {
        boolean canDirect;
        double distance;
        int priority;
    }
    
    private boolean isAnchorOccupied(BlocClasse bloc, int side, double pos) {
        final double TOLERANCE = 0.02;
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            if (autre.blocOrigine == bloc && autre.sideOrigine == side && Math.abs(autre.posRelOrigine - pos) < TOLERANCE) return true;
            if (autre.blocDestination == bloc && autre.sideDestination == side && Math.abs(autre.posRelDestination - pos) < TOLERANCE) return true;
        }
        return false;
    }
    
    private int calculatePathOffset(List<Point> myPath) {
        if (toutesLesLiaisons == null || toutesLesLiaisons.size() <= 1 || myPath.size() < 2) return 0;
        
        final int OFFSET_DISTANCE = 4;
        List<LiaisonVue> overlapping = new ArrayList<>();
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            Point autreOrig = GestionnaireAncrage.getPointOnSide(autre.blocOrigine, autre.sideOrigine, autre.posRelOrigine);
            Point autreDest = GestionnaireAncrage.getPointOnSide(autre.blocDestination, autre.sideDestination, autre.posRelDestination);
            List<Point> autrePath = calculateurChemin.createOrthogonalPath(autreOrig, autreDest, autre.sideOrigine, autre.sideDestination);
            
            if (gestionnaireIntersections.pathsShareSegments(myPath, autrePath)) {
                overlapping.add(autre);
            }
        }
        
        if (overlapping.isEmpty()) return 0;
        
        int myIndex = toutesLesLiaisons.indexOf(this);
        int offsetIdx = 0;
        for (LiaisonVue autre : overlapping) {
            if (toutesLesLiaisons.indexOf(autre) < myIndex) offsetIdx++;
        }
        
        if (offsetIdx == 0) return 0;
        return (offsetIdx % 2 == 1) ? ((offsetIdx + 1) / 2) * OFFSET_DISTANCE : -(offsetIdx / 2) * OFFSET_DISTANCE;
    }
    
    private List<Point> findIntersections(List<Point> myPath) {
        List<Point> intersections = new ArrayList<>();
        if (toutesLesLiaisons == null) return intersections;
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            
            Point autreOrig = GestionnaireAncrage.getPointOnSide(autre.blocOrigine, autre.sideOrigine, autre.posRelOrigine);
            Point autreDest = GestionnaireAncrage.getPointOnSide(autre.blocDestination, autre.sideDestination, autre.posRelDestination);
            List<Point> autreOrigPath = calculateurChemin.createOrthogonalPath(autreOrig, autreDest, autre.sideOrigine, autre.sideDestination);
            
            int autreOffset = autre.calculatePathOffset(autreOrigPath);
            List<Point> autrePath = autreOffset != 0 ? calculateurChemin.applyOffsetToPath(autreOrigPath, autreOffset) : autreOrigPath;
            
            for (int i = 0; i < myPath.size() - 1; i++) {
                for (int j = 0; j < autrePath.size() - 1; j++) {
                    Point inter = gestionnaireIntersections.getSegmentIntersection(
                        myPath.get(i), myPath.get(i + 1),
                        autrePath.get(j), autrePath.get(j + 1)
                    );
                    
                    if (inter != null && !containsPoint(intersections, inter, 3)) {
                        intersections.add(inter);
                    }
                }
            }
        }
        return intersections;
    }
    
    private boolean containsPoint(List<Point> points, Point p, int tolerance) {
        for (Point existing : points) {
            if (Math.abs(existing.x - p.x) < tolerance && Math.abs(existing.y - p.y) < tolerance) {
                return true;
            }
        }
        return false;
    }

    public void dessiner(Graphics2D g) {
        ancrageOrigine = GestionnaireAncrage.getPointOnSide(blocOrigine, sideOrigine, posRelOrigine);
        ancrageDestination = GestionnaireAncrage.getPointOnSide(blocDestination, sideDestination, posRelDestination);

        List<Point> originalPath = calculateurChemin.createOrthogonalPath(ancrageOrigine, ancrageDestination, sideOrigine, sideDestination);
        int offset = calculatePathOffset(originalPath);
        List<Point> path = offset != 0 ? calculateurChemin.applyOffsetToPath(originalPath, offset) : originalPath;
        List<Point> intersections = findIntersections(path);

        g.setColor(Color.BLACK);
        Stroke normalStroke = type.equals("interface") ? 
            new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5.0f, 5.0f}, 0) :
            new BasicStroke(1);
        g.setStroke(normalStroke);
        
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            
            List<Point> segmentIntersections = getSegmentIntersections(p1, p2, intersections);
            
            if (segmentIntersections.isEmpty()) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            } else {
                renduLiaison.drawLineWithBridges(g, p1, p2, segmentIntersections, normalStroke);
            }
        }

        if (type.equals("association") && unidirectionnel) {
            renduLiaison.dessinerFlecheAssociation(g, ancrageDestination, sideDestination);
        }
        if (type.equals("heritage") || type.equals("interface")) {
            renduLiaison.dessinerFlecheVide(g, ancrageDestination, sideDestination);
        }
        
        if (type.equals("association")) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            FontMetrics fm = g.getFontMetrics();

            if (multOrig != null && !multOrig.isEmpty()) {
                Point pos = GestionnaireAncrage.calculateMultiplicityPosition(ancrageOrigine, sideOrigine, fm.stringWidth(multOrig), fm.getAscent());
                g.drawString(multOrig, pos.x, pos.y);
            }
            if (multDest != null && !multDest.isEmpty()) {
                Point pos = GestionnaireAncrage.calculateMultiplicityPosition(ancrageDestination, sideDestination, fm.stringWidth(multDest), fm.getAscent());
                g.drawString(multDest, pos.x, pos.y);
            }
        }
    }
    
    private List<Point> getSegmentIntersections(Point p1, Point p2, List<Point> allIntersections) {
        List<Point> segInter = new ArrayList<>();
        boolean horizontal = (p1.y == p2.y);
        boolean vertical = (p1.x == p2.x);
        
        for (Point inter : allIntersections) {
            if (horizontal) {
                int minX = Math.min(p1.x, p2.x);
                int maxX = Math.max(p1.x, p2.x);
                if (inter.x > minX && inter.x < maxX && Math.abs(inter.y - p1.y) < 2) {
                    segInter.add(inter);
                }
            } else if (vertical) {
                int minY = Math.min(p1.y, p2.y);
                int maxY = Math.max(p1.y, p2.y);
                if (inter.y > minY && inter.y < maxY && Math.abs(inter.x - p1.x) < 2) {
                    segInter.add(inter);
                }
            }
        }
        return segInter;
    }

    // Méthodes de gestion d'ancrage déléguées
    public boolean isOnOriginAnchor(Point m) { 
        return GestionnaireAncrage.isOnAnchor(ancrageOrigine, m, 1, 0, 0, 0, 0); 
    }
    
    public boolean isOnOriginAnchor(Point m, double z, int px, int py, int w, int h) { 
        return GestionnaireAncrage.isOnAnchor(ancrageOrigine, m, z, px, py, w, h); 
    }
    
    public boolean isOnDestinationAnchor(Point m) { 
        return GestionnaireAncrage.isOnAnchor(ancrageDestination, m, 1, 0, 0, 0, 0); 
    }
    
    public boolean isOnDestinationAnchor(Point m, double z, int px, int py, int w, int h) { 
        return GestionnaireAncrage.isOnAnchor(ancrageDestination, m, z, px, py, w, h); 
    }
    
    private void dragAnchor(Point m, double z, int px, int py, int w, int h, BlocClasse bloc, boolean isOrig) {
        Point lm = z == 1.0 ? m : new Point((int)((m.x-px-w/2)/z+w/(2*z)), (int)((m.y-py-h/2)/z+h/(2*z)));
        int s = GestionnaireAncrage.getClosestSide(lm, bloc);
        double p = GestionnaireAncrage.getRelativePosFromMouse(lm, bloc, s);
        if (isOrig) { sideOrigine = s; posRelOrigine = p; }
        else { sideDestination = s; posRelDestination = p; }
    }
    
    public void dragOriginAnchor(Point m) { 
        dragAnchor(m, 1, 0, 0, 0, 0, blocOrigine, true); 
    }
    
    public void dragOriginAnchor(Point m, double z, int px, int py, int w, int h) { 
        dragAnchor(m, z, px, py, w, h, blocOrigine, true); 
    }
    
    public void dragDestinationAnchor(Point m) { 
        dragAnchor(m, 1, 0, 0, 0, 0, blocDestination, false); 
    }
    
    public void dragDestinationAnchor(Point m, double z, int px, int py, int w, int h) { 
        dragAnchor(m, z, px, py, w, h, blocDestination, false); 
    }

    public void setSideOrigine(int side) { this.sideOrigine = side; }
    public void setSideDestination(int side) { this.sideDestination = side; }
    
    public void adjustPositionToAvoidOverlap(List<LiaisonVue> allLiaisons, int index) {
        List<Integer> sameOriginSide = new ArrayList<>();
        for (int i = 0; i < allLiaisons.size(); i++) {
            LiaisonVue other = allLiaisons.get(i);
            if (other.blocOrigine == this.blocOrigine && other.sideOrigine == this.sideOrigine) {
                sameOriginSide.add(i);
            }
        }
        
        if (sameOriginSide.size() > 1) {
            int myPosition = sameOriginSide.indexOf(index);
            this.posRelOrigine = 0.2 + (0.6 * myPosition / (sameOriginSide.size() - 1));
        }
        
        List<Integer> sameDestSide = new ArrayList<>();
        for (int i = 0; i < allLiaisons.size(); i++) {
            LiaisonVue other = allLiaisons.get(i);
            if (other.blocDestination == this.blocDestination && other.sideDestination == this.sideDestination) {
                sameDestSide.add(i);
            }
        }
        
        if (sameDestSide.size() > 1) {
            int myPosition = sameDestSide.indexOf(index);
            this.posRelDestination = 0.2 + (0.6 * myPosition / (sameDestSide.size() - 1));
        }
    }

    // Getters/Setters
    public BlocClasse getBlocOrigine() { return blocOrigine; }
    public void setBlocOrigine(BlocClasse blocOrigine) { this.blocOrigine = blocOrigine; initializeHelpers(); }
    public BlocClasse getBlocDestination() { return blocDestination; }
    public void setBlocDestination(BlocClasse blocDestination) { this.blocDestination = blocDestination; initializeHelpers(); }
    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }
    public String getMultOrig() { return multOrig; }
    public void setMultOrig(String multOrig) { this.multOrig = multOrig; }
    public String getMultDest() { return multDest; }
    public void setMultDest(String multDest) { this.multDest = multDest; }
    public boolean isUnidirectionnel() { return unidirectionnel; }
    public void setUnidirectionnel(boolean unidirectionnel) { this.unidirectionnel = unidirectionnel; }
}
