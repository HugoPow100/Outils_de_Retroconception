package vue ;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import vue.liaison.LiaisonVue;
import java.awt.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;




public class PanelModif extends JPanel implements ListSelectionListener, ActionListener
{

    private JTextField txtMultipliciteMin;
    private JTextField txtMultipliciteMax;


    private JLabel lblMultipliciteMin;
    private JLabel lblMultipliciteMax;

    private JLabel lblTitre;

    private JPanel panel1;
    private JPanel panel2;

    private JButton btnValider;
    private JButton btnAnnuler;

    private JPanel panelBoutons;

    private JList<String>    listeLiaisonsIHM;
    private List<LiaisonVue> listeLiaisons;

    

    public PanelModif()
    {

        String[] testLiaisons = { "Liaison 1", "Liaison 2", "Liaison 3", "Liaison 4" };
        this.listeLiaisonsIHM = new JList<>(testLiaisons);
        this.setLayout(new GridLayout(5,1));

        this.panel1 = new JPanel();
        this.panel2 = new JPanel();

        


        this.lblTitre = new JLabel("Modification : [ BLOC CLIQUE ] a ",  JLabel.CENTER);
        
        this.lblMultipliciteMin = new JLabel("Multiplicité Min : ");
        this.txtMultipliciteMin = new JTextField(20);

        this.lblMultipliciteMax = new JLabel("Multiplicité Max : ");
        this.txtMultipliciteMax = new JTextField(20);

        this.btnValider = new JButton("Valider");
        this.btnAnnuler = new JButton("Annuler");

        this.panelBoutons = new JPanel();

        JScrollPane scrollPane = new JScrollPane(listeLiaisonsIHM);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panelBoutons.add(this.btnValider);
        panelBoutons.add(this.btnAnnuler);

        this.add(lblTitre);

        panel1.add(lblMultipliciteMin);
        panel1.add(txtMultipliciteMin);
        this.add(panel1);

        panel2.add(lblMultipliciteMax);
        panel2.add(txtMultipliciteMax);
        this.add(panel2);


       // this.add(listeLiaisonsIHM);



        //this.add(this.listeLiaisonsIHM);
        this.add(scrollPane);

        this.add(panelBoutons);

        this.listeLiaisonsIHM.addListSelectionListener(this);

        this.btnValider.addActionListener(this);
        this.btnAnnuler.addActionListener(this);

        this.setVisible(true);
    }


    public void valueChanged(ListSelectionEvent e)
    {
        System.out.println("Liaison sélectionnée : " + listeLiaisonsIHM.getSelectedValue());
        lblTitre.setText("Modification : [ BLOC CLIQUE ] a " + listeLiaisonsIHM.getSelectedValue());
    }

    public boolean caractereValideMultMin()
    {

        try
        {
            Integer.parseInt(txtMultipliciteMin.getText());
            return true;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Erreur : Multiplicité Min non valide");
        }

        return false;
    }

    public boolean caractereValideMultMax()
    {

        try
        {
            Integer.parseInt(txtMultipliciteMax.getText());
            return true;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Erreur : Multiplicité Max non valide");
        }

        return false;
    }


    public boolean estValide()
    {

        if 
    }


    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == btnValider)
        {

            if (estNombre())
            {
                int min = Integer.parseInt(txtMultipliciteMin.getText());
                int max = Integer.parseInt(txtMultipliciteMax.getText());
                System.out.println("Vous avez valider les Multiplicités : " + min + " et " + max);
            }
            else
            {
                System.out.println("Erreur : Multiplicité non valide");
                return;
            }
            
        }
        else if(e.getSource() == btnAnnuler)
        {
            System.out.println("Annuler clicked");
        }
    }





    
}