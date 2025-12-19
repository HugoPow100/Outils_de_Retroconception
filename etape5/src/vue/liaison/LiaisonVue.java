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
    private String type;
    private boolean unidirectionnel;
    private String multOrig;
    private String multDest;
    private BlocClasse blocOrigine;
    private BlocClasse blocDestination;
    private Point ancrageOrigine;
    private Point ancrageDestination;
    private double posRelOrigine;
    private double posRelDestination;
    private int sideOrigine;
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
     * Algorithme: calcule les chemins optimaux de toutes les liaisons avec même origine/destination,
     * puis choisit le meilleur chemin parmi tous et l'applique à toutes ces liaisons
     */
    private void chooseBestSides() {
        // Trouver toutes les liaisons ayant la même origine et destination (ou inversées)
        List<LiaisonVue> liaisonsGroupees = new ArrayList<>();
        liaisonsGroupees.add(this);
        
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            
            boolean memeDirection = (autre.blocOrigine == this.blocOrigine && autre.blocDestination == this.blocDestination);
            boolean directionInversee = (autre.blocOrigine == this.blocDestination && autre.blocDestination == this.blocOrigine);
            
            if (memeDirection || directionInversee) {
                liaisonsGroupees.add(autre);
            }
        }
        
        // Si plusieurs liaisons partagent la même origine/destination
        if (liaisonsGroupees.size() > 1) {
            // Calculer le meilleur chemin pour chaque liaison du groupe
            CheminCandidat meilleurChemin = null;
            
            for (LiaisonVue liaison : liaisonsGroupees) {
                CheminCandidat cheminLiaison = calculerMeilleurChemin(liaison.blocOrigine, liaison.blocDestination);
                
                if (cheminLiaison != null) {
                    if (meilleurChemin == null || estMeilleur(cheminLiaison, meilleurChemin)) {
                        meilleurChemin = cheminLiaison;
                    }
                }
            }
            
            // Appliquer le meilleur chemin à toutes les liaisons du groupe avec décalage pour éviter superposition
            if (meilleurChemin != null) {
                int index = 0;
                double offsetStep = 0.15; // Décalage de 15% par liaison
                
                for (LiaisonVue liaison : liaisonsGroupees) {
                    boolean inversee = (liaison.blocOrigine == this.blocDestination && liaison.blocDestination == this.blocOrigine);
                    
                    // Calculer le décalage pour éviter superposition
                    double origOffset = (index - liaisonsGroupees.size() / 2.0) * offsetStep;
                    double destOffset = (index - liaisonsGroupees.size() / 2.0) * offsetStep;
                    
                    if (inversee) {
                        liaison.sideOrigine = meilleurChemin.sideDestination;
                        liaison.sideDestination = meilleurChemin.sideOrigine;
                        liaison.posRelOrigine = Math.max(0.1, Math.min(0.9, meilleurChemin.posRelDestination + origOffset));
                        liaison.posRelDestination = Math.max(0.1, Math.min(0.9, meilleurChemin.posRelOrigine + destOffset));
                    } else {
                        liaison.sideOrigine = meilleurChemin.sideOrigine;
                        liaison.sideDestination = meilleurChemin.sideDestination;
                        liaison.posRelOrigine = Math.max(0.1, Math.min(0.9, meilleurChemin.posRelOrigine + origOffset));
                        liaison.posRelDestination = Math.max(0.1, Math.min(0.9, meilleurChemin.posRelDestination + destOffset));
                    }
                    
                    liaison.ancrageOrigine = GestionnaireAncrage.getPointOnSide(liaison.blocOrigine, liaison.sideOrigine, liaison.posRelOrigine);
                    liaison.ancrageDestination = GestionnaireAncrage.getPointOnSide(liaison.blocDestination, liaison.sideDestination, liaison.posRelDestination);
                    index++;
                }
                return;
            }
        }
        
        // Sinon, calculer le chemin optimal pour cette liaison seule
        int bestOrigSide = 0, bestDestSide = 0;
        double bestOrigPos = 0.5, bestDestPos = 0.5;
        int minBends = Integer.MAX_VALUE;
        double minDistance = Double.MAX_VALUE;
        
        final int ox = blocOrigine.getX() + (blocOrigine.getLargeur() >> 1);
        final int oy = blocOrigine.getY() + (blocOrigine.getHauteurCalculee() >> 1);
        final int dx = blocDestination.getX() + (blocDestination.getLargeur() >> 1);
        final int dy = blocDestination.getY() + (blocDestination.getHauteurCalculee() >> 1);
        
        final boolean destIsRight = dx > ox, destIsLeft = dx < ox;
        final boolean destIsBelow = dy > oy, destIsAbove = dy < oy;
        
        // Positions à tester (privilégier centre et coins)
        double[] positions = {0.5, 0.0, 1.0, 0.25, 0.75, 0.33, 0.67};
        
        // Tester TOUTES les combinaisons de côtés et positions
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                // Vérifier si c'est une paire naturelle
                boolean isNaturalPair = isNaturalSidePair(origSide, destSide, destIsRight, destIsLeft, destIsBelow, destIsAbove);
                
                for (double origPos : positions) {
                    for (double destPos : positions) {
                        // Vérifier si les ancrages sont déjà occupés
                        if (isAnchorOccupied(blocOrigine, origSide, origPos) || 
                            isAnchorOccupied(blocDestination, destSide, destPos)) continue;
                        
                        Point start = GestionnaireAncrage.getPointOnSide(blocOrigine, origSide, origPos);
                        Point end = GestionnaireAncrage.getPointOnSide(blocDestination, destSide, destPos);
                        
                        // Calculer le chemin orthogonal
                        List<Point> testPath = calculateurChemin.createOrthogonalPath(start, end, origSide, destSide);
                        
                        // Vérifier les collisions
                        if (!calculateurChemin.pathHasCollisions(testPath)) {
                            // Compter le nombre RÉEL de segments dans le chemin calculé
                            int actualSegments = testPath.size() - 1;
                            double distance = calculateurChemin.calculatePathLength(testPath);
                            
                            // Pénalité massive pour les accordéons
                            boolean hasAccordion = hasAccordion(testPath);
                            
                            // CRITÈRE PRINCIPAL: Moins de segments réels
                            // CRITÈRE SECONDAIRE: Pas d'accordéon
                            // CRITÈRE TERTIAIRE: Distance plus courte
                            boolean isBetter = false;
                            
                            if (hasAccordion) {
                                // Ne JAMAIS accepter un chemin avec accordéon sauf si c'est le seul
                                if (minBends == Integer.MAX_VALUE) {
                                    isBetter = true;
                                }
                            } else if (minBends == Integer.MAX_VALUE) {
                                // Premier chemin valide sans accordéon
                                isBetter = true;
                            } else if (actualSegments < minBends) {
                                // Moins de segments = TOUJOURS mieux
                                isBetter = true;
                            } else if (actualSegments == minBends) {
                                // Même nombre de segments: choisir le plus court
                                if (distance < minDistance * 0.95) {
                                    // Au moins 5% plus court pour éviter les changements mineurs
                                    isBetter = true;
                                } else if (Math.abs(distance - minDistance) < 20 && isNaturalPair) {
                                    // Distance similaire: préférer paire naturelle
                                    isBetter = true;
                                }
                            }
                            
                            if (isBetter) {
                                minBends = actualSegments;
                                minDistance = distance;
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
     * Calcule le nombre MINIMUM théorique de segments pour une paire de côtés
     * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     */
    private int getMinimumSegments(int origSide, int destSide, Point start, Point end) {
        // Sortie horizontale = DROITE(1) ou GAUCHE(3)
        boolean origHorizontal = (origSide == 1 || origSide == 3);
        boolean destHorizontal = (destSide == 1 || destSide == 3);
        
        // Cas 1: Les deux côtés ont la même orientation (horizontal-horizontal ou vertical-vertical)
        if (origHorizontal == destHorizontal) {
            // Il faut toujours 3 segments minimum: sortir, traverser perpendiculairement, entrer
            return 3;
        }
        
        // Cas 2: Orientations perpendiculaires (horizontal-vertical ou vertical-horizontal)
        // Vérifier si on peut faire un chemin direct en 1 segment (alignement parfait)
        if (origHorizontal) {
            // Origine horizontale, destination verticale
            if (start.y == end.y) {
                // Alignés horizontalement: 1 segment possible
                return 1;
            }
        } else {
            // Origine verticale, destination horizontale
            if (start.x == end.x) {
                // Alignés verticalement: 1 segment possible
                return 1;
            }
        }
        
        // Sinon, il faut 2 segments: sortir puis tourner pour entrer
        return 2;
    }
    
    /**
     * Détecte si un chemin fait des allers-retours inutiles (accordéon)
     * Détection ULTRA-STRICTE pour éliminer tous les chemins inefficaces
     */
    private boolean hasAccordion(List<Point> path) {
        if (path.size() < 4) return false;
        
        for (int i = 0; i < path.size() - 3; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            Point p3 = path.get(i + 2);
            Point p4 = path.get(i + 3);
            
            // Détecter un accordéon horizontal: p1-p2 à droite, p2-p3 vertical, p3-p4 à gauche (ou inverse)
            if (p1.y == p2.y && p2.x == p3.x && p3.y == p4.y) {
                if ((p2.x > p1.x && p4.x < p3.x) || (p2.x < p1.x && p4.x > p3.x)) {
                    return true;
                }
            }
            
            // Détecter un accordéon vertical: p1-p2 vertical, p2-p3 horizontal, p3-p4 vertical
            if (p1.x == p2.x && p2.y == p3.y && p3.x == p4.x) {
                if ((p2.y > p1.y && p4.y < p3.y) || (p2.y < p1.y && p4.y > p3.y)) {
                    return true;
                }
            }
        }
        
        // Détection supplémentaire: chemin qui fait un détour inutile
        // UNIQUEMENT pour chemins très longs (5+ segments)
        if (path.size() >= 6) {
            Point start = path.get(0);
            Point end = path.get(path.size() - 1);
            
            // Si début et fin sont alignés (même X ou même Y) mais qu'on a 5+ segments
            if ((start.x == end.x || start.y == end.y) && path.size() > 3) {
                return true;
            }
        }
        
        // Détection TRÈS stricte: si on a 4+ segments, vérifier qu'on n'a pas de segments
        // qui partent dans une direction puis reviennent partiellement
        if (path.size() >= 4) {
            for (int i = 0; i < path.size() - 2; i++) {
                Point p1 = path.get(i);
                Point p2 = path.get(i + 1);
                Point p3 = path.get(i + 2);
                
                // Segment horizontal qui change de direction
                if (p1.y == p2.y && p2.y == p3.y) {
                    int dir1 = Integer.compare(p2.x - p1.x, 0);
                    int dir2 = Integer.compare(p3.x - p2.x, 0);
                    if (dir1 != 0 && dir2 != 0 && dir1 != dir2) {
                        // Change de direction sur la même ligne horizontale
                        return true;
                    }
                }
                
                // Segment vertical qui change de direction
                if (p1.x == p2.x && p2.x == p3.x) {
                    int dir1 = Integer.compare(p2.y - p1.y, 0);
                    int dir2 = Integer.compare(p3.y - p2.y, 0);
                    if (dir1 != 0 && dir2 != 0 && dir1 != dir2) {
                        // Change de direction sur la même ligne verticale
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si une paire de côtés est naturelle (orientation logique)
     * Système de côtés: 0=HAUT, 1=DROITE, 2=BAS, 3=GAUCHE
     * Toutes les paires qui correspondent à la position relative sont acceptées
     */
    private boolean isNaturalSidePair(int oSide, int dSide, boolean right, boolean left, boolean below, boolean above) {
        // Paires perpendiculaires
        if (oSide == 1 && dSide == 0 && right && above) return true;  // DROITE -> HAUT
        if (oSide == 1 && dSide == 2 && right && below) return true;  // DROITE -> BAS
        if (oSide == 3 && dSide == 0 && left && above) return true;   // GAUCHE -> HAUT
        if (oSide == 3 && dSide == 2 && left && below) return true;   // GAUCHE -> BAS
        if (oSide == 0 && dSide == 1 && above && right) return true;  // HAUT -> DROITE
        if (oSide == 0 && dSide == 3 && above && left) return true;   // HAUT -> GAUCHE
        if (oSide == 2 && dSide == 1 && below && right) return true;  // BAS -> DROITE
        if (oSide == 2 && dSide == 3 && below && left) return true;   // BAS -> GAUCHE
        
        // Paires opposées sont aussi naturelles si elles correspondent à la direction
        if (oSide == 1 && dSide == 3 && right) return true;  // DROITE -> GAUCHE
        if (oSide == 3 && dSide == 1 && left) return true;   // GAUCHE -> DROITE  
        if (oSide == 2 && dSide == 0 && below) return true;  // BAS -> HAUT
        if (oSide == 0 && dSide == 2 && above) return true;  // HAUT -> BAS
        
        return false;
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
    
    /**
     * Classe pour stocker un chemin candidat avec son score
     */
    private static class CheminCandidat {
        int sideOrigine;
        int sideDestination;
        double posRelOrigine;
        double posRelDestination;
        int nbSegments;
        double distance;
        boolean hasAccordion;
        boolean isNaturalPair;
        
        CheminCandidat(int sideOrig, int sideDest, double posOrig, double posDest, 
                       int segments, double dist, boolean accordion, boolean natural) {
            this.sideOrigine = sideOrig;
            this.sideDestination = sideDest;
            this.posRelOrigine = posOrig;
            this.posRelDestination = posDest;
            this.nbSegments = segments;
            this.distance = dist;
            this.hasAccordion = accordion;
            this.isNaturalPair = natural;
        }
    }
    
    /**
     * Calcule le meilleur chemin possible pour une liaison entre deux blocs
     * OPTIMISATION: privilégie positions centrées et paires naturelles en premier
     */
    private CheminCandidat calculerMeilleurChemin(BlocClasse origine, BlocClasse destination) {
        CheminCandidat meilleurChemin = null;
        
        final int ox = origine.getX() + (origine.getLargeur() >> 1);
        final int oy = origine.getY() + (origine.getHauteurCalculee() >> 1);
        final int dx = destination.getX() + (destination.getLargeur() >> 1);
        final int dy = destination.getY() + (destination.getHauteurCalculee() >> 1);
        
        final boolean destIsRight = dx > ox, destIsLeft = dx < ox;
        final boolean destIsBelow = dy > oy, destIsAbove = dy < oy;
        
        // Positions triées par priorité: centre d'abord, puis quarts, puis tiers
        // Éviter les coins exacts (0.0 et 1.0) -> utiliser 0.1 et 0.9 à la place
        double[] positions = {0.5, 0.25, 0.75, 0.33, 0.67, 0.1, 0.9};
        
        // D'abord essayer les paires naturelles avec position centrale
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                boolean isNaturalPair = isNaturalSidePair(origSide, destSide, destIsRight, destIsLeft, destIsBelow, destIsAbove);
                
                if (isNaturalPair) {
                    Point start = GestionnaireAncrage.getPointOnSide(origine, origSide, 0.5);
                    Point end = GestionnaireAncrage.getPointOnSide(destination, destSide, 0.5);
                    
                    List<Point> testPath = calculateurChemin.createOrthogonalPath(start, end, origSide, destSide);
                    
                    if (!calculateurChemin.pathHasCollisions(testPath) && !hasAccordion(testPath)) {
                        int actualSegments = testPath.size() - 1;
                        double distance = calculateurChemin.calculatePathLength(testPath);
                        
                        CheminCandidat candidat = new CheminCandidat(
                            origSide, destSide, 0.5, 0.5,
                            actualSegments, distance, false, true
                        );
                        
                        if (meilleurChemin == null || estMeilleur(candidat, meilleurChemin)) {
                            meilleurChemin = candidat;
                        }
                    }
                }
            }
        }
        
        // Si on a déjà un excellent chemin (1-2 segments sans accordéon), on peut s'arrêter
        if (meilleurChemin != null && meilleurChemin.nbSegments <= 2) {
            return meilleurChemin;
        }
        
        // Sinon, tester toutes les combinaisons
        for (int origSide = 0; origSide < 4; origSide++) {
            for (int destSide = 0; destSide < 4; destSide++) {
                boolean isNaturalPair = isNaturalSidePair(origSide, destSide, destIsRight, destIsLeft, destIsBelow, destIsAbove);
                
                for (double origPos : positions) {
                    for (double destPos : positions) {
                        Point start = GestionnaireAncrage.getPointOnSide(origine, origSide, origPos);
                        Point end = GestionnaireAncrage.getPointOnSide(destination, destSide, destPos);
                        
                        List<Point> testPath = calculateurChemin.createOrthogonalPath(start, end, origSide, destSide);
                        
                        if (!calculateurChemin.pathHasCollisions(testPath)) {
                            int actualSegments = testPath.size() - 1;
                            double distance = calculateurChemin.calculatePathLength(testPath);
                            boolean hasAccordion = hasAccordion(testPath);
                            
                            // Pénalité légère pour positions exactement sur les coins
                            boolean isOnCorner = (origPos == 0.0 || origPos == 1.0 || destPos == 0.0 || destPos == 1.0);
                            double adjustedDistance = isOnCorner ? distance * 1.05 : distance;
                            
                            // BONUS FORT pour chemins à 2 segments (optimal)
                            // Pénalité pour 3+ segments pour vraiment favoriser la simplicité
                            if (actualSegments == 1) {
                                adjustedDistance *= 0.5; // Bonus énorme pour ligne droite
                            } else if (actualSegments == 2) {
                                adjustedDistance *= 0.7; // Bonus fort pour 2 segments
                            } else if (actualSegments >= 3) {
                                adjustedDistance *= 1.3; // Pénalité pour 3+ segments
                            }
                            
                            CheminCandidat candidat = new CheminCandidat(
                                origSide, destSide, origPos, destPos,
                                actualSegments, adjustedDistance, hasAccordion, isNaturalPair
                            );
                            
                            if (meilleurChemin == null || estMeilleur(candidat, meilleurChemin)) {
                                meilleurChemin = candidat;
                            }
                            
                            // Si on a un chemin parfait (1 segment), on peut arrêter
                            if (meilleurChemin != null && meilleurChemin.nbSegments == 1 && !meilleurChemin.hasAccordion) {
                                return meilleurChemin;
                            }
                        }
                    }
                }
            }
        }
        
        return meilleurChemin;
    }
    
    /**
     * Compare deux chemins candidats et détermine si le premier est meilleur
     * STRICTE: priorité absolue aux chemins sans accordéon et avec moins de segments
     * Prend en compte le coût des ponts (intersections avec d'autres liaisons)
     */
    private boolean estMeilleur(CheminCandidat candidat, CheminCandidat actuel) {
        // Priorité ABSOLUE: JAMAIS accepter un accordéon si on a un chemin sans accordéon
        if (candidat.hasAccordion && !actuel.hasAccordion) {
            return false;
        }
        if (!candidat.hasAccordion && actuel.hasAccordion) {
            return true;
        }
        
        // Si les deux ont un accordéon, prendre celui avec moins de segments
        if (candidat.hasAccordion && actuel.hasAccordion) {
            return candidat.nbSegments < actuel.nbSegments;
        }
        
        // Priorité 1: Moins de segments (ABSOLU - pas de tolérance)
        if (candidat.nbSegments < actuel.nbSegments) {
            return true;
        }
        if (candidat.nbSegments > actuel.nbSegments) {
            return false;
        }
        
        // Priorité 2: Même nombre de segments -> paire naturelle d'abord
        if (candidat.isNaturalPair && !actuel.isNaturalPair) {
            return true;
        }
        if (!candidat.isNaturalPair && actuel.isNaturalPair) {
            return false;
        }
        
        // Priorité 3: Plus court (seulement avec différence significative >20%)
        // Note: Les ponts ajoutent un coût implicite car ils nécessitent des détours
        if (candidat.distance < actuel.distance * 0.80) {
            return true;
        }
        
        // Priorité 4: Positions plus centrées (favorise 0.5)
        double candidatCentrality = Math.abs(candidat.posRelOrigine - 0.5) + Math.abs(candidat.posRelDestination - 0.5);
        double actuelCentrality = Math.abs(actuel.posRelOrigine - 0.5) + Math.abs(actuel.posRelDestination - 0.5);
        if (candidatCentrality < actuelCentrality * 0.9) {
            return true;
        }
        
        return false;
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
        
        final int OFFSET_DISTANCE = 8;
        int myIndex = toutesLesLiaisons.indexOf(this);
        
        // Compter les liaisons ayant exactement la même origine et destination
        List<LiaisonVue> samePath = new ArrayList<>();
        for (LiaisonVue autre : toutesLesLiaisons) {
            if (autre == this) continue;
            
            // Vérifier si les deux liaisons ont la même origine et destination
            boolean sameOriginDest = (autre.blocOrigine == this.blocOrigine && autre.blocDestination == this.blocDestination);
            boolean sameDestOrigin = (autre.blocOrigine == this.blocDestination && autre.blocDestination == this.blocOrigine);
            
            if (sameOriginDest || sameDestOrigin) {
                samePath.add(autre);
            }
        }
        
        // Si des liaisons partagent le même chemin, appliquer un décalage centré
        if (!samePath.isEmpty()) {
            int myPositionInGroup = 0;
            for (LiaisonVue autre : samePath) {
                if (toutesLesLiaisons.indexOf(autre) < myIndex) {
                    myPositionInGroup++;
                }
            }
            
            // Décalage centré : -offset, 0, +offset, -2*offset, +2*offset, etc.
            int totalInGroup = samePath.size() + 1;
            int centerOffset = -(totalInGroup / 2) * OFFSET_DISTANCE;
            return centerOffset + (myPositionInGroup * OFFSET_DISTANCE);
        }
        
        // Sinon, utiliser l'ancien système basé sur les segments partagés
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

        // Dessiner les flèches au dernier point réel du chemin (après offset)
        Point pointFleche = path.get(path.size() - 1);
        
        if (type.equals("association") && unidirectionnel) {
            renduLiaison.dessinerFlecheAssociation(g, pointFleche, sideDestination);
        }
        if (type.equals("heritage") || type.equals("interface")) {
            renduLiaison.dessinerFlecheVide(g, pointFleche, sideDestination);
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
    
    public String getSideOrig() {
        switch(sideOrigine) {
            case 0: return "TOP";
            case 1: return "RIGHT";
            case 2: return "BOTTOM";
            case 3: return "LEFT";
            default: return "UNKNOWN";
        }
    }
    
    public String getSideDest() {
        switch(sideDestination) {
            case 0: return "TOP";
            case 1: return "RIGHT";
            case 2: return "BOTTOM";
            case 3: return "LEFT";
            default: return "UNKNOWN";
        }
    }
    
    public double getNivOrig() { return posRelOrigine; }
    
    public double getNivDest() { return posRelDestination; }
    
    /**
     * Recalcule les chemins des liaisons ayant la même origine et destination
     * et applique un décalage visuel pour les distinguer.
     */
    public void recalculerCheminPartage() {
        // Regrouper les liaisons ayant la même origine et destination
        List<LiaisonVue> liaisonsPartagees = new ArrayList<>();
        for (LiaisonVue liaison : toutesLesLiaisons) {
            if (liaison.blocOrigine == this.blocOrigine && liaison.blocDestination == this.blocDestination) {
                liaisonsPartagees.add(liaison);
            }
        }

        // Calculer un chemin commun
        Point ancrageOrigineCommun = GestionnaireAncrage.getPointOnSide(blocOrigine, sideOrigine, posRelOrigine);
        Point ancrageDestinationCommun = GestionnaireAncrage.getPointOnSide(blocDestination, sideDestination, posRelDestination);
        List<Point> cheminCommun = calculateurChemin.createOrthogonalPath(ancrageOrigineCommun, ancrageDestinationCommun, sideOrigine, sideDestination);

        // Appliquer un décalage visuel pour chaque liaison
        int offset = -10 * (liaisonsPartagees.size() / 2); // Centrer les décalages
        for (LiaisonVue liaison : liaisonsPartagees) {
            List<Point> cheminAvecOffset = calculateurChemin.applyOffsetToPath(cheminCommun, offset);
            liaison.setChemin(cheminAvecOffset); // Met à jour le chemin de la liaison
            offset += 10; // Décalage visuel entre les liaisons
        }
    }

    /**
     * Met à jour le chemin de la liaison.
     * @param chemin Le nouveau chemin à appliquer.
     */
    public void setChemin(List<Point> chemin) {
        // Implémentation pour mettre à jour le chemin
        this.calculateurChemin.setPath(chemin);
    }
}
