public class Chien extends Animal 
{
    private String race;
    private Collier collier;
    

    public Chien(String nom, int age, String race) {
        super(nom, age);
        this.race = race;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void aboyer() {
        System.out.println("Ouaf!");
    }
}