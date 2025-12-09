package vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import controlleur.Controlleur;

public class PanneauDiagramme extends JPanel {

    private List<BlocClasse> blocsClasses;
    private String cheminProjetCourant;
    private BlocClasse blocEnDeplacement;
    private Point pointDernier;
    private Controlleur controlleur;

    public PanneauDiagramme() {
        this.blocsClasses = new ArrayList<>();
        this.cheminProjetCourant = null;
        this.controlleur = new Controlleur();

        setLayout(null);
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createTitledBorder("Diagramme UML"));

        ajouterListenersInteraction();
    }

    public void chargerProjet(String cheminProjet) {
        this.cheminProjetCourant = cheminProjet;
        blocsClasses.clear();

        List<BlocClasse> blocCharges = controlleur.chargerProjetEnBlocsClasses(cheminProjet);
        blocsClasses.addAll(blocCharges);

        repaint();
    }

    private void ajouterListenersInteraction() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pointDernier = e.getPoint();
                blocEnDeplacement = null;

                for (BlocClasse bloc : blocsClasses) {
                    if (bloc.contient(e.getX(), e.getY())) {
                        blocEnDeplacement = bloc;
                        bloc.setSelectionne(true);
                        break;
                    }
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (blocEnDeplacement != null) {
                    blocEnDeplacement.setSelectionne(false);
                }
                blocEnDeplacement = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (blocEnDeplacement != null) {
                    int dx = e.getX() - pointDernier.x;
                    int dy = e.getY() - pointDernier.y;

                    blocEnDeplacement.deplacer(dx, dy);
                    pointDernier = e.getPoint();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner les blocs
        for (BlocClasse bloc : blocsClasses) {
            bloc.dessiner(g2d);
        }

        // Dessiner les liaisons
        dessinerLiaisons(g2d);
    }

    private void dessinerLiaisons(Graphics2D g2d) {
    }

    public List<BlocClasse> getBlocsClasses() {
        return blocsClasses;
    }

    public String getCheminProjetCourant() {
        return cheminProjetCourant;
    }
}
