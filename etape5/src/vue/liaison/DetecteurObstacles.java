package vue.liaison;

import vue.BlocClasse;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Détection d'obstacles pour le routage des liaisons
 */
public class DetecteurObstacles {
    
    private final BlocClasse blocOrigine;
    private final BlocClasse blocDestination;
    private final List<BlocClasse> tousLesBlocs;
    
    public DetecteurObstacles(BlocClasse origine, BlocClasse destination, List<BlocClasse> blocs) {
        this.blocOrigine = origine;
        this.blocDestination = destination;
        this.tousLesBlocs = blocs;
    }
    
    /**
     * Détecte si un segment est libre d'obstacles
     */
    public boolean hasObstacle(boolean isHoriz, int a1, int a2, int b) {
        return isHoriz ? !getObstaclesOnHorizontalLine(a1, a2, b).isEmpty() 
                       : !getObstaclesOnVerticalLine(b, a1, a2).isEmpty();
    }
    
    /**
     * Détection STRICTE : vérifie qu'une ligne horizontale ne traverse AUCUN bloc
     */
    public boolean hasHorizontalObstacleStrict(int x1, int x2, int y) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            if (y >= by && y <= by + bh && maxX >= bx && minX <= bx + bw) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Détection STRICTE : vérifie qu'une ligne verticale ne traverse AUCUN bloc
     */
    public boolean hasVerticalObstacleStrict(int x, int y1, int y2) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            if (x >= bx && x <= bx + bw && maxY >= by && minY <= by + bh) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Renvoie les blocs qui coupent une ligne horizontale
     */
    public List<BlocClasse> getObstaclesOnHorizontalLine(int x1, int x2, int y) {
        List<BlocClasse> obstacles = new ArrayList<>();
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int margin = 10;
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
            boolean yTraverseBloc = (y >= by - margin && y <= by + bh + margin);
            boolean xCroiseBloc = !(maxX < bx - margin || minX > bx + bw + margin);
            
            if (yTraverseBloc && xCroiseBloc) {
                obstacles.add(bloc);
            }
        }
        return obstacles;
    }
    
    /**
     * Renvoie les blocs qui coupent une ligne verticale
     */
    public List<BlocClasse> getObstaclesOnVerticalLine(int x, int y1, int y2) {
        List<BlocClasse> obstacles = new ArrayList<>();
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int margin = 10;
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc == blocOrigine || bloc == blocDestination) continue;
            
            int bx = bloc.getX();
            int by = bloc.getY();
            int bw = bloc.getLargeur();
            int bh = bloc.getHauteurCalculee();
            
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
     */
    public int getDeflectionY(int originalY, List<BlocClasse> obstacles) {
        if (obstacles.isEmpty()) return originalY;
        
        int topMin = Integer.MAX_VALUE;
        int bottomMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            topMin = Math.min(topMin, bloc.getY());
            bottomMax = Math.max(bottomMax, bloc.getY() + bloc.getHauteurCalculee());
        }
        
        int margin = 30;
        int distToTop = Math.abs(originalY - topMin);
        int distToBottom = Math.abs(originalY - bottomMax);
        
        return distToTop < distToBottom ? topMin - margin : bottomMax + margin;
    }
    
    /**
     * Calcule une position X pour contourner les obstacles verticaux
     */
    public int getDeflectionX(int originalX, List<BlocClasse> obstacles) {
        if (obstacles.isEmpty()) return originalX;
        
        int leftMin = Integer.MAX_VALUE;
        int rightMax = Integer.MIN_VALUE;
        
        for (BlocClasse bloc : obstacles) {
            leftMin = Math.min(leftMin, bloc.getX());
            rightMax = Math.max(rightMax, bloc.getX() + bloc.getLargeur());
        }
        
        int margin = 30;
        int distToLeft = Math.abs(originalX - leftMin);
        int distToRight = Math.abs(originalX - rightMax);
        
        return distToLeft < distToRight ? leftMin - margin : rightMax + margin;
    }
    
    /**
     * Trouve une ligne horizontale claire entre deux Y
     */
    public int findClearHorizontalLine(int y1, int y2) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int midY = (minY + maxY) / 2;
        
        if (getObstaclesOnHorizontalLine(Integer.MIN_VALUE, Integer.MAX_VALUE, midY).isEmpty()) {
            return midY;
        }
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc != blocOrigine && bloc != blocDestination) {
                int testY = bloc.getY() - 30;
                if (testY > minY && testY < maxY) {
                    return testY;
                }
            }
        }
        return minY - 30;
    }
    
    /**
     * Trouve une ligne verticale claire entre deux X
     */
    public int findClearVerticalLine(int x1, int x2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int midX = (minX + maxX) / 2;
        
        if (getObstaclesOnVerticalLine(midX, Integer.MIN_VALUE, Integer.MAX_VALUE).isEmpty()) {
            return midX;
        }
        
        for (BlocClasse bloc : tousLesBlocs) {
            if (bloc != blocOrigine && bloc != blocDestination) {
                int testX = bloc.getX() - 30;
                if (testX > minX && testX < maxX) {
                    return testX;
                }
            }
        }
        return minX - 30;
    }
}
