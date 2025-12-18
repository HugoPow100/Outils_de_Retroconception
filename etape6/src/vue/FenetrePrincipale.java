package vue;

import controleur.Controleur;
import java.awt.BorderLayout;
import java.awt.Dimension; 
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import metier.util.test_structure_projet.VerificationStructureProjet;
import vue.liaison.LiaisonVue;

/**
* Fenêtre principale de l'IHM du générateur de diagramme UML.
* Gère également la liasion avec le controleur.
* @author Jules
*/
public class FenetrePrincipale extends JFrame 
{
    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

    private PanneauProjets   panneauProjets  ;
    private PanneauDiagramme panneauDiagramme;
    private Controleur       controleur      ;

    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//
    
    public FenetrePrincipale() 
    {
        setTitle("Générateur de diagramme UML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setMinimumSize(new Dimension(700,400));
        setLocationRelativeTo(null);
        setResizable(true);

        panneauProjets   = new PanneauProjets  (this);
        panneauDiagramme = new PanneauDiagramme(this);
        this.controleur  = new Controleur      (this);

        setLayout(new BorderLayout());
        
        // La ligne de division entre panel projet et panel diagramme
        JSplitPane splitPane = new JSplitPane
        (
            JSplitPane.HORIZONTAL_SPLIT,
            true,
            panneauProjets,
            panneauDiagramme
        );

        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);

        this.add(splitPane           , BorderLayout.CENTER);
        this.add(new BarreMenus(this), BorderLayout.NORTH );
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    public void ouvrirProjet(String cheminProjet) 
    {
        try
        {
            panneauDiagramme.chargerProjet(cheminProjet);
            // Enregistrer le projet au premier chargement
            controleur.sauvegardeProjetXml(cheminProjet);
            // Actualiser la liste des projets
            panneauProjets.actualiser();
        }
        catch (Exception e)
        {
            String message = "Erreur lors du chargement du projet :\n";
            
            if (e instanceof java.io.FileNotFoundException)
            {
                message += "Fichier ou dossier introuvable ou accès refusé.\n";
                message += "Vérifiez que vous avez les permissions nécessaires.";
            }
            else
            {
                message += e.getMessage();
            }
            
            javax.swing.JOptionPane.showMessageDialog
            (
                this, 
                message, 
                "Erreur de chargement", 
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    public void sauvegarderDiagramme() 
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // choisir un fichier
        chooser.setSelectedFile(new File("diagramme.png")); // nom par défaut

        int retour = chooser.showSaveDialog(panneauDiagramme);

        if (retour == JFileChooser.APPROVE_OPTION) 
        {
            File fichierSortie = chooser.getSelectedFile();
            if (!fichierSortie.getName().toLowerCase().endsWith(".png")) 
            {
                fichierSortie = new File(fichierSortie.getParentFile(), fichierSortie.getName() + ".png");
            }
            
            try 
            {
                // Sauvegarder le zoom actuel et le réinitialiser pour l'export
                double  zoomSauvegarde     = panneauDiagramme.getZoomLevel      ();
                boolean textZoomSauvegarde = panneauDiagramme.isAfficherTextZoom();
                
                panneauDiagramme.setZoomLevel(1.0);
                panneauDiagramme.setAfficherTextZoom(false);
                
                BufferedImage image = new BufferedImage
                (
                    panneauDiagramme.getWidth(), panneauDiagramme.getHeight(), BufferedImage.TYPE_INT_ARGB
                );

                panneauDiagramme.printAll(image.createGraphics());
                ImageIO.write(image, "png", fichierSortie);
                System.out.println("Diagramme sauvegardé dans"+fichierSortie);
                
                // Restaurer le zoom et l'affichage du texte
                panneauDiagramme.setZoomLevel       (zoomSauvegarde    );
                panneauDiagramme.setAfficherTextZoom(textZoomSauvegarde);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) 
    {
         // Vérification de la structure du projet
        VerificationStructureProjet verification = new VerificationStructureProjet();
        verification.verifierStructure();

        SwingUtilities.invokeLater(() ->
        {
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }

    public void affichageAttributs(boolean b)
    {
        panneauDiagramme.setAfficherAttributs(b);
    }

    public void affichageMethodes(boolean b)
    {
        panneauDiagramme.setAfficherMethodes(b);
    }

    public void optimiserPositionsClasses()
    {
        panneauDiagramme.optimiserPositionsClasses();
    }

    public void optimiserPositionsLiaisons()
    {
        panneauDiagramme.optimiserPositionsLiaisons();
    }

    public void setSauvegardeAuto(boolean b)
    {
        panneauDiagramme.setSauvegardeAuto(b);
    }

    public void actionSauvegarder()
    {
        panneauDiagramme.actionSauvegarder();
    }

    /**
    * Méthodes passerelle au controleur
    */
    public void chargerProjet(String cheminProjet) 
    {
        controleur.chargerProjet(cheminProjet);
    }

    public List<BlocClasse> getBlocClasses()
    {
        return controleur.getBlocClasses();
    }

    public List<LiaisonVue> getLiaisons()
    {
        return controleur.getLiaisons();
    }

    public ArrayList<String> getLstFichiersInvalides(String cheminProjet)
    {
        return this.controleur.getLstFichiersInvalides(cheminProjet);
    }

    public void sauvegarderClasses(List<BlocClasse> blocClasses, List<LiaisonVue> liaisonVues, String cheminProjet)
    {
        controleur.sauvegarderClasses(blocClasses, liaisonVues, cheminProjet);
    }

    public void viderDiagramme()
    {
        panneauDiagramme.viderDiagramme();
    }
}