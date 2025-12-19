
package vue;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.*;

public class PanneauProjets extends JPanel
{
    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//
    private static final String CHEMIN_SAUVEGARDES = "data/sauvegardes/";

    private FenetrePrincipale fenetrePrincipale;
    private String            cheminDossiers   ;
    private JPanel            panelProjets     ;

    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//
    
    public PanneauProjets(FenetrePrincipale fenetrePrincipale)
    {
        this.fenetrePrincipale = fenetrePrincipale;
        
        this.cheminDossiers = "data/donnees/projets.xml";

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createTitledBorder("Projets"));

        // Titre
        JLabel titreLabel = new JLabel("Liste des Projets");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titreLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titreLabel, BorderLayout.NORTH);

        // Panel scrollable
        this.panelProjets = new JPanel();
        panelProjets.setLayout(new BoxLayout(panelProjets, BoxLayout.Y_AXIS));
        panelProjets.setBackground(new Color(245, 245, 245));

        chargerProjets(panelProjets);

        JScrollPane scrollPane = new JScrollPane(panelProjets);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        //actualiser la liste
        JButton boutonActualiser = new JButton("Actualiser");
        boutonActualiser.addActionListener(e -> actualiser());
        add(boutonActualiser, BorderLayout.SOUTH);
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

    public void actualiser() 
    {
        panelProjets.removeAll();
        chargerProjets(panelProjets);
        panelProjets.revalidate();
        panelProjets.repaint();
    }

    private void chargerProjets(JPanel panelProjets) 
    {
        File fichier = new File(cheminDossiers);

        if (!fichier.exists())
        {
            JLabel labelErreur = new JLabel("Dossier non trouvé");
            labelErreur.setForeground(Color.RED);
            panelProjets.add(labelErreur);
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(fichier)))
        {
            String  ligne;

            boolean vide         = true;
            boolean formatValide = true;

            while ((ligne = reader.readLine()) != null) 
            {
                ligne = ligne.trim();

                String ligneChemin = ligne.substring(0, ligne.indexOf("\t") );
                String intitule    = ligne.substring(ligne.indexOf("\t") + 1).trim();

                if (!ligneChemin.isEmpty())
                {
                    File projet = new File(ligneChemin);

                    if (projet.exists())
                    {
                        if (projet.isDirectory() || projet.getName().endsWith(".java"))
                        {
                            JButton boutonProjet = creerBoutonProjet(projet, intitule);
                            panelProjets.add(boutonProjet);
                            panelProjets.add(Box.createVerticalStrut(5));
                            vide = false;
                        }
                    }
                }
            }

            if (vide) 
            {
                JLabel labelVide = new JLabel("Aucun projet");
                labelVide.setForeground(Color.GRAY);
                panelProjets.add(labelVide);
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            JLabel labelErreur = new JLabel("Erreur lecture fichier");
            labelErreur.setForeground(Color.RED);
            panelProjets.add(labelErreur);
        }

    }

    private JButton creerBoutonProjet(File projet, String intitule)
    {
        JButton bouton = new JButton(intitule);
        bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        bouton.setFont(new Font("Arial", Font.PLAIN, 12));
        bouton.setBackground(new Color(100, 150, 200));
        bouton.setForeground(Color.WHITE);
        bouton.setFocusPainted(false);

        // Menu contextuel (clic droit)
        JPopupMenu menuContextuel = new JPopupMenu();
        
        JMenuItem renommerItem  = new JMenuItem("Renommer" );
        renommerItem .addActionListener(e -> renommerProjet (projet.getAbsolutePath()));
        
        JMenuItem supprimerItem = new JMenuItem("Supprimer");
        supprimerItem.addActionListener(e -> supprimerProjet(projet.getAbsolutePath()));
        
        menuContextuel.add(renommerItem );
        menuContextuel.add(supprimerItem);
        
        // Afficher le menu au clic droit
        bouton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent e)
            {
                if (e.isPopupTrigger())
                    menuContextuel.show(e.getComponent(), e.getX(), e.getY());
            }
            
            public void mouseReleased(java.awt.event.MouseEvent e)
            {
                if (e.isPopupTrigger())
                    menuContextuel.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        bouton.addActionListener(e ->
        {
            try 
            {
                fenetrePrincipale.ouvrirProjet(projet.getAbsolutePath());
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Impossible de charger le projet : " + projet.getName(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return bouton;
    }

    private void renommerProjet(String cheminProjet)
    {
        String nouvelIntitule = JOptionPane.showInputDialog
        (
            this,
            "Entrez le nouveau nom du projet :",
            "Renommer le projet",
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (nouvelIntitule != null && !nouvelIntitule.trim().isEmpty())
        {
            nouvelIntitule = nouvelIntitule.trim();

            // Vérifier si l'intitulé existe déjà
            if (intituleExiste(nouvelIntitule, cheminProjet))
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Un projet avec ce nom existe déjà !\nVeuillez choisir un autre nom.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            try
            {
                modifierProjetDansFichier(cheminProjet, nouvelIntitule, false);
                actualiser();
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Erreur lors du renommage : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void supprimerProjet(String cheminProjet)
    {
        int confirmation = JOptionPane.showConfirmDialog
        (
            this,
            "Êtes-vous sûr de vouloir supprimer ce projet de la liste ?\n\nAttention : Cette action ne peut pas être annulée.\nSeules les données du graphe seront supprimées",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION)
        {
            try
            {
                modifierProjetDansFichier(cheminProjet, null, true);
                JOptionPane.showMessageDialog
                (
                    this,
                    "Projet supprimé",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
                );
                actualiser();
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Erreur lors de la suppression : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private boolean intituleExiste(String intitule, String cheminProjetActuel)
    {
        File fichier = new File(cheminDossiers);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fichier)))
        {
            String ligne;

            while ((ligne = reader.readLine()) != null)
            {
                String[] parts = ligne.split("\t");

                if (parts.length >= 2)
                {
                    String intituleExistant = parts[1].trim();
                    // Vérifier que c'est pas le même projet
                    if (intituleExistant.equals(intitule) && !parts[0].equals(cheminProjetActuel))
                        return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }

    //Modification de projets.xml
    private void modifierProjetDansFichier(String cheminProjet, String nouvelIntitule, boolean supprimer) throws Exception
    {
        File                        fichier                     = new File(cheminDossiers)   ;
        java.util.ArrayList<String> lignes                      = new java.util.ArrayList<>();
        String                      fichierSauvegardeASupprimer = null                       ;
        
        // Lire toutes les lignes
        try (BufferedReader reader = new BufferedReader(new FileReader(fichier)))
        {
            String ligne;

            while ((ligne = reader.readLine()) != null)
            {
                String[] parts = ligne.split("\t");

                if (parts.length >= 2 && parts[0].equals(cheminProjet))
                {
                    if (supprimer)
                    {
                        // Marquer le fichier de sauvegarde pour suppression
                        fichierSauvegardeASupprimer = parts[1].trim();
                        continue; // Ne pas ajouter cette ligne
                    }
                    else
                    {
                        // Renommer
                        ligne = parts[0] + "\t" + nouvelIntitule;
                        
                        // Renommer aussi le fichier de sauvegarde .xml
                        String ancienNom              = parts[1].trim();

                        File ancienFichierSauvegarde  = new File(CHEMIN_SAUVEGARDES + ancienNom      + ".xml");
                        File nouveauFichierSauvegarde = new File(CHEMIN_SAUVEGARDES + nouvelIntitule + ".xml");

                        if (ancienFichierSauvegarde.exists())
                        {
                            ancienFichierSauvegarde.renameTo(nouveauFichierSauvegarde);
                        }
                    }
                }
                lignes.add(ligne);
            }
        }
        
        // Réécrire le fichier
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fichier)))
        {
            for (String ligne : lignes)
            {
                writer.write(ligne);
                writer.newLine();
            }
        }
        
        // Supprimer le fichier de sauvegarde si nécessaire
        if (fichierSauvegardeASupprimer != null)
        {
            File fichierSauvegarde = new File(CHEMIN_SAUVEGARDES + fichierSauvegardeASupprimer + ".xml");
            if (fichierSauvegarde.exists())
                fichierSauvegarde.delete();
        }

        if (supprimer) fenetrePrincipale.viderDiagramme();
    }
}