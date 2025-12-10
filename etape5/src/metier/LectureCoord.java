import java.util.HashMap;
import java.util.Map;

public class LectureCoord {

    private Map<String, int[]> coordonnees = new HashMap<>();
    private String cheminDossier;

    public LectureCoord() 
    {
        this.cheminDossier = "donnees/sauvegardes/dossiers";
    }

    public void lecture(String chemin)

    public Map<String, int[]> getCoordonnees() 
    {
        return coordonnees;
    }

    public void ajouterCoord(String nom, int x, int y) 
    {
        coordonnees.put(nom, new int[]{x, y});
    }

}
