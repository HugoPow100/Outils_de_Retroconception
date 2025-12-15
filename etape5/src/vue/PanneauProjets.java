package vue;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.*;

public class PanneauProjets extends JPanel {

    //--------------------------//
    //        ATTRIBUTS         //
    //--------------------------//

    private FenetrePrincipale fenetrePrincipale;
    private String cheminDossiers;


    //-------------------------//
    //      CONSTRUCTEUR       //
    //-------------------------//
    
    public PanneauProjets(FenetrePrincipale fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        
        this.cheminDossiers = "donnees/projets.xml";
        //this.cheminDossiers = "sauvegardes/dossiers";

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createTitledBorder("Projets"));

        // Titre
        JLabel titreLabel = new JLabel("Liste des Projets");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titreLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titreLabel, BorderLayout.NORTH);

        // Panel scrollable
        JPanel panelProjets = new JPanel();
        panelProjets.setLayout(new BoxLayout(panelProjets, BoxLayout.Y_AXIS));
        panelProjets.setBackground(new Color(245, 245, 245));

        chargerProjets(panelProjets);

        JScrollPane scrollPane = new JScrollPane(panelProjets);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        //actualiser la liste
        JButton boutonActualiser = new JButton("Actualiser");
        boutonActualiser.addActionListener(e -> {
            panelProjets.removeAll();
            chargerProjets(panelProjets);
            panelProjets.revalidate();
            panelProjets.repaint();
        });
        add(boutonActualiser, BorderLayout.SOUTH);
    }

    //----------------------//
    //      METHODES        //
    //----------------------//

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
            String ligne;
            boolean vide = true;

            while ((ligne = reader.readLine()) != null) 
            {
                ligne = ligne.trim();

                if (!ligne.isEmpty()) 
                {
                    File projet          = new File(ligne);
                    if (projet.exists())
                    {
                        //System.out.println("Le projet existe : " + ligne);
                        
                        JButton boutonProjet = creerBoutonProjet(projet);
                        panelProjets.add(boutonProjet);
                        panelProjets.add(Box.createVerticalStrut(5));
                        vide = false;

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

    private JButton creerBoutonProjet(File projet) {
        JButton bouton = new JButton(projet.getName());
        bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        bouton.setFont(new Font("Arial", Font.PLAIN, 12));
        bouton.setBackground(new Color(100, 150, 200));
        bouton.setForeground(Color.WHITE);
        bouton.setFocusPainted(false);

        bouton.addActionListener(e -> {
            fenetrePrincipale.chargerProjet(projet.getAbsolutePath());
        });

        return bouton;
    }
}
