package vue;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import vue.liaison.LiaisonVue;

public class PanneauDiagramme extends JPanel 
{
    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

    private List<BlocClasse>    blocsClasses;
    private List<LiaisonVue>    liaisons;

    private FenetrePrincipale   fenetrePrincipale;

    private String              cheminProjetCourant;
    private BlocClasse          blocEnDeplacement;
    private Point               pointDernier;

    private boolean             afficherAttributs = true;
    private boolean             afficherMethodes = true;

    // Pour le drag des points d'ancrage de liaisons
    private LiaisonVue          liaisonEnDeplacement;
    private boolean             draggingOriginAnchor;
    private boolean             draggingDestinationAnchor;

    // Zoom
    private double              zoomLevel = 1.0;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 3.0;
    private static final double ZOOM_STEP = 0.1;
    private boolean             afficherTextZoom = true;
    
    // Pan
    private int                 panOffsetX = 0;
    private int                 panOffsetY = 0;
    private boolean             isPanning = false;
    private BlocClasse          blocPleinEcranTemporaire = null;


    private JPopupMenu          menuModif; 
    private JMenuItem menuChangerMultiplicite;

    ;


    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//

    public PanneauDiagramme(FenetrePrincipale fenetrePrincipale) 
    {
        this.blocsClasses           = new ArrayList<>();
        this.liaisons               = new ArrayList<>();
        this.cheminProjetCourant    = null;
        this.fenetrePrincipale      = fenetrePrincipale;

        this.menuModif                = new JPopupMenu();

        this.menuChangerMultiplicite      = new JMenuItem("Changer la multiplicité");

       

        this.menuChangerMultiplicite.addActionListener(ActionEvent -> {
            FenetreChangementMultiplicite fenetreChangementMultiplicite = new FenetreChangementMultiplicite();
            fenetreChangementMultiplicite.setVisible(true);
        });

        this.menuModif.add(this.menuChangerMultiplicite);
        setLayout(null);
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createTitledBorder("Diagramme UML"));
        
        // Augmenter la taille de la police par défaut pour la bordure
        Font defaultFont = UIManager.getFont("TitledBorder.font");
        if (defaultFont != null) {
            UIManager.put("TitledBorder.font", new Font(defaultFont.getName(), Font.PLAIN, 14));
        }

        ajouterListenersInteraction();
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    public void chargerProjet(String cheminProjet) throws Exception
    {
        this.cheminProjetCourant = cheminProjet;
        this.blocsClasses.clear();
        this.liaisons.clear();

        List<BlocClasse> blocCharges = fenetrePrincipale.chargerProjetEnBlocsClasses(cheminProjet);
        blocsClasses.addAll(blocCharges);
        liaisons.addAll(fenetrePrincipale.getLiaisons());

        // Passer la liste des blocs à toutes les liaisons pour le contournement
        for (LiaisonVue liaison : liaisons) {
            liaison.setTousLesBlocs(blocsClasses);
        }

        repaint();
    }

