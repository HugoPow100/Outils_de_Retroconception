package vue;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FenetrePrincipale extends JFrame {

    private PanneauProjets panneauProjets;
    private PanneauDiagramme panneauDiagramme;

    public FenetrePrincipale() {
        setTitle("Générateur de diagramme UML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
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
        add(new BarreMenus(), BorderLayout.NORTH);
    }

    public void chargerProjet(String cheminProjet) {
        panneauDiagramme.chargerProjet(cheminProjet);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }
}
