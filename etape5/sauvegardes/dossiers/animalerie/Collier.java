public class Collier implements IAccessoire {
    private Chat proprietaire;
    private String nom;
    private String couleur;

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    @Override
    public String getCouleur() {
        return couleur;
    }
}