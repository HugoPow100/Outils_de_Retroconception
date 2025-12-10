package vue;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class PanneauProjets extends JPanel {

    private FenetrePrincipale fenetrePrincipale;
    private String cheminDossiers;

    public PanneauProjets(FenetrePrincipale fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        
        //this.cheminDossiers = "donnees/projets.xml";
        this.cheminDossiers = "sauvegardes/dossiers";

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

    private void chargerProjets(JPanel panelProjets) 
    {

        File dossier = new File(cheminDossiers);

        if (!dossier.exists() || !dossier.isDirectory()) 
        {
            JLabel labelErreur = new JLabel("Dossier non trouvé");
            labelErreur.setForeground(Color.RED);
            panelProjets.add(labelErreur);
            return;
        }

        File[] projets = dossier.listFiles(File::isDirectory);

        if (projets == null || projets.length == 0) 
        {
            JLabel labelVide = new JLabel("Aucun projet");
            labelVide.setForeground(Color.GRAY);
            panelProjets.add(labelVide);
            return;
        }

        for (File projet : projets) 
        {
            JButton boutonProjet = creerBoutonProjet(projet);
            panelProjets.add(boutonProjet);
            panelProjets.add(Box.createVerticalStrut(5));
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