    private void ajouterListenersInteraction() {
        addMouseListener(new MouseAdapter() {

            boolean blocValide = false;
            @Override
            
            public void mousePressed(MouseEvent e) {
                // Vérifier si on clique sur le texte de zoom pour le réinitialiser
                // Zone cliquable : bas-gauche de la fenêtre
                int padding = 10;
                int textHeight = 25;
                if (e.getX() >= padding && e.getX() <= padding + 100 && 
                    e.getY() >= getHeight() - padding - textHeight && e.getY() <= getHeight() - padding) {
                    zoomLevel = 1.0;
                    panOffsetX = 0;
                    panOffsetY = 0;
                    repaint();
                    return;
                }
                
                // Convertir les coordonnées écran en coordonnées logiques (avec zoom et pan)
                double logicalX = (e.getX() - panOffsetX - getWidth() / 2) / zoomLevel + getWidth() / (2 * zoomLevel);
                double logicalY = (e.getY() - panOffsetY - getHeight() / 2) / zoomLevel + getHeight() / (2 * zoomLevel);

                // Clic-droit : affichage plein écran d'une classe ou pan
                if (e.getButton() == MouseEvent.BUTTON3 )
                {
                   // System.out.println("Le clic droit a été cliqué");
                    // Chercher si on clique sur un bloc
                    BlocClasse blocClique = null;
                    for (BlocClasse bloc : blocsClasses) {
                        if (bloc.contient((int) logicalX, (int) logicalY)) 
                        {
                            blocClique = bloc;
                            blocValide = true;
                            System.out.println("Bloc cliqué : " + bloc.getNom());
                            break;
                        }

                    }


                if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2)
                {
                    if (blocValide)
                    //System.out.println("Double clique droit effectué");
                        PanneauDiagramme.this.menuModif.show(e.getComponent(), e.getX(), e.getY());
                }

                    

                
                


                    
                    // Si on clique sur un bloc, activer l'affichage plein écran temporairement
                    if (blocClique != null) {
                        blocPleinEcranTemporaire = blocClique;
                        blocClique.setAffichagePleinEcran(true);
                        repaint();
                        return;
                    }
                    
                    // Sinon, commencer le pan
                    isPanning = true;
                    pointDernier = e.getPoint();
                    return;
                }

                pointDernier = e.getPoint();
                blocEnDeplacement = null;
                liaisonEnDeplacement = null;
                draggingOriginAnchor = false;
                draggingDestinationAnchor = false;

                // Vérifier si on clique sur un point d'ancrage de liaison
                for (LiaisonVue liaison : liaisons) {
                    if (liaison.isOnOriginAnchor(e.getPoint(), zoomLevel, panOffsetX, panOffsetY, getWidth(), getHeight())) {
                        liaisonEnDeplacement = liaison;
                        draggingOriginAnchor = true;
                        return;
                    }
                    if (liaison.isOnDestinationAnchor(e.getPoint(), zoomLevel, panOffsetX, panOffsetY, getWidth(), getHeight())) {
                        liaisonEnDeplacement = liaison;
                        draggingDestinationAnchor = true;
                        return;
                    }
                }

                // Sinon, vérifier si on clique sur un bloc
                for (BlocClasse bloc : blocsClasses) {
                    if (bloc.contient((int) logicalX, (int) logicalY)) {
                        blocEnDeplacement = bloc;
                        bloc.setSelectionne(true);
                        break;
                    }
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isPanning) {
                    isPanning = false;
                    repaint();
                    return;
                }
                
                // Désactiver l'affichage plein écran temporaire
                if (blocPleinEcranTemporaire != null) {
                    blocPleinEcranTemporaire.setAffichagePleinEcran(false);
                    blocPleinEcranTemporaire = null;
                    repaint();
                }
                
                if (blocEnDeplacement != null) {
                    blocEnDeplacement.setSelectionne(false);
                }
                blocEnDeplacement = null;
                liaisonEnDeplacement = null;
                draggingOriginAnchor = false;
                draggingDestinationAnchor = false;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Pan avec clic droit
                if (isPanning) {
                    int newPanX = panOffsetX + (e.getX() - pointDernier.x);
                    int newPanY = panOffsetY + (e.getY() - pointDernier.y);
                    
                    // Empêcher le pan d'aller dans le négatif
                    panOffsetX = Math.min(0, newPanX);
                    panOffsetY = Math.min(0, newPanY);
                    
                    pointDernier = e.getPoint();
                    repaint();
                    return;
                }
                
                // Drag d'un point d'ancrage de liaison
                if (liaisonEnDeplacement != null) {
                    if (draggingOriginAnchor) {
                        liaisonEnDeplacement.dragOriginAnchor(e.getPoint(), zoomLevel, panOffsetX, panOffsetY, getWidth(), getHeight());
                    } else if (draggingDestinationAnchor) {
                        liaisonEnDeplacement.dragDestinationAnchor(e.getPoint(), zoomLevel, panOffsetX, panOffsetY, getWidth(), getHeight());
                    }
                    repaint();
                    return;
                }

                // Drag d'un bloc
                if (blocEnDeplacement != null) {
                    // Convertir les déplacements en fonction du zoom
                    double dx = (e.getX() - pointDernier.x) / zoomLevel;
                    double dy = (e.getY() - pointDernier.y) / zoomLevel;

                    // Vérifier les limites pour éviter un déplacement hors zone
                    int newX = blocEnDeplacement.getX() + (int) dx;
                    int newY = blocEnDeplacement.getY() + (int) dy;

                    // Empêcher le déplacement hors de la zone de travail
                    if (newX >= 0 && newY >= 0) {
                        blocEnDeplacement.deplacer((int) dx, (int) dy);
                        pointDernier = e.getPoint();
                    } else if (newX >= 0 && newY < 0) {
                        blocEnDeplacement.deplacer((int) dx, 0);
                        pointDernier = e.getPoint();
                    } else if (newX < 0 && newY >= 0) {
                        blocEnDeplacement.deplacer(0, (int) dy);
                        pointDernier = e.getPoint();
                    }

                    repaint();
                }
            }
        });

