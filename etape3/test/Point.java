package metier;

public class Point {
    private String nom;
    private int x;
    private int y;
    private Disque d;

    public Point(String nom, int x, int y) {
        this.nom = nom;
        this.x = x;
        this.y = y;
    }

    private void test() {
        this.x = this.y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getNom() {
        return this.nom;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setY(int y) {
        this.y = y;
    }
}
