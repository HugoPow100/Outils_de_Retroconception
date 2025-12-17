public class Chat extends Animal {
    private final String couleur;
    private Collier collier;

    public Chat(String nom, int age, String couleur) {
        super(nom, age);
        this.couleur = couleur;
    }

    public String getCouleur() {
        return couleur;
    }

    public void miauler() {
        System.out.println("Miaou!");
    }
}