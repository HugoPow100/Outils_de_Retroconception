package vue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
* L'affichage des blocs de Classe dans le diagramme UML
* @author Jules
*/
public class BlocClasse 
{
    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

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
    private boolean affichagePleinEcran;

    // Constantes 
    private static final int PADDING           = 10;
    private static final int HAUTEUR_ENTETE    = 30;
    private static final int HAUTEUR_LIGNE     = 20;
    private static final int MAX_ATTRIBUTS     = 3;
    private static final int MAX_METHODES      = 2;
    private static final int MAX_PARAMETRES    = 2;
    private static final Color COULEUR_FOND    = new Color(230, 240, 250);
    private static final Color COULEUR_BORDURE = new Color(0, 0, 0);
    private static final Color COULEUR_ENTETE  = new Color(100, 150, 200);


    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//
    
    public BlocClasse(String nom, int x, int y) 
    {
        this.nom = nom;
        this.x   = x;
        this.y   = y;
        this.largeur = 200;
        this.hauteur = 150;
        this.estInterface   = false;
        this.estSelectionne = false;
        this.affichagePleinEcran = false;
        this.attributs = new ArrayList<String>();
        this.methodes  = new ArrayList<String>();
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    /**
     * Formate le texte d'une méthode en wrappant sur plusieurs lignes si nécessaire
     * @param methodeStr Chaîne de méthode au format: "visibilite nomMethode(param1, param2, ...) : typeRetour"
     * @param maxWidth Largeur maximale disponible en pixels
     * @param g2d Graphics2D pour mesurer la largeur du texte
     * @return Chaîne formatée avec retours à la ligne si dépasse maxWidth
     */
    private String formatMethodeAvecLargeur(String methodeStr, int maxWidth, Graphics2D g2d) 
    {
        FontMetrics fm = g2d.getFontMetrics();
        
        // Si rentre sur une ligne, retourner tel quel
        if (fm.stringWidth(methodeStr) <= maxWidth) {
            return methodeStr;
        }
        
        // Si on est en mode plein écran, afficher tous les paramètres avec wrapping
        if (affichagePleinEcran) {
            int closeParen = methodeStr.indexOf(")");
            if (closeParen > 0) {
                String firstPart = methodeStr.substring(0, closeParen + 1);
                String secondPart = methodeStr.substring(closeParen + 1);
                return firstPart + "\n" + secondPart;
            }
            return methodeStr;
        }
        
        // En mode condensé, limiter aussi les paramètres lors du wrapping
        int closeParen = methodeStr.indexOf(")");
        if (closeParen <= 0) {
            return methodeStr;
        }
        
        String firstPart = methodeStr.substring(0, closeParen + 1);
        String secondPart = methodeStr.substring(closeParen + 1);
        
        // Vérifier que les deux parties rentrent dans la largeur
        // Si la première partie est trop longue, on coupe aussi les paramètres
        if (fm.stringWidth(firstPart) > maxWidth) {
            // Tronquer les paramètres
            int openParen = methodeStr.indexOf("(");
            if (openParen > 0) {
                String signature = methodeStr.substring(0, openParen + 1);
                String params = methodeStr.substring(openParen + 1, closeParen);
                String[] paramList = params.split(",");
                
                // Garder seulement les 2 premiers paramètres
                if (paramList.length > 2) {
                    StringBuilder truncated = new StringBuilder(signature);
                    for (int i = 0; i < 2; i++) {
                        truncated.append(paramList[i].trim());
                        if (i < 1) truncated.append(", ");
                    }
                    truncated.append(", ...");
                    truncated.append(")");
                    truncated.append(secondPart);
                    firstPart = truncated.toString();
                }
            }
        }
        
        return firstPart + "\n" + secondPart;
    }

    /**
     * Formate le texte d'une méthode en limitant les paramètres en mode condensé
     * @param methodeStr Chaîne de méthode au format: "visibilite nomMethode(param1, param2, ...) : typeRetour"
     * @return Chaîne formatée avec max 2 paramètres si mode condensé
     */
    private String formatMethode(String methodeStr) 
    {
        // En mode condensé, limiter les paramètres à 2
        if (!affichagePleinEcran) {
            int startParen = methodeStr.indexOf("(");
            int endParen = methodeStr.indexOf(")");
            
            if (startParen > 0 && endParen > startParen) {
                String avant = methodeStr.substring(0, startParen + 1);
                String apres = methodeStr.substring(endParen);
                String parametres = methodeStr.substring(startParen + 1, endParen);
                
                if (!parametres.trim().isEmpty()) {
                    String[] params = parametres.split(",");
                    
                    if (params.length > MAX_PARAMETRES) {
                        StringBuilder result = new StringBuilder(avant);
                        for (int i = 0; i < MAX_PARAMETRES; i++) {
                            result.append(params[i].trim());
                            if (i < MAX_PARAMETRES - 1) {
                                result.append(", ");
                            }
                        }
                        result.append(", ...");
                        result.append(apres);
                        return result.toString();
                    }
                }
            }
        }
        
        return methodeStr;
    }

    /**
     * Obtient la liste des attributs à afficher selon le mode d'affichage
     */
    private List<String> getAttributsAffichage() 
    {
        if (affichagePleinEcran) {
            return attributs;
        }
        
        List<String> result = new ArrayList<>();
        
        if (attributs.size() <= MAX_ATTRIBUTS) {
            return attributs;
        }
        
        for (int i = 0; i < MAX_ATTRIBUTS; i++) {
            result.add(attributs.get(i));
        }
        result.add("...");
        
        return result;
    }

    /**
     * Obtient la liste des méthodes à afficher selon le mode d'affichage
     */
    private List<String> getMethodesAffichage() 
    {
        if (affichagePleinEcran) {
            return methodes;
        }
        
        List<String> result = new ArrayList<>();
        
        if (methodes.size() <= MAX_METHODES) {
            // Formater les méthodes pour limiter les paramètres
            for (String methode : methodes) {
                result.add(formatMethode(methode));
            }
            return result;
        }
        
        for (int i = 0; i < MAX_METHODES; i++) {
            result.add(formatMethode(methodes.get(i)));
        }
        result.add("...");
        
        return result;
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
        
        int textY = y + HAUTEUR_ENTETE - (HAUTEUR_ENTETE - fm.getAscent()) / 2;
        
        // Si c'est une interface, afficher <<interface>> au-dessus du nom
        if (estInterface) {
            g.setFont(new Font("Arial", Font.ITALIC, 10));
            FontMetrics fmSmall = g.getFontMetrics();
            String interfaceLabel = "<<interface>>";
            int textXInterface = x + (largeur - fmSmall.stringWidth(interfaceLabel)) / 2;
            g.drawString(interfaceLabel, textXInterface, textY - 8);
            
            // Afficher le nom en-dessous
            g.setFont(new Font("Arial", Font.BOLD, 12));
            fm = g.getFontMetrics();
            textY = y + HAUTEUR_ENTETE - (HAUTEUR_ENTETE - fm.getAscent()) / 2 + 5;
        }
        
        int textX = x + (largeur - fm.stringWidth(nom)) / 2;
        g.drawString(nom, textX, textY);

        int yActuel = y + HAUTEUR_ENTETE + PADDING;

        // Utiliser les méthodes de formatage pour l'affichage
        List<String> attributsAffichage = getAttributsAffichage();
        List<String> methodesAffichage = getMethodesAffichage();

        if (!attributsAffichage.isEmpty()) 
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            FontMetrics fmAttributs = g.getFontMetrics();

            if (afficherAttributs)
            {
                for (String att : attributsAffichage) 
                {
                    // pour gérer les soulignement
                    // Vérifier si l'attribut contient un code ANSI de soulignement
                    boolean isSouligne = att.contains("\u001B[4m");
                    
                    // Retirer les codes ANSI pour l'affichage
                    String displayText = att.replace("\u001B[4m", "").replace("\u001B[0m", "");
                    
                    
                    g.drawString(displayText, x + PADDING, yActuel);
                    
                    // Si l'attribut doit être souligné, tracer une ligne sous le texte
                    if (isSouligne) {
                        int textWidth = fmAttributs.stringWidth(displayText);
                        int underlineY = yActuel + 2;
                        g.drawLine(x + PADDING, underlineY, x + PADDING + textWidth, underlineY);
                    }
                    
                    yActuel += HAUTEUR_LIGNE;
                }
                
            } 
            else 
            {
                g.drawString("...", x + PADDING, yActuel);
                yActuel += HAUTEUR_LIGNE;
            }
        }

        if (!attributsAffichage.isEmpty() && !methodesAffichage.isEmpty()) 
        {
            g.setColor(COULEUR_BORDURE);
            g.drawLine(x + PADDING, yActuel - 5, x + largeur - PADDING, yActuel - 5);
            yActuel += 5;
        }

        if (!methodesAffichage.isEmpty()) 
        {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            Graphics2D g2d = (Graphics2D) g;

            if (afficherMethodes)
            {
                int maxMethodeWidth = largeur - 2 * PADDING;
                for (String met : methodesAffichage) 
                {
                    // Afficher sur plusieurs lignes si la méthode contient des retours à la ligne
                    String[] lines = met.split("\n");
                    for (String line : lines) {
                        String methodeFormatee = formatMethodeAvecLargeur(line, maxMethodeWidth, g2d);
                        g.drawString(methodeFormatee, x + PADDING, yActuel);
                        yActuel += HAUTEUR_LIGNE;
                    }
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
        List<String> attributsAffichage = getAttributsAffichage();
        List<String> methodesAffichage = getMethodesAffichage();
        
        int h = HAUTEUR_ENTETE + PADDING;
        
        // Ajouter de l'espace pour <<interface>> si nécessaire
        if (estInterface) {
            h += 10;
        }
        
        h += attributsAffichage.size() * HAUTEUR_LIGNE;

        if (!attributsAffichage.isEmpty() && !methodesAffichage.isEmpty()) 
        {
            h += 10;
        }

        // Calculer la hauteur des méthodes en tenant compte des retours à la ligne
        for (String methode : methodesAffichage) {
            int nbLignes = 1;
            for (char c : methode.toCharArray()) {
                if (c == '\n') {
                    nbLignes++;
                }
            }
            h += nbLignes * HAUTEUR_LIGNE;
        }
        
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

    public List<String> getAttributs() 
    {
        return attributs;
    }

    public void setMethodesAffichage(List<String> methodes) 
    {
        this.methodes = methodes;
    }

    public List<String> getMethodes() 
    {
        return methodes;
    }

    public boolean isAffichagePleinEcran() 
    {
        return affichagePleinEcran;
    }

    public void setAffichagePleinEcran(boolean affichagePleinEcran) 
    {
        this.affichagePleinEcran = affichagePleinEcran;
    }
}
