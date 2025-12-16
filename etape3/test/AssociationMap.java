import java.util.HashMap;

public class AssociationMap {
    private HashMap<String, Point> pointMap;
    private HashMap<String, Disque> disqueMap;

    public AssociationMap(HashMap<String, Point> pointMap, HashMap<String, Disque> disqueMap) {
        this.pointMap = pointMap;
        this.disqueMap = disqueMap;
    }
}
