package vue;

import java.io.File;
import java.io.FileWriter;
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

    private FenetrePrincipale fenetrePrincipale;

    private JCheckBoxMenuItem afficherAttributsItem;
    private JCheckBoxMenuItem afficherMethodesItem;
    
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
        exporterItem.   addActionListener(e -> fenetrePrincipale.sauvegarderDiagramme());

        JMenuItem sauvegarderItem = new JMenuItem("Sauvegarder");
        sauvegarderItem.addActionListener(e -> actionSauvegarder());

        JMenuItem quitterItem     = new JMenuItem("Quitter");
        quitterItem.    addActionListener(e -> System.exit(0));

        menu.add(ouvrirItem);
        menu.addSeparator();
        menu.add(exporterItem);
        menu.add(sauvegarderItem);
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

        afficherAttributsItem.  addActionListener(e -> actionAffichageAttributs());
        afficherMethodesItem.   addActionListener(e -> actionAffichageMethodes());
        optimiserItem.          addActionListener(e -> actionOptimiser());
        optimiserLiaisonsItem.  addActionListener(e -> actionOptimiserLiaisons());

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

    private void verifierFichiersProjet(String cheminDossiers)
    {
        File projet = new File(cheminDossiers);

        String messageInvalide = "Attention : Fichiers non valides detectés.";
        String messageErreur = "\n( ";

        if (projet.isDirectory())
        {
            File[] tabFichiers = projet.listFiles();
            boolean fichierInvalides = false;

            for (File file : tabFichiers)
            {
                if (file.isFile() && !file.getName().endsWith(".java"))
                {
                    messageErreur += file.getName() + ", ";
                    fichierInvalides = true;
                }
            }

            messageErreur += ")";
            if (fichierInvalides)
            {
                JOptionPane.showMessageDialog(this,messageInvalide+messageErreur ,"Format invalide",JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void sauvegardeProjetXml(String cheminFichier) 
    {
        // Déléguer au contrôleur via la fenêtre principale
        fenetrePrincipale.chargerProjet(cheminFichier);
    }

    private void actionAffichageAttributs() 
    {
        fenetrePrincipale.affichageAttributs(afficherAttributsItem.getState());
    }

    private void actionAffichageMethodes() 
    {
        fenetrePrincipale.affichageMethodes(afficherMethodesItem.getState());
    }

    private void actionAligner()
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
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
        String messageDeb = "Modélisation UML - Générateur de Diagrammes\n";
        String messageRes = "par Romain BARUCHELLO,\nJules BOUQUET,\nPierre COIGNARD,\nPaul NOEL,\n";
        String messageFin = messageDeb + messageRes + "Thibault PADOIS,\nHugo VARAO GOMES DA SILVA";

        JOptionPane.showMessageDialog(null, messageFin, "À propos", JOptionPane.INFORMATION_MESSAGE);
    }
}
