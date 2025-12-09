package test;

public class Voiture {
    private String marque;
    private Personne proprietaire;

    public Voiture(String marque) {
        this.marque = marque;
        this.proprietaire = null;
    }

    public String getMarque() {
        return marque;
    }

    public Personne getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Personne proprietaire) {
        this.proprietaire = proprietaire;
    }
}
