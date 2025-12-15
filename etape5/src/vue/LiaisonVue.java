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
        
        // Choisir automatiquement les meilleurs côtés
        chooseBestSides();
    }

    public LiaisonVue(BlocClasse blocOrigine, BlocClasse blocDestination, String type, boolean unidirectionnel, String multOrig, String multDest) 
    {
        this.blocOrigine       = blocOrigine;
        this.blocDestination   = blocDestination;
        this.type              = type;
        this.unidirectionnel   = unidirectionnel;
        this.multOrig          = multOrig;
        this.multDest          = multDest;
        
        // Choisir automatiquement les meilleurs côtés
        chooseBestSides();
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    /**
     * Recalcule les ancrages optimaux (appelé par le bouton "Optimiser les liaisons uniquement")
     */
    public void recalculerAncrages() {
        chooseBestSides();
    }

    /**
     * Choisit automatiquement les meilleurs côtés et positions pour minimiser la DISTANCE TOTALE
     * et éviter les collisions. PRIORITÉ ABSOLUE aux chemins directs (1 segment).
     * Parmi les chemins directs équivalents, PRÉFÉRER positions parfaitement centrées sur le plus petit bloc.
     */
    private void chooseBestSides() {
        int bestOrigSide = 0;
        int bestDestSide = 0;
        double bestOrigPos = 0.5;
        double bestDestPos = 0.5;
        double minDistance = Double.MAX_VALUE;
        int bestCenterPriority = Integer.MAX_VALUE; // Plus petit = meilleur
        boolean foundDirect = false;
        
        // Déterminer les côtés "naturels" selon la position relative des blocs
        int ox = blocOrigine.getX() + blocOrigine.getLargeur() / 2;
        int oy = blocOrigine.getY() + blocOrigine.getHauteurCalculee() / 2;
        int dx = blocDestination.getX() + blocDestination.getLargeur() / 2;
        int dy = blocDestination.getY() + blocDestination.getHauteurCalculee() / 2;
        
        boolean destIsRight = dx > ox;  // Destination à droite
        boolean destIsLeft = dx < ox;   // Destination à gauche
        boolean destIsBelow = dy > oy;  // Destination en bas
        boolean destIsAbove = dy < oy;  // Destination en haut
        
        // PHASE 1 : Recherche EXHAUSTIVE de chemins directs
        // Tester TOUS les côtés et TOUTES les combinaisons de positions
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                // Vérifier si c'est une paire de côtés "logique" selon la position relative
                boolean isNaturalPair = false;
                if (origSide == 0 && destSide == 2 && destIsRight) isNaturalPair = true; // DROITE -> GAUCHE (dest à droite)
                if (origSide == 2 && destSide == 0 && destIsLeft) isNaturalPair = true;  // GAUCHE -> DROITE (dest à gauche)
                if (origSide == 1 && destSide == 3 && destIsBelow) isNaturalPair = true; // BAS -> HAUT (dest en bas)
                if (origSide == 3 && destSide == 1 && destIsAbove) isNaturalPair = true; // HAUT -> BAS (dest en haut)
                
                // Optimisation: tester seulement les paires qui peuvent s'aligner
                // Côtés opposés (0-2, 2-0, 1-3, 3-1) pour même position
                boolean samePosPair = (origSide == 0 && destSide == 2) || (origSide == 2 && destSide == 0) ||
                                      (origSide == 1 && destSide == 3) || (origSide == 3 && destSide == 1);
                
                // Côtés perpendiculaires ou adjacents: tester différentes positions
                boolean canAlign = true;
                
                if (samePosPair) {
                    // Pour les côtés opposés, tester avec LA MÊME position (alignement garanti)
                    for (double pos = 0.0; pos <= 1.0; pos += 0.01) {
                        Point start = getPointOnSide(blocOrigine, origSide, pos);
                        Point end = getPointOnSide(blocDestination, destSide, pos);
                        
                        // Vérifier qu'on peut tracer une ligne directe SANS obstacle
                        boolean canDirect = false;
                        double directDist = 0;
                        
                        if (start.x == end.x) {
                            canDirect = !hasVerticalObstacleStrict(start.x, start.y, end.y);
                            directDist = Math.abs(end.y - start.y);
                        } else if (start.y == end.y) {
                            canDirect = !hasHorizontalObstacleStrict(start.x, end.x, start.y);
                            directDist = Math.abs(end.x - start.x);
                        }
                        
                        if (canDirect) {
                            int centerPriority = calculateCenterPriority(pos);
                            
                            // Bonus si c'est une paire naturelle (côtés logiques)
                            int naturalBonus = isNaturalPair ? -1000 : 0; // -1000 = très haute priorité
                            int totalPriority = centerPriority + naturalBonus;
                            
                            boolean isBetter = false;
                            if (!foundDirect) {
                                isBetter = true;
                            } else if (Math.abs(directDist - minDistance) < 1.0) {
                                // Distances équivalentes: prioriser 1) côtés naturels, 2) centrage
                                isBetter = (totalPriority < bestCenterPriority);
                            } else {
                                isBetter = (directDist < minDistance);
                            }
                            
                            if (isBetter) {
                                minDistance = directDist;
                                bestCenterPriority = totalPriority;
                                bestOrigSide = origSide;
                                bestDestSide = destSide;
                                bestOrigPos = pos;
                                bestDestPos = pos;
                                foundDirect = true;
                            }
                        }
                    }
                } else {
                    // Pour les autres combinaisons: tester DIFFÉRENTES positions pour trouver un alignement
                    // Tester avec un pas plus grand pour ne pas exploser le temps de calcul
                    for (double origPos = 0.0; origPos <= 1.0; origPos += 0.05) {
                        for (double destPos = 0.0; destPos <= 1.0; destPos += 0.05) {
                            Point start = getPointOnSide(blocOrigine, origSide, origPos);
                            Point end = getPointOnSide(blocDestination, destSide, destPos);
                            
                            // Vérifier l'alignement (vertical OU horizontal)
                            boolean aligned = (start.x == end.x) || (start.y == end.y);
                            if (!aligned) continue;
                            
                            // Vérifier qu'on peut tracer une ligne directe SANS obstacle
                            boolean canDirect = false;
                            double directDist = 0;
                            
                            if (start.x == end.x) {
                                canDirect = !hasVerticalObstacleStrict(start.x, start.y, end.y);
                                directDist = Math.abs(end.y - start.y);
                            } else if (start.y == end.y) {
                                canDirect = !hasHorizontalObstacleStrict(start.x, end.x, start.y);
                                directDist = Math.abs(end.x - start.x);
                            }
                            
                            if (canDirect) {
                                // Pour positions différentes, centrage moyen
                                int centerPriority = (calculateCenterPriority(origPos) + calculateCenterPriority(destPos)) / 2;
                                
                                // Bonus si c'est une paire naturelle (côtés logiques)
                                int naturalBonus = isNaturalPair ? -1000 : 0;
                                int totalPriority = centerPriority + naturalBonus;
                                
                                boolean isBetter = false;
                                if (!foundDirect) {
                                    isBetter = true;
                                } else if (Math.abs(directDist - minDistance) < 1.0) {
                                    // Distances équivalentes: prioriser 1) côtés naturels, 2) centrage
                                    isBetter = (totalPriority < bestCenterPriority);
                                } else {
                                    isBetter = (directDist < minDistance);
                                }
                                
                                if (isBetter) {
                                    minDistance = directDist;
                                    bestCenterPriority = totalPriority;
                                    bestOrigSide = origSide;
                                    bestDestSide = destSide;
                                    bestOrigPos = origPos;
                                    bestDestPos = destPos;
                                    foundDirect = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Si on a trouvé un chemin direct, on l'utilise immédiatement
        if (foundDirect) {
            this.sideOrigine = bestOrigSide;
            this.sideDestination = bestDestSide;
            this.posRelOrigine = bestOrigPos;
            this.posRelDestination = bestDestPos;
            return;
        }
        
        // PHASE 2 : Pas de chemin direct possible, chercher le plus court chemin multi-segments
        // PRIORISER les côtés naturels même pour les chemins multi-segments
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                // Vérifier si c'est une paire de côtés "logique" selon la position relative
                boolean isNaturalPair = false;
                if (origSide == 0 && destSide == 2 && destIsRight) isNaturalPair = true; // DROITE -> GAUCHE (dest à droite)
                if (origSide == 2 && destSide == 0 && destIsLeft) isNaturalPair = true;  // GAUCHE -> DROITE (dest à gauche)
                if (origSide == 1 && destSide == 3 && destIsBelow) isNaturalPair = true; // BAS -> HAUT (dest en bas)
                if (origSide == 3 && destSide == 1 && destIsAbove) isNaturalPair = true; // HAUT -> BAS (dest en haut)
                
                // Tester plusieurs positions sur TOUTE la surface
                for (double origPos = 0.1; origPos <= 0.9; origPos += 0.1) {
                    for (double destPos = 0.1; destPos <= 0.9; destPos += 0.1) {
                        Point start = getPointOnSide(blocOrigine, origSide, origPos);
                        Point end = getPointOnSide(blocDestination, destSide, destPos);
                        
                        List<Point> testPath = createOrthogonalPath(start, end, origSide, destSide);
                        
                        if (!pathHasCollisions(testPath)) {
                            double distance = calculatePathLength(testPath);
                            
                            // Appliquer un bonus pour les paires naturelles (réduire la distance de 30%)
                            double adjustedDistance = isNaturalPair ? distance * 0.7 : distance;
                            
                            if (adjustedDistance < minDistance) {
                                minDistance = adjustedDistance;
                                bestOrigSide = origSide;
                                bestDestSide = destSide;
                                bestOrigPos = origPos;
                                bestDestPos = destPos;
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
    
    /**
     * Calcule la longueur totale d'un chemin (somme des distances entre points consécutifs)
     */
    private double calculatePathLength(List<Point> path) {
        if (path.size() < 2) return 0;
        
        double totalLength = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            totalLength += Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        }
        
        return totalLength;
    }
    
    /**
     * Calcule la position optimale sur un côté de bloc pour s'aligner vers un autre bloc
     */
    private double calculateOptimalPosition(BlocClasse fromBloc, int side, BlocClasse toBloc) {
        int fromX = fromBloc.getX();
        int fromY = fromBloc.getY();
        int fromW = fromBloc.getLargeur();
        int fromH = fromBloc.getHauteurCalculee();
        
        int toX = toBloc.getX() + toBloc.getLargeur() / 2;
        int toY = toBloc.getY() + toBloc.getHauteurCalculee() / 2;
        
        double pos = 0.5; // Par défaut au milieu
        
        switch(side) {
            case 0: // DROITE
            case 2: // GAUCHE
                // Aligner verticalement avec le centre du bloc destination
                int relY = toY - fromY;
                pos = Math.max(0.2, Math.min(0.8, (double)relY / fromH));
                break;
            case 1: // BAS
            case 3: // HAUT
                // Aligner horizontalement avec le centre du bloc destination
                int relX = toX - fromX;
                pos = Math.max(0.2, Math.min(0.8, (double)relX / fromW));
                break;
        }
        
        return pos;
    }
    
    /**
     * Vérifie si un chemin a des collisions avec les blocs (autres que origine et destination)
     */
    private boolean pathHasCollisions(List<Point> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            
            // Vérifier chaque segment
            if (p1.x == p2.x) { // Segment vertical
                if (hasVerticalObstacle(p1.x, Math.min(p1.y, p2.y), Math.max(p1.y, p2.y))) {
                    return true;
                }
            } else if (p1.y == p2.y) { // Segment horizontal
                if (hasHorizontalObstacle(Math.min(p1.x, p2.x), Math.max(p1.x, p2.x), p1.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calcule la priorité de centrage d'une position (plus petit = mieux centré)
     * Niveau 0: 0.5 (milieu exact)
     * Niveau 1: 0.25, 0.75 (milieu du milieu)
     * Niveau 2: 0.125, 0.375, 0.625, 0.875 (subdivision encore)
     * Niveau 3+: toutes les autres positions
     */
    private int calculateCenterPriority(double pos) {
        // Tolérance de 0.01 pour la comparaison flottante
        final double TOLERANCE = 0.01;
        
        // Niveau 0: milieu exact (0.5)
        if (Math.abs(pos - 0.5) < TOLERANCE) return 0;
        
        // Niveau 1: 0.25 et 0.75
        if (Math.abs(pos - 0.25) < TOLERANCE || Math.abs(pos - 0.75) < TOLERANCE) return 1;
        
        // Niveau 2: 0.125, 0.375, 0.625, 0.875
        if (Math.abs(pos - 0.125) < TOLERANCE || Math.abs(pos - 0.375) < TOLERANCE ||
            Math.abs(pos - 0.625) < TOLERANCE || Math.abs(pos - 0.875) < TOLERANCE) return 2;
        
        // Niveau 3: toutes les autres positions
        return 1000; // Très basse priorité
    }

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

        // AUTORISER TOUTE LA SURFACE du bloc (0.0 à 1.0)
        posRel = Math.max(0.0, Math.min(1.0, posRel));

        switch(side) 
        {
            case 0: // DROITE - point exactement sur le bord droit
                return new Point(x + w, y + (int)(h * posRel));
            case 1: // BAS - point exactement sur le bord bas
                return new Point(x + (int)(w * posRel), y + h);
            case 2: // GAUCHE - point exactement sur le bord gauche
                return new Point(x, y + (int)(h * posRel));
            case 3: // HAUT - point exactement sur le bord haut
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
    * Optimisé pour les chemins directs (1 segment) quand possible
    * @param start Point de départ
    * @param end Point d'arrivée
    * @param startSide Coté de départ du bloc (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT)
    * @param endSide Coté d'arrivée (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT) 
    * @return Une {@link List} de {@link Point}s, qui représente le chemin.
    */
    private List<Point> createOrthogonalPath(Point start, Point end, int startSide, int endSide) {
        List<Point> path = new ArrayList<>();
        path.add(start);
        
        // PRIORITÉ 1 : Chemin direct (1 segment) si aligné horizontalement ou verticalement
        if (start.x == end.x) {
            // Alignement vertical parfait
            if (!hasVerticalObstacleStrict(start.x, start.y, end.y)) {
                path.add(end);
                return path;
            }
        } else if (start.y == end.y) {
            // Alignement horizontal parfait
            if (!hasHorizontalObstacleStrict(start.x, end.x, start.y)) {
                path.add(end);
                return path;
            }
        }

        int padding = 20; // Distance minimale de sortie du bloc
        
        // Calculer les points de sortie et d'entrée
        Point exitPoint = calculateExitPoint(start, startSide, padding);
        Point entryPoint = calculateEntryPoint(end, endSide, padding);
        
        // Ajouter le point de sortie
        path.add(exitPoint);
        
        // Créer le chemin entre sortie et entrée
        createMiddlePath(path, exitPoint, entryPoint, startSide, endSide);
        
        // Ajouter le point d'entrée s'il est différent du dernier point
        Point lastPoint = path.get(path.size() - 1);
        if (!lastPoint.equals(entryPoint)) {
            path.add(entryPoint);
        }
        
        // Ajouter le point final
        path.add(end);
        
        // Nettoyer les points redondants
        path = cleanRedundantPoints(path);
        
        return path;
    }
    
    /**
     * Calcule le point de sortie d'un bloc selon le côté
     */
    private Point calculateExitPoint(Point start, int side, int padding) {
        switch(side) {
            case 0: return new Point(start.x + padding, start.y); // DROITE
            case 1: return new Point(start.x, start.y + padding); // BAS
            case 2: return new Point(start.x - padding, start.y); // GAUCHE
            case 3: return new Point(start.x, start.y - padding); // HAUT
            default: return start;
        }
    }
    
    /**
     * Calcule le point d'entrée vers un bloc selon le côté
     */
    private Point calculateEntryPoint(Point end, int side, int padding) {
        switch(side) {
            case 0: return new Point(end.x + padding, end.y); // DROITE
            case 1: return new Point(end.x, end.y + padding); // BAS
            case 2: return new Point(end.x - padding, end.y); // GAUCHE
            case 3: return new Point(end.x, end.y - padding); // HAUT
            default: return end;
        }
    }
    
    /**
     * Crée le chemin intermédiaire entre le point de sortie et le point d'entrée
     * VÉRIFIE TOUJOURS si un chemin direct est possible avant de créer un accordéon
     */
    private void createMiddlePath(List<Point> path, Point exit, Point entry, int startSide, int endSide) {
        // PRIORITÉ ABSOLUE : Vérifier si on peut aller directement de exit à entry
        if (exit.x == entry.x && !hasVerticalObstacleStrict(exit.x, exit.y, entry.y)) {
            // Alignement vertical sans obstacle - LIGNE DROITE !
            return; // On ne fait rien, le chemin sera exit -> entry directement
        }
        if (exit.y == entry.y && !hasHorizontalObstacleStrict(exit.x, entry.x, exit.y)) {
            // Alignement horizontal sans obstacle - LIGNE DROITE !
            return; // On ne fait rien, le chemin sera exit -> entry directement
        }
        
        // Déterminer si on sort horizontalement ou verticalement
        boolean exitHorizontal = (startSide == 0 || startSide == 2);
        boolean entryHorizontal = (endSide == 0 || endSide == 2);
        
        if (exitHorizontal && entryHorizontal) {
            // Sortie et entrée horizontales : vérifier d'abord si une ligne horizontale directe est possible
            if (exit.y == entry.y && !hasHorizontalObstacleStrict(exit.x, entry.x, exit.y)) {
                return; // Ligne droite horizontale possible !
            }
            
            // Sinon, passage vertical au milieu
            int midY = (exit.y + entry.y) / 2;
            
            // Vérifier les obstacles
            if (!getObstaclesOnVerticalLine(exit.x, exit.y, midY).isEmpty() ||
                !getObstaclesOnVerticalLine(entry.x, midY, entry.y).isEmpty() ||
                !getObstaclesOnHorizontalLine(exit.x, entry.x, midY).isEmpty()) {
                midY = findClearHorizontalLine(exit.y, entry.y);
            }
            
            path.add(new Point(exit.x, midY));
            path.add(new Point(entry.x, midY));
            
        } else if (!exitHorizontal && !entryHorizontal) {
            // Sortie et entrée verticales : vérifier d'abord si une ligne verticale directe est possible
            if (exit.x == entry.x && !hasVerticalObstacleStrict(exit.x, exit.y, entry.y)) {
                return; // Ligne droite verticale possible !
            }
            
            // Sinon, passage horizontal au milieu
            int midX = (exit.x + entry.x) / 2;
            
            // Vérifier les obstacles
            if (!getObstaclesOnHorizontalLine(exit.x, midX, exit.y).isEmpty() ||
                !getObstaclesOnHorizontalLine(midX, entry.x, entry.y).isEmpty() ||
                !getObstaclesOnVerticalLine(midX, exit.y, entry.y).isEmpty()) {
                midX = findClearVerticalLine(exit.x, entry.x);
            }
            
            path.add(new Point(midX, exit.y));
            path.add(new Point(midX, entry.y));
            
        } else {
            // Sortie et entrée perpendiculaires : chemin en L
            if (exitHorizontal) {
                // Sortie horizontale, entrée verticale - vérifier si le L est libre
                Point corner = new Point(entry.x, exit.y);
                if (!hasHorizontalObstacleStrict(exit.x, corner.x, exit.y) &&
                    !hasVerticalObstacleStrict(corner.x, corner.y, entry.y)) {
                    path.add(corner);
                } else {
                    // Essayer l'autre coin
                    corner = new Point(exit.x, entry.y);
                    if (!hasVerticalObstacleStrict(exit.x, exit.y, corner.y) &&
                        !hasHorizontalObstacleStrict(corner.x, entry.x, entry.y)) {
                        path.add(corner);
                    } else {
                        // Fallback : premier coin quand même
                        path.add(new Point(entry.x, exit.y));
                    }
                }
            } else {
                // Sortie verticale, entrée horizontale - vérifier si le L est libre
                Point corner = new Point(exit.x, entry.y);
                if (!hasVerticalObstacleStrict(exit.x, exit.y, corner.y) &&
                    !hasHorizontalObstacleStrict(corner.x, entry.x, entry.y)) {
                    path.add(corner);
                } else {
                    // Essayer l'autre coin
                    corner = new Point(entry.x, exit.y);
                    if (!hasHorizontalObstacleStrict(exit.x, corner.x, exit.y) &&
                        !hasVerticalObstacleStrict(corner.x, corner.y, entry.y)) {
                        path.add(corner);
                    } else {
                        // Fallback : premier coin quand même
                        path.add(new Point(exit.x, entry.y));
                    }
                }
            }
        }
    }
    
    /**
     * Trouve une ligne horizontale claire entre deux Y
     */
    private int findClearHorizontalLine(int y1, int y2) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        
        // Essayer au milieu d'abord
        int midY = (minY + maxY) / 2;
        if (getObstaclesOnHorizontalLine(Integer.MIN_VALUE, Integer.MAX_VALUE, midY).isEmpty()) {
            return midY;
        }
        
        // Essayer au-dessus
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc != blocOrigine && bloc != blocDestination) {
                int testY = bloc.getY() - 30;
                if (testY > minY && testY < maxY) {
                    return testY;
                }
            }
        }
        
        // Par défaut
        return minY - 30;
    }
    
    /**
     * Trouve une ligne verticale claire entre deux X
     */
    private int findClearVerticalLine(int x1, int x2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        
        // Essayer au milieu d'abord
        int midX = (minX + maxX) / 2;
        if (getObstaclesOnVerticalLine(midX, Integer.MIN_VALUE, Integer.MAX_VALUE).isEmpty()) {
            return midX;
        }
        
        // Essayer à gauche
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc != blocOrigine && bloc != blocDestination) {
                int testX = bloc.getX() - 30;
                if (testX > minX && testX < maxX) {
                    return testX;
                }
            }
        }
        
        // Par défaut
        return minX - 30;
    }
    
    /**
     * Nettoie les points redondants (3 points alignés → 2 points)
     */
    private List<Point> cleanRedundantPoints(List<Point> path) {
        if (path.size() < 3) return path;
        
        List<Point> cleaned = new ArrayList<>();
        cleaned.add(path.get(0));
        
        for (int i = 1; i < path.size() - 1; i++) {
            Point prev = path.get(i - 1);
            Point curr = path.get(i);
            Point next = path.get(i + 1);
            
            // Garder le point seulement s'il change de direction
            boolean horizontal = (prev.y == curr.y && curr.y == next.y);
            boolean vertical = (prev.x == curr.x && curr.x == next.x);
            
            if (!horizontal && !vertical) {
                cleaned.add(curr);
            }
        }
        
        cleaned.add(path.get(path.size() - 1));
        return cleaned;
    }
    
    /**
     * Détecte si un segment horizontal est libre d'obstacles
     */
    private boolean hasHorizontalObstacle(int x1, int x2, int y) {
        return !getObstaclesOnHorizontalLine(x1, x2, y).isEmpty();
    }
    
    /**
     * Détecte si un segment vertical est libre d'obstacles
     */
    private boolean hasVerticalObstacle(int x, int y1, int y2) {
        return !getObstaclesOnVerticalLine(x, y1, y2).isEmpty();
    }
    
    /**
     * Détection STRICTE : vérifie qu'une ligne horizontale ne traverse AUCUN bloc
     * (utilisé pour les chemins directs) - EXCLUT origine et destination
     */
    private boolean hasHorizontalObstacleStrict(int x1, int x2, int y) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        
        for (BlocClasse bloc : tousLesBlocs) {
            // EXCLURE les blocs origine et destination
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // Vérifier que la ligne ne traverse PAS le rectangle du bloc (Y COMPRIS les bords !)
            // La ligne y doit passer à travers le bloc : by <= y <= by+bh
            // ET le segment [minX, maxX] doit croiser [bx, bx+bw]
            if (y >= by && y <= by + bh) {
                // La ligne traverse la hauteur du bloc
                if (maxX >= bx && minX <= bx + bw) {
                    // Le segment horizontal croise le bloc
                    return true; // OBSTACLE !
                }
            }
        }
        
        return false; // Pas d'obstacle
    }
    
    /**
     * Détection STRICTE : vérifie qu'une ligne verticale ne traverse AUCUN bloc
     * (utilisé pour les chemins directs) - EXCLUT origine et destination
     */
    private boolean hasVerticalObstacleStrict(int x, int y1, int y2) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        
        for (BlocClasse bloc : tousLesBlocs) {
            // EXCLURE les blocs origine et destination
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // Vérifier que la ligne ne traverse PAS le rectangle du bloc (Y COMPRIS les bords !)
            // La ligne x doit passer à travers le bloc : bx <= x <= bx+bw
            // ET le segment [minY, maxY] doit croiser [by, by+bh]
            if (x >= bx && x <= bx + bw) {
                // La ligne traverse la largeur du bloc
                if (maxY >= by && minY <= by + bh) {
                    // Le segment vertical croise le bloc
                    return true; // OBSTACLE !
                }
            }
        }
        
        return false; // Pas d'obstacle
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
        int margin = 10; // Marge pour éviter de coller aux blocs
        
        for (BlocClasse bloc : tousLesBlocs) {
            // EXCLURE les blocs origine et destination
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // La ligne horizontale traverse le bloc si:
            // 1. La ligne y est entre by et by+bh (avec marge)
            // 2. Le segment horizontal [minX, maxX] croise le bloc [bx, bx+bw] (avec marge)
            boolean yTraverseBloc = (y >= by - margin && y <= by + bh + margin);
            boolean xCroiseBloc = !(maxX < bx - margin || minX > bx + bw + margin);
            
            if (yTraverseBloc && xCroiseBloc) {
                obstacles.add(bloc);
            }
        }
        
        return obstacles;
    }
    
    /**
    * Renvoie la liste des blocs qui coupent une ligne verticale
    * @param x Abcisse de la ligne
    * @param y1 Ordonnée du début de la ligne
    * @param y2 Ordonnée de la fin de la ligne
    * @return Une {@link List} de {@link BlocClasse}s, qui représente les blocs qui sont sur la ligne donnée.
    */
    private List<BlocClasse> getObstaclesOnVerticalLine(int x, int y1, int y2) 
    {
        List<BlocClasse> obstacles = new ArrayList<>();
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int margin = 10; // Marge pour éviter de coller aux blocs
        
        for (BlocClasse bloc : tousLesBlocs) {
            // EXCLURE les blocs origine et destination
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            // La ligne verticale traverse le bloc si:
            // 1. La ligne x est entre bx et bx+bw (avec marge)
            // 2. Le segment vertical [minY, maxY] croise le bloc [by, by+bh] (avec marge)
            boolean xTraverseBloc = (x >= bx - margin && x <= bx + bw + margin);
            boolean yCroiseBloc = !(maxY < by - margin || minY > by + bh + margin);
            
            if (xTraverseBloc && yCroiseBloc) {
                obstacles.add(bloc);
            }
        }
        
        return obstacles;
    }
    
    /**
    * Calcule une position Y pour contourner les obstacles horizontaux
    * Choisit le meilleur chemin en fonction de l'espace disponible et de la distance
    */
    private int getDeflectionY(int originalY, List<BlocClasse> obstacles) {
        if (obstacles.isEmpty()) return originalY;
        
        int topMin = Integer.MAX_VALUE;
        int bottomMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            topMin = Math.min(topMin, bloc.getY());
            bottomMax = Math.max(bottomMax, bloc.getY() + bloc.getHauteurCalculee());
        }
        
        int margin = 30; // Marge augmentée pour plus d'espace
        
        // Calculer la distance au lieu de l'espace absolu
        int distToTop = Math.abs(originalY - topMin);
        int distToBottom = Math.abs(originalY - bottomMax);
        
        // Préférer le chemin le plus court
        if (distToTop < distToBottom) {
            return topMin - margin; // Passer au-dessus
        } else {
            return bottomMax + margin; // Passer en-dessous
        }
    }
    
    /**
     * Calcule une position X pour contourner les obstacles verticaux
     * Choisit le meilleur chemin en fonction de l'espace disponible et de la distance
     */
    private int getDeflectionX(int originalX, List<BlocClasse> obstacles) {
        if (obstacles.isEmpty()) return originalX;
        
        int leftMin = Integer.MAX_VALUE;
        int rightMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            leftMin = Math.min(leftMin, bloc.getX());
            rightMax = Math.max(rightMax, bloc.getX() + bloc.getLargeur());
        }
        
        int margin = 30; // Marge augmentée pour plus d'espace
        
        // Calculer la distance au lieu de l'espace absolu
        int distToLeft = Math.abs(originalX - leftMin);
        int distToRight = Math.abs(originalX - rightMax);
        
        // Préférer le chemin le plus court
        if (distToLeft < distToRight) {
            return leftMin - margin; // Passer à gauche
        } else {
            return rightMax + margin; // Passer à droite
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

        // Trait plein simple pour association
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        
        // Dessiner chaque segment du chemin (tous strictement horizontaux ou verticaux)
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            
            // Vérification : chaque segment doit être soit horizontal (même Y) soit vertical (même X)
            // Pas de diagonales permises
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        if (this.type.equals("association") && unidirectionnel) {
            dessinerFlecheAssociation(g, ancrageDestination, sideDestination);
        }


        if (this.type.equals("heritage")) {
            dessinerFlecheHeritage(g, ancrageDestination, sideDestination);
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

        private void dessinerFlecheHeritage(Graphics2D g, Point anchor, int side) {
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
    
    /**
     * Ajuste la position relative pour éviter les superpositions avec d'autres liaisons
     * @param allLiaisons Liste de toutes les liaisons
     * @param index Index de cette liaison dans la liste
     */
    public void adjustPositionToAvoidOverlap(List<LiaisonVue> allLiaisons, int index) {
        // Compter combien de liaisons partagent le même côté d'origine
        List<Integer> sameOriginSide = new ArrayList<>();
        for (int i = 0; i < allLiaisons.size(); i++) {
            LiaisonVue other = allLiaisons.get(i);
            if (other.blocOrigine == this.blocOrigine && other.sideOrigine == this.sideOrigine) {
                sameOriginSide.add(i);
            }
        }
        
        // Distribuer uniformément les positions si plusieurs liaisons sur le même côté
        if (sameOriginSide.size() > 1) {
            int myPosition = sameOriginSide.indexOf(index);
            this.posRelOrigine = 0.2 + (0.6 * myPosition / (sameOriginSide.size() - 1));
        }
        
        // Faire de même pour la destination
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