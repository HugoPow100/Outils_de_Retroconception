package testUnitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import metier.sauvegarde.GestionSauvegarde;

import java.util.Map;

public class LectureCoordTest {

    @Test
    public void testLectureAnimalerie() {

        GestionSauvegarde lecture = new GestionSauvegarde();

        // Lecture du fichier animalerie
        lecture.lecture("animalerie.xml");

        Map<String, int[]> coords = lecture.lireCoordoneesXml(lecture.getIntituleFromLien(lecture) + ".xml");

        // === AFFICHAGE POUR DEBUG ===
        System.out.println("=== Affichage de la map dans le test ===");
        coords.forEach((key, value) -> {
            System.out.println(key + " -> x=" + value[0] + ", y=" + value[1]);
        });
        System.out.println("========================================");

        // Vérification de la map
        assertNotNull(coords, "La map ne doit pas être null.");
        assertEquals(2, coords.size(), "La map doit contenir exactement 2 éléments.");

        // Vérification des coordonnées chargées
        assertArrayEquals(new int[]{42, 16}, coords.get("Chat"),
                "Les coordonnées de Chat sont incorrectes.");

        assertArrayEquals(new int[]{50, 45}, coords.get("Collier"),
                "Les coordonnées de Collier sont incorrectes.");

        // Vérification du chemin dossier détecté
        String chemin = lecture.getCheminDossier();
        assertNotNull(chemin, "Le chemin dossier doit être détecté.");
        assertTrue(chemin.contains("animalerie"),
                "Le chemin doit contenir 'animalerie'.");
        System.out.println("Chemin dossier détecté : " + chemin);
    }
}


