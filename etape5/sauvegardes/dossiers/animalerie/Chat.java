public class Chat extends Animal {
    private String couleur;
    private Collier collier;

    public Chat(String nom, int age, String couleur) {
        super(nom, age);
        this.couleur = couleur;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void miauler() {
        System.out.println("Miaou!");
    }
}