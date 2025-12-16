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
    
    // Référence à toutes les liaisons pour éviter les ancrages en doublon
    private List<LiaisonVue>    toutesLesLiaisons = new ArrayList<>();


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
     * Définit la liste de toutes les liaisons du diagramme pour éviter les doublons d'ancrages
     */
    public void setToutesLesLiaisons(List<LiaisonVue> liaisons) {
        this.toutesLesLiaisons = liaisons;
    }

    /**
     * Choisit automatiquement les meilleurs côtés et positions pour MINIMISER LE NOMBRE DE SEGMENTS.
     * PRIORITÉ ABSOLUE : moins de points = mieux (1 segment >> 2 segments >> 3 segments, etc.)
     * En cas d'égalité sur le nombre de points : distance minimale, côtés naturels, puis centrage.
     */
    private void chooseBestSides() {
        int bestOrigSide = 0;
        int bestDestSide = 0;
        double bestOrigPos = 0.5;
        double bestDestPos = 0.5;
        int minSegments = Integer.MAX_VALUE;  // CRITÈRE PRINCIPAL : nombre de segments
        double minDistance = Double.MAX_VALUE;
        int bestCenterPriority = Integer.MAX_VALUE;
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
                        // VÉRIFIER si cet ancrage est déjà utilisé par une autre liaison
                        if (isAnchorOccupied(blocOrigine, origSide, pos) || 
                            isAnchorOccupied(blocDestination, destSide, pos)) {
                            continue; // Ancrage déjà pris, passer au suivant
                        }
                        
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
                            
                            // CRITÈRES DE COMPARAISON (par ordre de priorité):
                            // 1. Nombre de segments (1 segment = 2 points)
                            // 2. Côtés naturels et centrage (totalPriority)
                            // 3. Distance minimale
                            boolean isBetter = false;
                            int numSegments = 1; // Chemin direct = 1 segment
                            
                            if (!foundDirect) {
                                isBetter = true;
                            } else if (numSegments < minSegments) {
                                // Moins de segments : toujours mieux !
                                isBetter = true;
                            } else if (numSegments == minSegments) {
                                // Même nombre de segments : comparer priorité puis distance
                                if (totalPriority < bestCenterPriority) {
                                    isBetter = true;
                                } else if (totalPriority == bestCenterPriority && directDist < minDistance) {
                                    isBetter = true;
                                }
                            }
                            
                            if (isBetter) {
                                minSegments = numSegments;
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
                            // VÉRIFIER si ces ancrages sont déjà utilisés par une autre liaison
                            if (isAnchorOccupied(blocOrigine, origSide, origPos) || 
                                isAnchorOccupied(blocDestination, destSide, destPos)) {
                                continue; // Au moins un ancrage déjà pris, passer au suivant
                            }
                            
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
                                
                                // CRITÈRES DE COMPARAISON (par ordre de priorité):
                                // 1. Nombre de segments (1 segment = 2 points)
                                // 2. Côtés naturels et centrage (totalPriority)
                                // 3. Distance minimale
                                boolean isBetter = false;
                                int numSegments = 1; // Chemin direct = 1 segment
                                
                                if (!foundDirect) {
                                    isBetter = true;
                                } else if (numSegments < minSegments) {
                                    // Moins de segments : toujours mieux !
                                    isBetter = true;
                                } else if (numSegments == minSegments) {
                                    // Même nombre de segments : comparer priorité puis distance
                                    if (totalPriority < bestCenterPriority) {
                                        isBetter = true;
                                    } else if (totalPriority == bestCenterPriority && directDist < minDistance) {
                                        isBetter = true;
                                    }
                                }
                                
                                if (isBetter) {
                                    minSegments = numSegments;
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
                        // VÉRIFIER si ces ancrages sont déjà utilisés par une autre liaison
                        if (isAnchorOccupied(blocOrigine, origSide, origPos) || 
                            isAnchorOccupied(blocDestination, destSide, destPos)) {
                            continue; // Au moins un ancrage déjà pris, passer au suivant
                        }
                        
                        Point start = getPointOnSide(blocOrigine, origSide, origPos);
                        Point end = getPointOnSide(blocDestination, destSide, destPos);
                        
                        List<Point> testPath = createOrthogonalPath(start, end, origSide, destSide);
                        
                        if (!pathHasCollisions(testPath)) {
                            int numSegments = testPath.size() - 1; // Nombre de segments
                            double distance = calculatePathLength(testPath);
                            
                            // Bonus pour les paires naturelles (réduction de priorité)
                            int naturalBonus = isNaturalPair ? -1000 : 0;
                            
                            // CRITÈRES DE COMPARAISON (par ordre de priorité):
                            // 1. Nombre de segments (moins = mieux)
                            // 2. Côtés naturels
                            // 3. Distance minimale
                            boolean isBetter = false;
                            
                            if (numSegments < minSegments) {
                                // Moins de segments : TOUJOURS mieux !
                                isBetter = true;
                            } else if (numSegments == minSegments) {
                                // Même nombre de segments : comparer naturel puis distance
                                if (isNaturalPair && bestCenterPriority >= 0) {
                                    // Paire naturelle vs non-naturelle : prendre naturelle
                                    isBetter = true;
                                } else if ((isNaturalPair && bestCenterPriority < 0) || (!isNaturalPair && bestCenterPriority >= 0)) {
                                    // Même statut naturel : comparer distance
                                    isBetter = (distance < minDistance);
                                } else {
                                    // Non-naturelle vs naturelle : ne pas prendre
                                    isBetter = false;
                                }
                            }
                            
                            if (isBetter) {
                                minSegments = numSegments;
                                minDistance = distance;
                                bestCenterPriority = naturalBonus;
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
     * Vérifie si un point d'ancrage est déjà utilisé par une autre liaison
     * @param bloc Le bloc sur lequel vérifier
     * @param side Le côté du bloc (0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT)
     * @param pos La position relative (0.0 à 1.0)
     * @return true si l'ancrage est déjà occupé
     */
    private boolean isAnchorOccupied(BlocClasse bloc, int side, double pos) {
        final double TOLERANCE = 0.02; // Tolérance de 2% pour considérer qu'un ancrage est occupé
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue; // Ignorer soi-même
            
            // Vérifier l'origine de l'autre liaison
            if (autre.blocOrigine == bloc && autre.sideOrigine == side) {
                if (Math.abs(autre.posRelOrigine - pos) < TOLERANCE) {
                    return true; // Ancrage déjà utilisé !
                }
            }
            
            // Vérifier la destination de l'autre liaison
            if (autre.blocDestination == bloc && autre.sideDestination == side) {
                if (Math.abs(autre.posRelDestination - pos) < TOLERANCE) {
                    return true; // Ancrage déjà utilisé !
                }
            }
        }
        
        return false; // Ancrage libre
    }

    /**
     * Détecte si deux segments orthogonaux se croisent et retourne le point d'intersection
     * @return Le point d'intersection ou null si pas d'intersection
     */
    private Point getSegmentIntersection(Point a1, Point a2, Point b1, Point b2) {
        // Vérifier si les segments sont orthogonaux (l'un horizontal, l'autre vertical)
        boolean a_horizontal = (a1.y == a2.y);
        boolean a_vertical = (a1.x == a2.x);
        boolean b_horizontal = (b1.y == b2.y);
        boolean b_vertical = (b1.x == b2.x);
        
        // Les deux segments doivent être perpendiculaires
        if (!(a_horizontal && b_vertical) && !(a_vertical && b_horizontal)) {
            return null;
        }
        
        // Déterminer quel segment est horizontal et lequel est vertical
        Point h1, h2, v1, v2;
        if (a_horizontal) {
            h1 = a1; h2 = a2;
            v1 = b1; v2 = b2;
        } else {
            h1 = b1; h2 = b2;
            v1 = a1; v2 = a2;
        }
        
        // Point d'intersection potentiel
        int ix = v1.x;  // x du segment vertical
        int iy = h1.y;  // y du segment horizontal
        
        // Bornes des segments
        int minHx = Math.min(h1.x, h2.x);
        int maxHx = Math.max(h1.x, h2.x);
        int minVy = Math.min(v1.y, v2.y);
        int maxVy = Math.max(v1.y, v2.y);
        
        // Le point doit être à l'intérieur des deux segments (pas strictement aux extrémités)
        // Utiliser une tolérance pour éviter les problèmes de pixels
        if (ix >= minHx + 1 && ix <= maxHx - 1 && iy >= minVy + 1 && iy <= maxVy - 1) {
            return new Point(ix, iy);
        }
        
        return null;
    }

    /**
     * Calcule un décalage pour éviter que deux liaisons se superposent complètement
     * @param myPath Le chemin de cette liaison
     * @return Le décalage en pixels à appliquer perpendiculairement aux segments
     */
    private int calculatePathOffset(List<Point> myPath) {
        if (toutesLesLiaisons == null || toutesLesLiaisons.isEmpty()) return 0;
        
        final int OFFSET_DISTANCE = 4; // Distance de décalage en pixels
        
        // Trouver toutes les liaisons qui partagent des segments avec cette liaison
        List<LiaisonVue> overlappingLiaisons = new ArrayList<>();
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            
            // Vérifier si l'autre liaison a des segments qui se chevauchent avec cette liaison
            Point autreOrigine = getPointOnSide(autre.blocOrigine, autre.sideOrigine, autre.posRelOrigine);
            Point autreDestination = getPointOnSide(autre.blocDestination, autre.sideDestination, autre.posRelDestination);
            List<Point> autrePath = createOrthogonalPath(autreOrigine, autreDestination, autre.sideOrigine, autre.sideDestination);
            
            if (pathsShareSegments(myPath, autrePath)) {
                overlappingLiaisons.add(autre);
            }
        }
        
        if (overlappingLiaisons.isEmpty()) return 0;
        
        // Trouver ma position dans la liste globale parmi les liaisons qui se chevauchent
        int myGlobalIndex = toutesLesLiaisons.indexOf(this);
        int myOffsetIndex = 0;
        
        for (LiaisonVue autre : overlappingLiaisons) {
            int autreGlobalIndex = toutesLesLiaisons.indexOf(autre);
            if (autreGlobalIndex < myGlobalIndex) {
                myOffsetIndex++;
            }
        }
        
        // Décalage alterné : 0, +OFFSET, -OFFSET, +2*OFFSET, -2*OFFSET, ...
        if (myOffsetIndex == 0) return 0;
        if (myOffsetIndex % 2 == 1) {
            return ((myOffsetIndex + 1) / 2) * OFFSET_DISTANCE;
        } else {
            return -(myOffsetIndex / 2) * OFFSET_DISTANCE;
        }
    }
    
    /**
     * Vérifie si deux chemins partagent des segments communs (superposition)
     * @param path1 Premier chemin
     * @param path2 Deuxième chemin
     * @return true si les chemins partagent au moins un segment
     */
    private boolean pathsShareSegments(List<Point> path1, List<Point> path2) {
        // Pour chaque segment du premier chemin
        for (int i = 0; i < path1.size() - 1; i++) {
            Point p1a = path1.get(i);
            Point p1b = path1.get(i + 1);
            
            // Comparer avec chaque segment du deuxième chemin
            for (int j = 0; j < path2.size() - 1; j++) {
                Point p2a = path2.get(j);
                Point p2b = path2.get(j + 1);
                
                // Vérifier si les deux segments sont colinéaires et se chevauchent
                if (segmentsOverlap(p1a, p1b, p2a, p2b)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Vérifie si deux segments sont colinéaires et se chevauchent
     * @return true s'ils se chevauchent
     */
    private boolean segmentsOverlap(Point a1, Point a2, Point b1, Point b2) {
        // Vérifier si les segments sont tous deux horizontaux
        if (a1.y == a2.y && b1.y == b2.y && a1.y == b1.y) {
            int minA = Math.min(a1.x, a2.x);
            int maxA = Math.max(a1.x, a2.x);
            int minB = Math.min(b1.x, b2.x);
            int maxB = Math.max(b1.x, b2.x);
            // Chevauchement si les intervalles se croisent
            return maxA >= minB && maxB >= minA;
        }
        
        // Vérifier si les segments sont tous deux verticaux
        if (a1.x == a2.x && b1.x == b2.x && a1.x == b1.x) {
            int minA = Math.min(a1.y, a2.y);
            int maxA = Math.max(a1.y, a2.y);
            int minB = Math.min(b1.y, b2.y);
            int maxB = Math.max(b1.y, b2.y);
            // Chevauchement si les intervalles se croisent
            return maxA >= minB && maxB >= minA;
        }
        
        return false;
    }
    
    /**
     * Trouve tous les points d'intersection entre cette liaison et les autres
     * Utilise les chemins AVEC décalage pour les liaisons superposées
     * @param myPath Le chemin de cette liaison (déjà décalé si nécessaire)
     * @return Liste des points d'intersection
     */
    private List<Point> findIntersections(List<Point> myPath) {
        List<Point> intersections = new ArrayList<>();
        
        if (toutesLesLiaisons == null) return intersections;
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue; // Ignorer soi-même
            
            // Obtenir le chemin ORIGINAL de l'autre liaison
            Point autreOrigine = getPointOnSide(autre.blocOrigine, autre.sideOrigine, autre.posRelOrigine);
            Point autreDestination = getPointOnSide(autre.blocDestination, autre.sideDestination, autre.posRelDestination);
            List<Point> autreOriginalPath = createOrthogonalPath(autreOrigine, autreDestination, autre.sideOrigine, autre.sideDestination);
            
            // Appliquer le décalage de l'autre liaison si nécessaire
            int autreOffset = autre.calculatePathOffset(autreOriginalPath);
            List<Point> autrePath = autreOffset != 0 ? autre.applyOffsetToPath(autreOriginalPath, autreOffset) : autreOriginalPath;
            
            // Tester chaque paire de segments
            for (int i = 0; i < myPath.size() - 1; i++) {
                for (int j = 0; j < autrePath.size() - 1; j++) {
                    Point intersection = getSegmentIntersection(
                        myPath.get(i), myPath.get(i + 1),
                        autrePath.get(j), autrePath.get(j + 1)
                    );
                    
                    if (intersection != null) {
                        // Éviter les doublons
                        boolean exists = false;
                        for (Point p : intersections) {
                            if (Math.abs(p.x - intersection.x) < 3 && Math.abs(p.y - intersection.y) < 3) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            intersections.add(intersection);
                        }
                    }
                }
            }
        }
        
        return intersections;
    }
    
    /**
     * Applique un décalage perpendiculaire à tous les segments du chemin
     * @param path Le chemin original
     * @param offset Le décalage en pixels (positif ou négatif)
     * @return Le chemin décalé
     */
    private List<Point> applyOffsetToPath(List<Point> path, int offset) {
        List<Point> offsetPath = new ArrayList<>();
        
        for (int i = 0; i < path.size(); i++) {
            Point p = path.get(i);
            
            if (i == 0) {
                // Premier point : décaler selon le premier segment
                Point next = path.get(i + 1);
                if (next.x == p.x) {
                    // Segment vertical : décaler horizontalement
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else {
                    // Segment horizontal : décaler verticalement
                    offsetPath.add(new Point(p.x, p.y + offset));
                }
            } else if (i == path.size() - 1) {
                // Dernier point : décaler selon le dernier segment
                Point prev = path.get(i - 1);
                if (prev.x == p.x) {
                    // Segment vertical : décaler horizontalement
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else {
                    // Segment horizontal : décaler verticalement
                    offsetPath.add(new Point(p.x, p.y + offset));
                }
            } else {
                // Point intermédiaire : déterminer le décalage selon les segments adjacents
                Point prev = path.get(i - 1);
                Point next = path.get(i + 1);
                
                // Si les deux segments sont verticaux ou les deux horizontaux
                boolean prevVertical = (prev.x == p.x);
                boolean nextVertical = (next.x == p.x);
                
                if (prevVertical && nextVertical) {
                    // Les deux segments sont verticaux : décaler horizontalement
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else if (!prevVertical && !nextVertical) {
                    // Les deux segments sont horizontaux : décaler verticalement
                    offsetPath.add(new Point(p.x, p.y + offset));
                } else {
                    // Changement de direction : décaler dans les deux directions pour créer un coin
                    offsetPath.add(new Point(p.x + offset, p.y + offset));
                }
            }
        }
        
        return offsetPath;
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
        
        int padding = 15; // Distance minimale de sortie/entrée du bloc pour un rendu propre
        
        // Calculer les points de sortie et d'entrée (TOUJOURS présents pour un rendu propre)
        Point exitPoint = calculateExitPoint(start, startSide, padding);
        Point entryPoint = calculateEntryPoint(end, endSide, padding);
        
        // Ajouter le point de sortie
        path.add(exitPoint);
        
        // Déterminer si on sort horizontalement ou verticalement
        boolean exitHorizontal = (startSide == 0 || startSide == 2);
        boolean entryHorizontal = (endSide == 0 || endSide == 2);
        
        // Créer le chemin optimal entre exitPoint et entryPoint
        // IMPORTANT: Tous les segments doivent être horizontaux ou verticaux (pas de diagonales!)
        
        if (exitHorizontal && entryHorizontal) {
            // Les deux horizontaux
            if (exitPoint.y == entryPoint.y) {
                // Même Y -> ligne droite horizontale
            } else {
                // Y différents -> besoin de 2 coins
                int cornerX;
                if (startSide == 2 && endSide == 2) {
                    cornerX = Math.min(exitPoint.x, entryPoint.x);
                } else if (startSide == 0 && endSide == 0) {
                    cornerX = Math.max(exitPoint.x, entryPoint.x);
                } else {
                    cornerX = (exitPoint.x + entryPoint.x) / 2;
                }
                path.add(new Point(cornerX, exitPoint.y));
                path.add(new Point(cornerX, entryPoint.y));
            }
        } else if (!exitHorizontal && !entryHorizontal) {
            // Les deux verticaux
            if (exitPoint.x == entryPoint.x) {
                // Même X -> ligne droite verticale
            } else {
                // X différents -> besoin de 2 coins
                int cornerY;
                if (startSide == 3 && endSide == 3) {
                    cornerY = Math.min(exitPoint.y, entryPoint.y);
                } else if (startSide == 1 && endSide == 1) {
                    cornerY = Math.max(exitPoint.y, entryPoint.y);
                } else {
                    cornerY = (exitPoint.y + entryPoint.y) / 2;
                }
                path.add(new Point(exitPoint.x, cornerY));
                path.add(new Point(entryPoint.x, cornerY));
            }
        } else if (exitHorizontal && !entryHorizontal) {
            // Sortie horizontale, entrée verticale -> un coin
            path.add(new Point(entryPoint.x, exitPoint.y));
        } else {
            // Sortie verticale, entrée horizontale -> un coin
            path.add(new Point(exitPoint.x, entryPoint.y));
        }
        
        // Ajouter le point d'entrée
        path.add(entryPoint);
        
        // Ajouter le point final
        path.add(end);
        
        // Nettoyer les points redondants (3 points alignés -> 2 points)
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
     * Nettoie les points redondants (3 points alignés sur une même ligne → supprimer le milieu)
     * GARDE tous les coins (changements de direction)
     */
    private List<Point> cleanRedundantPoints(List<Point> path) {
        if (path.size() < 3) return path;
        
        List<Point> cleaned = new ArrayList<>();
        cleaned.add(path.get(0));
        
        for (int i = 1; i < path.size() - 1; i++) {
            Point prev = cleaned.get(cleaned.size() - 1); // Utiliser le dernier point nettoyé
            Point curr = path.get(i);
            Point next = path.get(i + 1);
            
            // Supprimer le point SEULEMENT si les 3 points sont sur la même ligne
            boolean allHorizontal = (prev.y == curr.y && curr.y == next.y);
            boolean allVertical = (prev.x == curr.x && curr.x == next.x);
            
            // Garder le point s'il n'est PAS sur la même ligne (c'est un coin)
            if (!allHorizontal && !allVertical) {
                cleaned.add(curr);
            }
        }
        
        cleaned.add(path.get(path.size() - 1));
        
        // Vérification de sécurité : s'assurer qu'il n'y a pas de diagonales
        for (int i = 0; i < cleaned.size() - 1; i++) {
            Point p1 = cleaned.get(i);
            Point p2 = cleaned.get(i + 1);
            if (p1.x != p2.x && p1.y != p2.y) {
                // DIAGONALE DÉTECTÉE ! Ajouter un coin
                // On insère un point intermédiaire
                cleaned.add(i + 1, new Point(p2.x, p1.y));
                // Recommencer la vérification
                i = -1;
            }
        }
        
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

        // Créer le chemin orthogonal ORIGINAL
        List<Point> originalPath = createOrthogonalPath(ancrageOrigine, ancrageDestination, sideOrigine, sideDestination);
        
        // Calculer un décalage si cette liaison se superpose à d'autres
        int offset = calculatePathOffset(originalPath);
        
        // Appliquer le décalage si nécessaire
        List<Point> path = offset != 0 ? applyOffsetToPath(originalPath, offset) : originalPath;
        
        // Trouver les intersections avec les autres liaisons (sur le chemin décalé)
        List<Point> intersections = findIntersections(path);

        // Définir le style de trait selon le type de liaison
        g.setColor(Color.BLACK);
        Stroke normalStroke;
        if (this.type.equals("interface")) {
            // Pointillés pour les interfaces
            float[] dashPattern = {5.0f, 5.0f};
            normalStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0);
        } else {
            // Trait plein pour association et héritage
            normalStroke = new BasicStroke(1);
        }
        g.setStroke(normalStroke);
        
        // Dessiner chaque segment du chemin avec des ponts aux intersections
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            
            // Vérifier si ce segment a des intersections
            List<Point> segmentIntersections = new ArrayList<>();
            boolean horizontalSegment = (p1.y == p2.y);
            boolean verticalSegment = (p1.x == p2.x);
            
            for (Point inter : intersections) {
                if (horizontalSegment) {
                    // Segment horizontal : vérifier que l'intersection est à l'intérieur
                    int minX = Math.min(p1.x, p2.x);
                    int maxX = Math.max(p1.x, p2.x);
                    if (inter.x > minX && inter.x < maxX && Math.abs(inter.y - p1.y) < 2) {
                        segmentIntersections.add(inter);
                    }
                } else if (verticalSegment) {
                    // Segment vertical : vérifier que l'intersection est à l'intérieur
                    int minY = Math.min(p1.y, p2.y);
                    int maxY = Math.max(p1.y, p2.y);
                    if (inter.y > minY && inter.y < maxY && Math.abs(inter.x - p1.x) < 2) {
                        segmentIntersections.add(inter);
                    }
                }
            }
            
            if (segmentIntersections.isEmpty()) {
                // Pas d'intersection : dessiner normalement
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            } else {
                // Il y a des intersections : dessiner avec des ponts (uniquement sur segments verticaux)
                drawLineWithBridges(g, p1, p2, segmentIntersections, normalStroke);
            }
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

    /**
     * Dessine une ligne avec des ponts (arcs) aux points d'intersection
     * IMPORTANT: Les ponts ne sont dessinés QUE sur les segments VERTICAUX
     * @param g Graphics2D
     * @param p1 Point de début
     * @param p2 Point de fin
     * @param intersections Liste des points d'intersection sur ce segment
     * @param normalStroke Le stroke normal à utiliser (peut être pointillé)
     */
    private void drawLineWithBridges(Graphics2D g, Point p1, Point p2, List<Point> intersections, Stroke normalStroke) {
        final int BRIDGE_SIZE = 8; // Taille du pont
        
        // Déterminer si le segment est horizontal ou vertical
        boolean horizontal = (p1.y == p2.y);
        
        // Si le segment est HORIZONTAL : dessiner normalement SANS pont
        if (horizontal) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            return;
        }
        
        // Le segment est VERTICAL : dessiner avec des ponts
        
        if (intersections.isEmpty()) {
            // Pas d'intersection : dessiner normalement
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            return;
        }
        
        // Trier les intersections selon l'ordre sur le segment (du haut vers le bas ou inverse)
        intersections.sort((a, b) -> {
            if (p1.y < p2.y) {
                return Integer.compare(a.y, b.y); // Tri croissant
            } else {
                return Integer.compare(b.y, a.y); // Tri décroissant
            }
        });
        
        // Dessiner le segment en morceaux avec des ponts
        Point current = p1;
        
        for (Point inter : intersections) {
            // Dessiner jusqu'AVANT le pont avec le stroke normal
            g.setStroke(normalStroke);
            if (current.y < inter.y) {
                g.drawLine(current.x, current.y, inter.x, inter.y - BRIDGE_SIZE / 2);
            } else {
                g.drawLine(current.x, current.y, inter.x, inter.y + BRIDGE_SIZE / 2);
            }
            
            // Dessiner le pont (petit arc) - TOUJOURS avec un trait PLEIN
            g.setStroke(new BasicStroke(1));
            g.drawArc(inter.x - BRIDGE_SIZE / 2, inter.y - BRIDGE_SIZE / 2, 
                     BRIDGE_SIZE, BRIDGE_SIZE, 90, 180);
            
            // Continuer APRÈS le pont
            if (current.y < inter.y) {
                current = new Point(inter.x, inter.y + BRIDGE_SIZE / 2);
            } else {
                current = new Point(inter.x, inter.y - BRIDGE_SIZE / 2);
            }
        }
        
        // Dessiner le dernier morceau jusqu'au point final
        g.setStroke(normalStroke);
        g.drawLine(current.x, current.y, p2.x, p2.y);
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