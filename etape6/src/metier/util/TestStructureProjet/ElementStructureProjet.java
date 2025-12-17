package metier.util.TestStructureProjet;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Enum décrivant les éléments obligatoires de la structure du projet.
 *
 * Chaque valeur représente un élément (dossier ou fichier) identifié
 * par son chemin relatif dans le projet et par son type.
 * L'enum centralise la définition de la structure attendue et fournit
 * les méthodes nécessaires pour vérifier l'existence de chaque élément.
 */
public enum ElementStructureProjet
{

    //DOSSIER extérieur à la RACINE projet (etape5)
    TEST_LIB(     "../test-lib"      , TypeElement.DOSSIER),

    // DOSSIERS RACINE
    BIN(          "bin"           , TypeElement.DOSSIER),
    DATA(         "data"          , TypeElement.DOSSIER),
    SRC(          "src"           , TypeElement.DOSSIER),
    TEST_UNITAIRE("testUnitaire"  , TypeElement.DOSSIER),



    
    // SOUS-DOSSIERS DATA
    DONNEES(             "data/donnees"             , TypeElement.DOSSIER),
    SAUVEGARDES(         "data/sauvegardes"         , TypeElement.DOSSIER),
    SAUVEGARDES_DOSSIERS("data/sauvegardes/dossiers", TypeElement.DOSSIER),


    // DOSSIERS SRC
    CONTROLEUR(       "src/controleur"       , TypeElement.DOSSIER),
    METIER(           "src/metier"           , TypeElement.DOSSIER),
    METIER_LECTURE(   "src/metier/lecture"   , TypeElement.DOSSIER),
    METIER_OBJET(     "src/metier/objet"     , TypeElement.DOSSIER),
    METIER_SAUVEGARDE("src/metier/sauvegarde", TypeElement.DOSSIER),
    METIER_UTIL(      "src/metier/util"      , TypeElement.DOSSIER),
    VUE(              "src/vue"              , TypeElement.DOSSIER),
    VUE_LIAISON(      "src/vue/liaison"      , TypeElement.DOSSIER),


    //FICHIER OBLIGATOIRES extérieur à la RACINE projet (etape5)
    GITIGNORE(   "../.gitignore"             , TypeElement.FICHIER),

    // FICHIERS OBLIGATOIRES
    PROJETS_XML( "data/donnees/projets.xml"   , TypeElement.FICHIER),
    COMPILE_LIST("compile.list"               , TypeElement.FICHIER);

    /* ------------------------------*/
    /* -----------ATTRIBUTS----------*/
    /* ------------------------------*/
    private final String chemin;
    private final TypeElement type;


    /**
     * Crée un élément de structure du projet.
     *
     * @param chemin chemin relatif de l'élément dans le projet
     * @param type   type de l'élément (dossier ou fichier)
     */
    ElementStructureProjet(String chemin, TypeElement type)
    {
        this.chemin = chemin;
        this.type   = type;
    }


    /**
     * Vérifie l'existence de l'élément sur le système de fichiers.
     *
     * @return true si l'élément existe et correspond à son type, false sinon
     */
    public boolean existe()
    {
        Path path = Path.of(this.chemin);

        if (this.type == TypeElement.DOSSIER)
        {
            return Files.isDirectory(path);
        }
        else
        {
            return Files.isRegularFile(path);
        }
    }


    /**
     * Retourne le message d'erreur associé à l'élément lorsque celui-ci est absent.
     *
     * @return message d'erreur décrivant l'élément manquant
     */
    public String getMessageErreur()
    {
        if (this.type == TypeElement.DOSSIER)
        {
            return "Dossier manquant : " + this.chemin;
        }
        else
        {
            return "Fichier manquant : " + this.chemin;
        }
    }
}

