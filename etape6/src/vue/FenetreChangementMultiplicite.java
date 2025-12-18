package vue;


import controleur.Controleur;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class FenetreChangementMultiplicite extends JFrame
{

    private PanelModif panelModif;


    public FenetreChangementMultiplicite()
    {

        this.setTitle("modification des multiplicités");
        this.setSize(350, 350);


        this.panelModif = new PanelModif();
        this.add(this.panelModif, BorderLayout.CENTER);
        


        this.setVisible(true);

    }


    



    

}