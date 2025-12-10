package vue;

import controlleur.Controlleur;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PanneauDiagramme extends JPanel 
{

    private List<BlocClasse> blocsClasses;
    private List<LiaisonVue> liaisons;

    private String           cheminProjetCourant;
    private BlocClasse       blocEnDeplacement;
    private Point            pointDernier;
    private Controlleur      controlleur;
    
    // Pour le drag des points d'ancrage de liaisons
    private LiaisonVue liaisonEnDeplacement;
    private boolean draggingOriginAnchor;
    private boolean draggingDestinationAnchor;

    public PanneauDiagramme() 
    {
        this.blocsClasses        = new ArrayList<>();
        this.liaisons            = new ArrayList<>();
        this.cheminProjetCourant = null;
        this.controlleur         = new Controlleur();

        setLayout(null);
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createTitledBorder("Diagramme UML"));


        ajouterListenersInteraction();
    }

    public void chargerProjet(String cheminProjet) 
    {
        this.cheminProjetCourant = cheminProjet;
        this.blocsClasses.clear();
        this.liaisons.clear();

        List<BlocClasse> blocCharges = controlleur.chargerProjetEnBlocsClasses(cheminProjet);
        blocsClasses.addAll(blocCharges);
        liaisons.addAll(controlleur.getLiaisons());

        repaint();
    }

    private void ajouterListenersInteraction() 
    {
        addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mousePressed(MouseEvent e) 
            {
                pointDernier = e.getPoint();
                blocEnDeplacement = null;
                liaisonEnDeplacement = null;
                draggingOriginAnchor = false;
                draggingDestinationAnchor = false;

                // Vérifier si on clique sur un point d'ancrage de liaison
                for (LiaisonVue liaison : liaisons) 
                {
                    if (liaison.isOnOriginAnchor(e.getPoint())) 
                    {
                        liaisonEnDeplacement = liaison;
                        draggingOriginAnchor = true;
                        return;
                    }
                    if (liaison.isOnDestinationAnchor(e.getPoint())) 
                    {
                        liaisonEnDeplacement = liaison;
                        draggingDestinationAnchor = true;
                        return;
                    }
                }

                // Sinon, vérifier si on clique sur un bloc
                for (BlocClasse bloc : blocsClasses) 
                {
                    if (bloc.contient(e.getX(), e.getY())) 
                    {
                        blocEnDeplacement = bloc;
                        bloc.setSelectionne(true);
                        break;
                    }
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) 
            {
                if (blocEnDeplacement != null) 
                {
                    blocEnDeplacement.setSelectionne(false);
                }
                blocEnDeplacement         = null;
                liaisonEnDeplacement      = null;
                draggingOriginAnchor      = false;
                draggingDestinationAnchor = false;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Drag d'un point d'ancrage de liaison
                if (liaisonEnDeplacement != null) {
                    if (draggingOriginAnchor) {
                        liaisonEnDeplacement.dragOriginAnchor(e.getPoint());
                    } else if (draggingDestinationAnchor) {
                        liaisonEnDeplacement.dragDestinationAnchor(e.getPoint());
                    }
                    repaint();
                    return;
                }

                // Drag d'un bloc
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

        // Dessiner les liaisons
        dessinerLiaisons(g2d);

        // Dessiner les blocs
        for (BlocClasse bloc : blocsClasses) {
            bloc.dessiner(g2d);
        }
    }

    private void dessinerLiaisons(Graphics2D g2d) {
        for (LiaisonVue liaison : liaisons) {
            liaison.dessiner(g2d);
        }
        repaint();
    }

    public void supprimerLiaison(LiaisonVue liaison) {
        liaisons.remove(liaison);
        controlleur.supprimerLiaison(liaison);
        this.repaint();
    }


    public List<BlocClasse> getBlocsClasses() {
        return blocsClasses;
    }

    public String getCheminProjetCourant() {
        return cheminProjetCourant;
    }
}
