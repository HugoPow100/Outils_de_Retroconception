public class Chien implements Animal {

    @Override
    public void manger() {
        System.out.println("Le chien mange des croquettes.");
    }

    @Override
    public void dormir() {
        System.out.println("Le chien dort dans sa niche.");
    }

    public void aboyer() {
        System.out.println("Le chien aboie : Woof !");
    }
}
