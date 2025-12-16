package testUnitaire;

import  vue.BlocClasse;


import org.junit.jupiter.api.*;

import metier.sauvegarde.GestionSauvegarde;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SauvegardeTest 
{

    private static final String FICHIER_TEST = "donnees/projets.xml";

    @BeforeEach
    void setUp() throws IOException 
    {
        // On crée un fichier vide avant chaque test
        Files.createDirectories(Paths.get("donnees"));
        Files.write(Paths.get(FICHIER_TEST), new byte[0]);
    }

    @AfterEach
    void tearDown() throws IOException 
    {
        // On supprime le fichier après le test
        Files.deleteIfExists(Paths.get(FICHIER_TEST));
    }

    @Test
    void testSauvegarderClasses() throws IOException 
    {
        GestionSauvegarde sauvegarde = new GestionSauvegarde();

        List<BlocClasse> blocs = new ArrayList<>(); // liste vide pour le test
        String cheminProjet    = "/mon/projet";

        // Premier appel → pas de doublon
        sauvegarde.sauvegarderClasses(blocs, cheminProjet);

        List<String> lignes = Files.readAllLines(Paths.get(FICHIER_TEST));
        assertEquals(1, lignes.size());
        assertEquals("/mon/projet\tprojet", lignes.get(0));

        // Deuxième appel → crée un doublon
        sauvegarde.sauvegarderClasses(blocs, cheminProjet);

        lignes = Files.readAllLines(Paths.get(FICHIER_TEST));
        assertEquals(2, lignes.size());
        assertEquals("/mon/projet\tprojet_2", lignes.get(1));
    }
}