        // Listener pour la molette de souris (zoom)
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    double oldZoom = zoomLevel;
                    zoomLevel -= e.getWheelRotation() * ZOOM_STEP;
                    zoomLevel = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomLevel));

                    if (zoomLevel != oldZoom) {
                        repaint();
                    }
                }
            }
        });
    }

    public void optimiserPositionsClasses() {
        if (blocsClasses.isEmpty()) {
            return;
        }

        // organiser les blocs en grille
        organiserEnGrille();

        // Étape 4 : Redessiner
        repaint();

        System.out.println("Opti pos réalisée");
    }

    public void optimiserPositionsLiaisons() {
        if (liaisons.isEmpty()) {
            return;
        }

        // D'abord, passer la liste de toutes les liaisons à chaque liaison
        for (LiaisonVue liaison : liaisons) {
            liaison.setToutesLesLiaisons(liaisons);
        }
        
        // Réinitialiser toutes les liaisons avec le nouvel algorithme
        for (LiaisonVue liaison : liaisons) {
            liaison.recalculerAncrages();
        }

        // Redessiner
        repaint();
    }

    /**
     * Organise les blocs en une grille régulière avec espacement optimal
     */
    private void organiserEnGrille() {
        int cols = (int) Math.ceil(Math.sqrt(blocsClasses.size()));
        int spacingX = 275; // Espacement entre les blocs
        int spacingY = 275;
        int startX = 50;
        int startY = 50;

        for (BlocClasse bloc : blocsClasses) {
            if (spacingY < bloc.getHauteurCalculee()) {
                spacingY = bloc.getHauteurCalculee();
            }

        }

        for (int i = 0; i < blocsClasses.size(); i++) {
            BlocClasse bloc = blocsClasses.get(i);
            int col = i % cols;
            int row = i / cols;

            int newX = startX + col * spacingX;
            int newY = startY + row * spacingY;

            bloc.setX(newX);
            bloc.setY(newY);
        }
    }

    private void reinitialiserAnchages() {
        for (LiaisonVue liaison : liaisons) {
            // Réinitialiser les côtés selon la position relative des blocs
            optimiserAncragesPourLiaison(liaison);
        }
    }

    private void optimiserAncragesPourLiaison(LiaisonVue liaison) {
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
    private int determinerMeilleurCote(int origX, int origY, int destX, int destY) {
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

    private int determinerMeilleurCoteDestination(int origX, int origY, int destX, int destY) {
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

        // Appliquer le pan offset
        g2d.translate(panOffsetX, panOffsetY);
        
        // Appliquer le zoom
        g2d.translate(getWidth() / 2, getHeight() / 2);
        g2d.scale(zoomLevel, zoomLevel);
        g2d.translate(-getWidth() / (2 * zoomLevel), -getHeight() / (2 * zoomLevel));

        // Mettre à jour la liste de toutes les liaisons pour détecter les intersections
        for (LiaisonVue liaison : liaisons) {
            liaison.setToutesLesLiaisons(liaisons);
        }
        
        // Dessiner les liaisons
        dessinerLiaisons(g2d);

        // Dessiner les blocs
        for (BlocClasse bloc : blocsClasses) {
            bloc.dessiner(g2d, this.afficherAttributs, this.afficherMethodes);
        }

        // Afficher le pourcentage de zoom
        afficherZoomPercentage(g2d);
    }

    private void afficherZoomPercentage(Graphics2D g2d) {
        if (!afficherTextZoom) {
            return;
        }

        // Réinitialiser la transformation pour afficher le texte sans zoom
        g2d.setTransform(new java.awt.geom.AffineTransform());

        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        
        String zoomText = String.format("Zoom: %d%%", (int) (zoomLevel * 100));
        int textWidth = fm.stringWidth(zoomText);
        int textHeight = fm.getAscent();
        
        // Positionner le texte en bas à gauche avec du padding
        int padding = 10;
        int x = padding;
        int y = getHeight() - padding;
        
        g2d.drawString(zoomText, x, y);
    }

    private void dessinerLiaisons(Graphics2D g2d) 
    {
        for (LiaisonVue liaison : liaisons) 
        {
            liaison.dessiner(g2d);

            System.out.println("liaison : " + liaison);
        }
    }

    public void modifiMultiplicite(int min, int max)
    {
                
    }

    public List<BlocClasse> getBlocsClasses() {
        return blocsClasses;
    }

    public String getCheminProjetCourant() {
        return cheminProjetCourant;
    }

    public void setAfficherMethodes(boolean b) {
        this.afficherMethodes = b;
        this.repaint();
    }

    public void setAfficherAttributs(boolean b) {
        this.afficherAttributs = b;
        this.repaint();
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoom) {
        this.zoomLevel = zoom;
        this.repaint();
    }

    public boolean isAfficherTextZoom() {
        return afficherTextZoom;
    }

    public void setAfficherTextZoom(boolean afficher) {
        this.afficherTextZoom = afficher;
        this.repaint();
    }

    public void actionSauvegarder() {
        this.fenetrePrincipale.sauvegarderClasses(this.blocsClasses, cheminProjetCourant);
    }



   
}
