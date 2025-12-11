package vue;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class BarreMenus extends JMenuBar 
{

    private FenetrePrincipale fenetrePrincipale;

    private JCheckBoxMenuItem afficherAttributsItem;
    private JCheckBoxMenuItem afficherMethodesItem;
    
    public BarreMenus(FenetrePrincipale fenetrePrincipale) 
    {
        add(creerMenuFichier());
        //add(creerMenuEdition());
        add(creerMenuAffichage());
        add(creerMenuAide());

        this.fenetrePrincipale = fenetrePrincipale;
    }

    private JMenu creerMenuFichier() 
    {
        JMenu menu = new JMenu("Fichier");

        JMenuItem nouvelleItem = new JMenuItem("Nouveau projet ");
        nouvelleItem.addActionListener(e -> actionNouveauProjet());

        JMenuItem ouvrirItem = new JMenuItem("Ouvrir projet...");
        ouvrirItem.addActionListener(e -> actionOuvrirProjet());

        JMenuItem exporterItem = new JMenuItem("Exporter en image");
        exporterItem.addActionListener(e -> fenetrePrincipale.sauvegarderDiagramme());

        JMenuItem sauvegarderItem = new JMenuItem("Sauvegarder");
        sauvegarderItem.addActionListener(e -> actionSauvegarder());

        JMenuItem quitterItem = new JMenuItem("Quitter");
        quitterItem.addActionListener(e -> System.exit(0));

        //menu.add(nouvelleItem);
        menu.add(ouvrirItem);
        /////menu.addSeparator();
        menu.add(exporterItem);
        menu.add(sauvegarderItem);
        menu.addSeparator();
        menu.add(quitterItem);

        return menu;
    }

    private JMenu creerMenuEdition() 
    {
        JMenu menu = new JMenu("Édition");

        JMenuItem annulerItem = new JMenuItem("Annuler");
        JMenuItem retablirItem = new JMenuItem("Rétablir");
        JMenuItem supprimerItem = new JMenuItem("Supprimer");

        annulerItem.addActionListener(e -> actionAnnuler());
        retablirItem.addActionListener(e -> actionRetablir());
        supprimerItem.addActionListener(e -> actionSupprimer());

        menu.add(annulerItem);
        menu.add(retablirItem);
        menu.addSeparator();
        menu.add(supprimerItem);

        return menu;
    }

    private JMenu creerMenuAffichage() 
    {
        JMenu menu = new JMenu("Affichage");

        afficherAttributsItem = new JCheckBoxMenuItem("Afficher attributs", true);
        afficherMethodesItem = new JCheckBoxMenuItem("Afficher méthodes", true);
        JMenuItem optimiserItem = new JMenuItem("Optimiser les positions");
        JMenuItem optimiserLiaisonsItem = new JMenuItem("Optimiser les liaisons uniquement");

        afficherAttributsItem.addActionListener(e -> actionAffichageAttributs());
        afficherMethodesItem.addActionListener(e -> actionAffichageMethodes());
        optimiserItem.addActionListener(e -> actionOptimiser());
        optimiserLiaisonsItem.addActionListener(e -> actionOptimiserLiaisons());

        menu.add(afficherAttributsItem);
        menu.add(afficherMethodesItem);
        menu.addSeparator();
        menu.add(optimiserItem);
        //menu.add(optimiserLiaisonsItem);

        return menu;
    }

    private JMenu creerMenuAide() 
    {
        JMenu menu = new JMenu("Aide");

        JMenuItem aProposItem = new JMenuItem("À propos");
        aProposItem.addActionListener(e -> actionAPropos());

        menu.add(aProposItem);

        return menu;
    }

    // à faire
    private void actionNouveauProjet() 
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
    }
        
    private void actionOuvrirProjet() 
    {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Afficher la boîte de dialogue
        int resultat = chooser.showOpenDialog(null); // null = centré à l'écran

        // Si l'utilisateur a choisi un fichier
        if (resultat == JFileChooser.APPROVE_OPTION) 
        {
            File fichierSelectionne = chooser.getSelectedFile();
            String cheminAbsolu = fichierSelectionne.getAbsolutePath();

            SauvegardeProjetXml(cheminAbsolu);
        } 
        else 
        {
            System.out.println("Aucun fichier choisi");
        }    
    }

    private void SauvegardeProjetXml(String cheminFichierChoisi) 
    {
        try 
        {
            // Emplacement du fichier XML
            File fichier = new File("donnees/projets.xml");

            // Création du dossier parent si nécessaire
            if (!fichier.getParentFile().exists()) {
                fichier.getParentFile().mkdirs();
            }

            // Création du fichier s'il n'existe pas
            if (!fichier.exists()) {
                fichier.createNewFile();
            }

            // FileWriter avec "true" pour ajouter à la fin
            FileWriter writer = new FileWriter(fichier, true);

            // Écrire la chaîne avec un retour à la ligne
            writer.write(cheminFichierChoisi + System.lineSeparator());

            // Fermer le writer
            writer.close();

            System.out.println("Ajout effectué dans : " + fichier.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        
    private void actionExporter() 
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
    }

    private void actionSauvegarder() 
    {
        this.fenetrePrincipale.actionSauvegarder();
    }

    private void actionAnnuler() 
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
    }

    private void actionRetablir() 
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
    }

    private void actionSupprimer() 
    {
        JOptionPane.showMessageDialog(null, "Pas fini");
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
        fenetrePrincipale.optimiserPositionsClasses();
        fenetrePrincipale.optimiserPositionsLiaisons();
    }

    private void actionOptimiserLiaisons() 
    {
        fenetrePrincipale.optimiserPositionsLiaisons();
    }

    private void actionAPropos() 
    {
        JOptionPane.showMessageDialog(null,
            "Modélisation UML - Générateur de Diagrammes\n" +
            "par Romain BARUCHELLO,\n" + "Jules BOUQUET,\n" + "Pierre COIGNARD,\n" + "Paul NOEL,\n" + "Thibault PADOIS,\n" + "Hugo VARAO GOMES DA SILVA",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }

}
