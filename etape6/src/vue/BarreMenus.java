package vue;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
* Barre de menu du haut de la fenêtre principale. 
* Contient tous les accès aux outils d'affichage, édition, fichiers
* @author Jules
*/
public class BarreMenus extends JMenuBar 
{
    //-------------------------//
    //       ATTRIBUTS        //
    //------------------------//

    private FenetrePrincipale fenetrePrincipale    ;

    private JCheckBoxMenuItem afficherAttributsItem;
    private JCheckBoxMenuItem afficherMethodesItem ;
    private JCheckBoxMenuItem sauvegardeAutoItem   ;
    
    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//
    
    public BarreMenus(FenetrePrincipale fenetrePrincipale) 
    {
        add(creerMenuFichier  ());
        //add(creerMenuEdition());
        add(creerMenuAffichage());
        add(creerMenuAide     ());

        this.fenetrePrincipale = fenetrePrincipale;
    }

    //----------------------//
    //      METHODES        //
    //----------------------//
    
    private JMenu creerMenuFichier() 
    {
        JMenu menu = new JMenu("Fichier");

        JMenuItem ouvrirItem      = new JMenuItem("Ouvrir projet...");
        ouvrirItem.     addActionListener(e -> actionOuvrirProjet());

        JMenuItem exporterItem    = new JMenuItem("Exporter en image");
        exporterItem.   addActionListener(e -> fenetrePrincipale.exporterImageDiagramme());

        JMenuItem sauvegarderItem = new JMenuItem("Sauvegarder");
        sauvegarderItem.addActionListener(e -> actionSauvegarder());
        
        sauvegardeAutoItem        = new JCheckBoxMenuItem("Sauvegarde automatique", false);
        sauvegardeAutoItem.  addActionListener(e -> actionSauvegardeAuto());

        JMenuItem quitterItem     = new JMenuItem("Quitter");
        quitterItem.    addActionListener(e -> System.exit(0));

        menu.add(ouvrirItem);
        menu.addSeparator();
        menu.add(exporterItem);
        menu.add(sauvegarderItem);
        //menu.add(sauvegardeAutoItem);
        menu.addSeparator();
        menu.add(quitterItem);

        return menu;
    }

    private JMenu creerMenuAffichage() 
    {
        JMenu menu = new JMenu("Affichage");

        afficherAttributsItem           = new JCheckBoxMenuItem("Afficher attributs", true);
        afficherMethodesItem            = new JCheckBoxMenuItem("Afficher méthodes", true);
        JMenuItem optimiserItem         = new JMenuItem("Optimiser les positions");
        JMenuItem optimiserLiaisonsItem = new JMenuItem("Optimiser les liaisons uniquement");

        afficherAttributsItem. addActionListener(e -> actionAffichageAttributs());
        afficherMethodesItem.  addActionListener(e -> actionAffichageMethodes ());
        optimiserItem.         addActionListener(e -> actionOptimiser         ());
        optimiserLiaisonsItem. addActionListener(e -> actionOptimiserLiaisons ());

        menu.add(afficherAttributsItem);
        menu.add(afficherMethodesItem);
        menu.addSeparator();
        menu.add(optimiserItem);
        menu.add(optimiserLiaisonsItem);

        return menu;
    }

    private JMenu creerMenuAide() 
    {
        JMenu menu              = new JMenu("Aide");

        JMenuItem aProposItem   = new JMenuItem("À propos");
        aProposItem.addActionListener(e -> actionAPropos());

        menu.add(aProposItem);

        return menu;
    }
        
    private void actionOuvrirProjet() 
    {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // boite de dialogue
        int resultat = chooser.showOpenDialog(null); // null = centré à l'écran

        // Si l'utilisateur a choisi un fichier
        if (resultat == JFileChooser.APPROVE_OPTION) 
        {
            File fichierSelectionne = chooser.getSelectedFile();
            String cheminAbsolu     = fichierSelectionne.getAbsolutePath();

            verifierFichiersProjet(cheminAbsolu);
            
            sauvegardeProjetXml(cheminAbsolu);
        } 
        else 
        {
            System.out.println("Aucun fichier choisi");
        }    
    }

    private void verifierFichiersProjet(String cheminFichier)
    {
        ArrayList<String> lstFichiersInvalides = fenetrePrincipale.getLstFichiersInvalides(cheminFichier);
        String messageInvalide = lstFichiersInvalides.size() == 1
               ? "Attention, fichier non valide detectés :\n ( "
               : "Attention, fichiers non valides detectés :\n ( ";

        if (lstFichiersInvalides.isEmpty()) return;

        for(String fichierInvalide : lstFichiersInvalides)
        {
            messageInvalide += fichierInvalide + ", ";
        }
        // Retirer la derniere virgule
        messageInvalide = messageInvalide.substring(0, messageInvalide.length() - 2);
        messageInvalide += " )";

        JOptionPane.showMessageDialog(null, messageInvalide, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private void sauvegardeProjetXml(String cheminFichier) 
    {
        fenetrePrincipale.ouvrirProjet(cheminFichier);
    }

    private void actionAffichageAttributs() 
    {
        fenetrePrincipale.affichageAttributs(afficherAttributsItem.getState());
    }

    private void actionAffichageMethodes() 
    {
        fenetrePrincipale.affichageMethodes(afficherMethodesItem.getState());
    }

    private void actionSauvegardeAuto() 
    {
        fenetrePrincipale.setSauvegardeAuto(sauvegardeAutoItem.getState());
    }

    private void actionOptimiser() 
    {
        fenetrePrincipale.optimiserPositionsClasses ();
        fenetrePrincipale.optimiserPositionsLiaisons();
    }

    private void actionOptimiserLiaisons() 
    {
        fenetrePrincipale.optimiserPositionsLiaisons();
    }
    
    private void actionSauvegarder() 
    {
        this.fenetrePrincipale.actionSauvegarder();
    }

private void actionAPropos()
{
    String htmlMessage =
        "<html>"
      + "<body style='font-family:Arial; font-size:12pt;'>"

      + "<div style='text-align:center; font-weight:bold; font-size:14pt;'>"
      + "Modélisation UML – Générateur de Diagrammes"
      + "</div>"

      + "<hr><br>"

      + "<div style='color:#DC143C;'><u>AUTEURS</u> :</div><br>"

      + "<pre style='color:#228B22;'>Hugo     VARAO GOMES DA SILVA</pre>"
      + "<pre style='color:#FF1493;'>Romain   BARUCHELLO</pre>"
      + "<pre style='color:#FF8C00;'>Jules    BOUQUET</pre>"
      + "<pre style='color:#0000FF;'>Pierre   COIGNARD</pre>"
      + "<pre style='color:#800080;'>Paul     NOEL</pre>"
      + "<pre style='color:#DC143C;'>Thibaul  PADOIS</pre>"

      + "<br><hr>"

      + "<div style='text-align:center;'>Projet académique – IUT du Havre</div>"
      + "<div style='text-align:center;'>SAE 3.01 – Outil de rétroconception Java-UML</div>"

      + "</body></html>";

    JOptionPane.showMessageDialog(null,htmlMessage,"À propos",
                                JOptionPane.INFORMATION_MESSAGE);
}



}