package vue.liaison;

import vue.BlocClasse;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcul des chemins orthogonaux pour les liaisons
 */
public class CalculateurChemin {
    
    private final DetecteurObstacles detecteurObstacles;
    
    public CalculateurChemin(DetecteurObstacles detecteur) {
        this.detecteurObstacles = detecteur;
    }
    
    /**
     * Crée une liste de points qui représente un chemin orthogonal
     */
    public List<Point> createOrthogonalPath(Point start, Point end, int startSide, int endSide) {
        List<Point> path = new ArrayList<>();
        path.add(start);
        
        int padding = 15;
        Point exitPoint = calculateOffsetPoint(start, startSide, padding);
        Point entryPoint = calculateOffsetPoint(end, endSide, padding);
        
        path.add(exitPoint);
        
        boolean exitHorizontal = (startSide == 0 || startSide == 2);
        boolean entryHorizontal = (endSide == 0 || endSide == 2);
        
        if (exitHorizontal && entryHorizontal) {
            if (exitPoint.y != entryPoint.y) {
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
            if (exitPoint.x != entryPoint.x) {
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
            path.add(new Point(entryPoint.x, exitPoint.y));
        } else {
            path.add(new Point(exitPoint.x, entryPoint.y));
        }
        
        path.add(entryPoint);
        path.add(end);
        
        return cleanRedundantPoints(path);
    }
    
    /**
     * Calcule le point décalé selon le côté
     */
    private Point calculateOffsetPoint(Point pt, int side, int padding) {
        return new Point(pt.x + (side == 0 ? padding : side == 2 ? -padding : 0),
                        pt.y + (side == 1 ? padding : side == 3 ? -padding : 0));
    }
    
    /**
     * Nettoie les points redondants (3 points alignés → 2 points)
     */
    private List<Point> cleanRedundantPoints(List<Point> path) {
        if (path.size() < 3) return path;
        
        List<Point> cleaned = new ArrayList<>();
        cleaned.add(path.get(0));
        
        for (int i = 1; i < path.size() - 1; i++) {
            Point prev = cleaned.get(cleaned.size() - 1);
            Point curr = path.get(i);
            Point next = path.get(i + 1);
            
            boolean allHorizontal = (prev.y == curr.y && curr.y == next.y);
            boolean allVertical = (prev.x == curr.x && curr.x == next.x);
            
            if (!allHorizontal && !allVertical) {
                cleaned.add(curr);
            }
        }
        
        cleaned.add(path.get(path.size() - 1));
        
        // Vérification de sécurité : éliminer les diagonales
        for (int i = 0; i < cleaned.size() - 1; i++) {
            Point p1 = cleaned.get(i);
            Point p2 = cleaned.get(i + 1);
            if (p1.x != p2.x && p1.y != p2.y) {
                cleaned.add(i + 1, new Point(p2.x, p1.y));
                i = -1;
            }
        }
        
        return cleaned;
    }
    
    /**
     * Calcule la longueur totale d'un chemin
     */
    public double calculatePathLength(List<Point> path) {
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
     * Vérifie si un chemin a des collisions avec les blocs
     */
    public boolean pathHasCollisions(List<Point> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            
            if (p1.x == p2.x) {
                if (detecteurObstacles.hasObstacle(false, Math.min(p1.y, p2.y), Math.max(p1.y, p2.y), p1.x)) {
                    return true;
                }
            } else if (p1.y == p2.y) {
                if (detecteurObstacles.hasObstacle(true, Math.min(p1.x, p2.x), Math.max(p1.x, p2.x), p1.y)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Applique un décalage perpendiculaire à tous les segments du chemin
     */
    public List<Point> applyOffsetToPath(List<Point> path, int offset) {
        List<Point> offsetPath = new ArrayList<>();
        
        for (int i = 0; i < path.size(); i++) {
            Point p = path.get(i);
            
            if (i == 0) {
                Point next = path.get(i + 1);
                if (next.x == p.x) {
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else {
                    offsetPath.add(new Point(p.x, p.y + offset));
                }
            } else if (i == path.size() - 1) {
                Point prev = path.get(i - 1);
                if (prev.x == p.x) {
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else {
                    offsetPath.add(new Point(p.x, p.y + offset));
                }
            } else {
                Point prev = path.get(i - 1);
                Point next = path.get(i + 1);
                
                boolean prevVertical = (prev.x == p.x);
                boolean nextVertical = (next.x == p.x);
                
                if (prevVertical && nextVertical) {
                    offsetPath.add(new Point(p.x + offset, p.y));
                } else if (!prevVertical && !nextVertical) {
                    offsetPath.add(new Point(p.x, p.y + offset));
                } else {
                    offsetPath.add(new Point(p.x + offset, p.y + offset));
                }
            }
        }
        
        return offsetPath;
    }
}
