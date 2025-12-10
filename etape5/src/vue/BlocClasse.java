package vue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlocClasse 
{

    private String nom;
    private boolean estInterface;
    private boolean estSelectionne;
    private List<String> attributs;
    private List<String> methodes;

    private int x;
    private int y;
    private int largeur;
    private int hauteur;

    private int hauteurCalculee;

    // Constantes 
    private static final int PADDING           = 10;
    private static final int HAUTEUR_ENTETE    = 30;
    private static final int HAUTEUR_LIGNE     = 20;
    private static final Color COULEUR_FOND    = new Color(230, 240, 250);
    private static final Color COULEUR_BORDURE = new Color(0, 0, 0);
    private static final Color COULEUR_ENTETE  = new Color(100, 150, 200);

    public BlocClasse(String nom, int x, int y) 
    {
        this.nom = nom;
        this.x   = x;
        this.y   = y;
        this.largeur = 200;
        this.hauteur = 150;
        this.estInterface   = false;
        this.estSelectionne = false;
        this.attributs = new ArrayList<>();
        this.methodes  = new ArrayList<>();
    }

    public void dessiner(Graphics2D g, boolean afficherAttributs, boolean afficherMethodes) 
    {
        hauteurCalculee = calculerHauteur();
        
        g.setColor(COULEUR_FOND);
        g.fillRect(x, y, largeur, hauteurCalculee);

        g.setColor(estSelectionne ? Color.BLUE : COULEUR_BORDURE);
        g.setStroke(new BasicStroke(estSelectionne ? 2 : 1)); // sert à définir l'apparence du trait
        g.drawRect(x, y, largeur, hauteurCalculee);

        g.setColor(COULEUR_ENTETE);
        g.fillRect(x, y, largeur, HAUTEUR_ENTETE); 

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (largeur - fm.stringWidth(nom)) / 2;
        int textY = y + HAUTEUR_ENTETE - (HAUTEUR_ENTETE - fm.getAscent()) / 2;
        g.drawString(nom, textX, textY);

        int yActuel = y + HAUTEUR_ENTETE + PADDING;

        if (!attributs.isEmpty()) 
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));

            if (afficherAttributs)
            {
                for (String att : attributs) 
                {
                    g.drawString(att, x + PADDING, yActuel);
                    yActuel += HAUTEUR_LIGNE;
                }
                
            } 
            else 
            {
                g.drawString("...", x + PADDING, yActuel);
                yActuel += HAUTEUR_LIGNE;
            }
        }

        if (!attributs.isEmpty() && !methodes.isEmpty()) 
        {
            g.setColor(COULEUR_BORDURE);
            g.drawLine(x + PADDING, yActuel - 5, x + largeur - PADDING, yActuel - 5);
            yActuel += 5;
        }

        if (!methodes.isEmpty()) 
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));

            if (afficherMethodes)
            {
                for (String met : methodes) 
                {
                    g.drawString(met, x + PADDING, yActuel);
                    yActuel += HAUTEUR_LIGNE;
                }
            }
            else 
            {
                g.drawString("...", x + PADDING, yActuel);
                    yActuel += HAUTEUR_LIGNE;
            }
        }
    }

    private int calculerHauteur() 
    {
        int h = HAUTEUR_ENTETE + PADDING;
        h    += attributs.size() * HAUTEUR_LIGNE;

        if (!attributs.isEmpty() && !methodes.isEmpty()) 
        {
            h += 10;
        }

        h += methodes.size() * HAUTEUR_LIGNE;
        h += PADDING;

        return Math.max(h, 80);
    }

    // Vérifie si le point donné est dans le bloc
    public boolean contient(int px, int py) 
    {
        int hauteurCalculee = calculerHauteur();

        return px >= x                    &&
               px <= x + largeur          &&
               py >= y                    &&
               py <= y + hauteurCalculee;
    }

    // Vérifie si un rectangle de texte chevauchent le bloc
    public boolean chevaucheTexte(int textX, int textY, int textWidth, int textHeight) 
    {
        int hauteurCalculee = calculerHauteur();
        
        // Vérifier si le rectangle du texte chevauche le rectangle du bloc
        return !(textX + textWidth < x || 
                 textX > x + largeur || 
                 textY > y + hauteurCalculee || 
                 textY - textHeight < y);
    }

    public void deplacer(int dx, int dy) 
    {
        this.x += dx;
        this.y += dy;
    }

    // Getters et Setters
    public String getNom() 
    {
        return nom;
    }

    public int getX() 
    {
        return x;
    }

    public void setX(int x) 
    {
        this.x = x;
    }

    public int getY() 
    {
        return y;
    }

    public void setY(int y) 
    {
        this.y = y;
    }

    public int getLargeur() 
    {
        return largeur;
    }

    public int getHauteur() 
    {
        return hauteur;
    }

    public int getHauteurCalculee() 
    {
        return hauteurCalculee;
    }

    public boolean estInterface() 
    {
        return estInterface;
    }

    public void setInterface(boolean estInterface) 
    {
        this.estInterface = estInterface;
    }

    public boolean estSelectionne() 
    {
        return estSelectionne;
    }

    public void setSelectionne(boolean selectionne) 
    {
        estSelectionne = selectionne;
    }

    public void setAttributsAffichage(List<String> attributs) 
    {
        this.attributs = attributs;
    }

    public void setMethodesAffichage(List<String> methodes) 
    {
        this.methodes = methodes;
    }
}
