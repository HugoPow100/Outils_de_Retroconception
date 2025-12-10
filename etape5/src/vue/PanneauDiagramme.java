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

    private boolean afficherAttributs = true;
    private boolean afficherMethodes = true;
    
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
        
        // Passer la liste des blocs à toutes les liaisons pour le contournement
        for (LiaisonVue liaison : liaisons) {
            liaison.setTousLesBlocs(blocsClasses);
        }

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

    public void optimiserPositions()
    {
        if (blocsClasses.isEmpty()) {
            return;
        }

        // organiser les blocs en grille
        organiserEnGrille();
        
        // réinitialiser les positions des ancres pour les liaisons
        reinitialiserAnchages();
        
        // Étape 4 : Redessiner
        repaint();
    }
    
    /**
     * Organise les blocs en une grille régulière avec espacement optimal
     */
    private void organiserEnGrille()
    {
        int cols = (int) Math.ceil(Math.sqrt(blocsClasses.size()));
        int spacing = 250; // Espacement entre les blocs
        int startX = 50;
        int startY = 50;
        
        for (int i = 0; i < blocsClasses.size(); i++) {
            BlocClasse bloc = blocsClasses.get(i);
            int col = i % cols;
            int row = i / cols;
            
            int newX = startX + col * spacing;
            int newY = startY + row * spacing;
            
            bloc.setX(newX);
            bloc.setY(newY);
        }
    }
    
    private void reinitialiserAnchages()
    {
        for (LiaisonVue liaison : liaisons) {
            // Réinitialiser les côtés selon la position relative des blocs
            optimiserAncragesPourLiaison(liaison);
        }
    }
    
    private void optimiserAncragesPourLiaison(LiaisonVue liaison)
    {
        BlocClasse orig = liaison.getBlocOrigine();
        BlocClasse dest = liaison.getBlocDestination();
        
        if (orig == null || dest == null) {
            return;
        }
        
        // Calculer les positions relatives
        int origX = orig.getX() + orig.getLargeur() / 2;
        int origY = orig.getY() + orig.getHauteurCalculee() / 2;
        int destX = dest.getX() + dest.getLargeur() / 2;
        int destY = dest.getY() + dest.getHauteurCalculee() / 2;
        
        // Déterminer le meilleur côté pour l'origine
        int sideOrigine = determinerMeilleurCote(origX, origY, destX, destY);
        liaison.setSideOrigine(sideOrigine);
        
        // Déterminer le meilleur côté pour la destination (généralement opposé)
        int sideDestination = determinerMeilleurCoteDestination(origX, origY, destX, destY);
        liaison.setSideDestination(sideDestination);
    }
    
    /**
     * Détermine le meilleur côté de sortie pour l'origine
     * 0=DROITE, 1=BAS, 2=GAUCHE, 3=HAUT
     */
    private int determinerMeilleurCote(int origX, int origY, int destX, int destY)
    {
        int dx = destX - origX;
        int dy = destY - origY;
        
        // Déterminer la direction principale
        if (Math.abs(dx) > Math.abs(dy)) {
            // Direction horizontale
            return dx > 0 ? 0 : 2; // DROITE ou GAUCHE
        } else {
            // Direction verticale
            return dy > 0 ? 1 : 3; // BAS ou HAUT
        }
    }
    
    private int determinerMeilleurCoteDestination(int origX, int origY, int destX, int destY)
    {
        int dx = destX - origX;
        int dy = destY - origY;
        
        // Déterminer la direction principale et prendre le côté opposé
        if (Math.abs(dx) > Math.abs(dy)) {
            // Direction horizontale
            return dx > 0 ? 2 : 0; // GAUCHE ou DROITE (opposé du départ)
        } else {
            // Direction verticale
            return dy > 0 ? 3 : 1; // HAUT ou BAS (opposé du départ)
        }
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
            bloc.dessiner(g2d, this.afficherAttributs, this.afficherMethodes);
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
    
    public void setAfficherMethodes(boolean b) 
    {
        this.afficherMethodes = b;
        this.repaint();
    }

    public void setAfficherAttributs(boolean b) 
    {
        this.afficherAttributs = b;
        this.repaint();
    }
}
