package test;

public class Personne {
    private String nom;
    private Voiture voiture;

    public Personne(String nom) {
        this.nom = nom;
        this.voiture = null;
    }

    public String getNom() {
        return nom;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }
}
