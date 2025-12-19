package vue.role_classe;

import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import vue.BlocClasse;
import vue.PanneauDiagramme;
import vue.liaison.LiaisonVue;

public class PanneauModif extends JPanel implements ListSelectionListener, ActionListener
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

	private String[] listeClasseDest;

	private PanneauDiagramme panDiag;

	private String nomSelectionListe;

	private final String MULTIPLICITE_PAR_DEFAUT = "1..1";

	private BlocClasse blocSelectionne;

	public PanneauModif(PanneauDiagramme panDiag)
	{
		this.panDiag = panDiag;
		this.blocSelectionne = panDiag.getBlocClique();
		
		this.listeClasseDest = new String[getLiaisonConnectees(panDiag.getBlocClique()).size() - 1];
		initListe();

		this.listeLiaisonsIHM = new JList<>(listeClasseDest);
		this.setLayout(new GridLayout(5,1));

		this.panel1 = new JPanel();
		this.panel2 = new JPanel();

		this.lblTitre = new JLabel("Modification :" + this.blocSelectionne.getNom() ,  JLabel.CENTER);
		
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

	public void initListe()
	{
		
		int index = 0;
		for (LiaisonVue liaison : this.panDiag.getLstLiaisons()) 
		{
			if (liaison.getType().equals("association")) 
			{
				// Si la classe cliquée est l'origine ou la destination, on affiche l'autre extrémité
				if (this.blocSelectionne == liaison.getBlocOrigine()) 
				{
					this.listeClasseDest[index] = liaison.getBlocDestination().getNom();
					index++;
				} 
				else if (this.blocSelectionne == liaison.getBlocDestination()) 
				{
					this.listeClasseDest[index] = liaison.getBlocOrigine().getNom();
					index++;
				}
			}
		}
		// Tronquer le tableau si besoin
	}
	public void valueChanged(ListSelectionEvent e)
	{
		//System.out.println("Liaison sélectionnée : " + listeLiaisonsIHM.getSelectedValue());
		lblTitre.setText("Modification : " + this.blocSelectionne.getNom() + " à " + listeLiaisonsIHM.getSelectedValue());
		nomSelectionListe = listeLiaisonsIHM.getSelectedValue();
	}

	public boolean caractereValideMultMin(String min)
	{
		try
		{
			Integer.parseInt(min);
			return true;
		}
		catch (NumberFormatException e)
		{
			System.err.println("Erreur : Multiplicité min non valide");
		}

		return false;
	}

	public boolean caractereValideMultMax(String max)
	{
		try
		{
			
			Integer.parseInt(max);
			
			return true;
		}
		catch (NumberFormatException e)
		{
			System.err.println("Erreur : Multiplicité Max non valide");
		}

		return false;
	}

	public boolean estMultipliciteValide(String min, String max)
	{
		
		if ((caractereValideMultMin(min) && txtMultipliciteMax.getText().equals("*")) || (caractereValideMultMin(min) && caractereValideMultMax(max)) )
		{
			try
			{
				int minFormat = Integer.parseInt(min);
				int maxFormat = Integer.parseInt(max);
				if (minFormat > maxFormat)
				{
					System.err.println("Changement impossible : Le minimum est superieur au maximum");
					return false;
				}
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
			
			return true;
		}

		return false;

	}

	public BlocClasse getBlocClasseSelectionne()
	{
		List<BlocClasse> lstBlocsClasses = panDiag.getBlocsClasses();

		for (BlocClasse blcClasse : lstBlocsClasses)
		{
			if (blcClasse.getNom() == this.nomSelectionListe)
			{
				return blcClasse;
			}
		}
		
		return null;
	}
	

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnValider)
		{				
			
			valider();
			SwingUtilities.getWindowAncestor(this).dispose();
			
		}

		if(e.getSource() == btnAnnuler)
		{
			SwingUtilities.getWindowAncestor(this).dispose();
		}

			
	}

	public void valider()
	{
		String	 nouvelleMultiplicite = MULTIPLICITE_PAR_DEFAUT;

		String min = txtMultipliciteMin.getText();
		String max = txtMultipliciteMax.getText();
		
		if (!getBlocClasseSelectionne().equals(null) && estMultipliciteValide(min, max))
		{
			int minFormatte = Integer.parseInt(min);

			List<LiaisonVue> lstLiaisons = getLiaisonConnectees(this.getBlocClasseSelectionne());

			for (LiaisonVue liaison : lstLiaisons)
			{
				if(liaison.getBlocOrigine() == this.getBlocClasseSelectionne())
				{
					nouvelleMultiplicite = min + ".." + max;
					
					liaison.setMultDest(nouvelleMultiplicite);
				}
				else if(liaison.getBlocDestination() == this.getBlocClasseSelectionne())
				{
					nouvelleMultiplicite = min + ".." + max;
					
					liaison.setMultOrig(nouvelleMultiplicite);
				}
			}

			//System.out.println("Nouvelle Multiplicité : " + nouvelleMultiplicite);
			//System.out.println("Les multiplicités de " + this.blocSelectionne.getNom() + " vers " + listeLiaisonsIHM.getSelectedValue() + " sont modifiés");
			this.panDiag.rafraichirDiagramme();
		}
		else
		{
			System.err.println("Erreur : Multiplicité non valide");
			return;
		}
	}

	public List<LiaisonVue> getLiaisonConnectees(BlocClasse blcClasse) 
	{
		List<LiaisonVue> lstLiaisonVues = new ArrayList<>();

		for(LiaisonVue liaison : this.panDiag.getLstLiaisons())
		{
			if(liaison.getBlocOrigine() == this.blocSelectionne)
			{
				lstLiaisonVues.add(liaison);
			}
			else
			if(liaison.getBlocDestination() == this.blocSelectionne)
			{
				lstLiaisonVues.add(liaison);
			}
		}

		return lstLiaisonVues;
	}
}