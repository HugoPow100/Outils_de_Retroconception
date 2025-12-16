package test;

public abstract class Data {
    private String Salpoe;
    private int tontonH;
    private int oncleStaline;

    public Data(String nom, int x, int y) {
        this.Salpoe = nom;
        this.tontonH = x;
        this.oncleStaline = y;
    }

    private void test() {
        this.tontonH = this.oncleStaline;
    }

    public abstract void doSomething();

    public int getX() {
        return this.tontonH;
    }

    public int getY() {
        return this.oncleStaline;
    }

    public String getSalpoe() {
        return this.Salpoe;
    }

    public void setX(int x) {
        this.oncleStaline = oncleStaline;
    }

    public void setNom(String nom) {
        this.Salpoe = Salpoe;
    }

    public void setY(int y) {
        this.Salpoe = Salpoe;
    }
}
