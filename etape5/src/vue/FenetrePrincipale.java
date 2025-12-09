package vue;

import java.awt.BorderLayout;
import java.awt.Dimension; 
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;



public class FenetrePrincipale extends JFrame {

    private PanneauProjets panneauProjets;
    private PanneauDiagramme panneauDiagramme;

    public FenetrePrincipale() {
        setTitle("Générateur de diagramme UML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setMinimumSize(new Dimension(700,400));
        setLocationRelativeTo(null);
        setResizable(true);

        panneauProjets = new PanneauProjets(this);
        panneauDiagramme = new PanneauDiagramme();

        setLayout(new BorderLayout());
        
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            true,
            panneauProjets,
            panneauDiagramme
        );
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);
        add(new BarreMenus(this), BorderLayout.NORTH);
    }

    public void chargerProjet(String cheminProjet) {
        panneauDiagramme.chargerProjet(cheminProjet);
    }


    public void sauvegarderDiagramme() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // choisir un fichier
        chooser.setSelectedFile(new File("diagramme.png")); // nom par défaut

        int retour = chooser.showSaveDialog(panneauDiagramme);
        if (retour == JFileChooser.APPROVE_OPTION) {

            File fichierSortie = chooser.getSelectedFile();
            if (!fichierSortie.getName().toLowerCase().endsWith(".png")) {
                fichierSortie = new File(fichierSortie.getParentFile(), fichierSortie.getName() + ".png");
            }
            
            try {
            BufferedImage image = new BufferedImage(
                panneauDiagramme.getWidth(), panneauDiagramme.getHeight(), BufferedImage.TYPE_INT_ARGB
            );
            panneauDiagramme.printAll(image.createGraphics());
            ImageIO.write(image, "png", fichierSortie);
            System.out.println("Diagramme sauvegardé dans"+fichierSortie);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }
}
