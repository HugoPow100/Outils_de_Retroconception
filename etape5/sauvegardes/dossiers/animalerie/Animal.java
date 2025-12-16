public class Animal 
{
    private String nom;
    private int age;

    private static int nbAnimaux = 0;
    private static final int nbPattes = 4;
    
    public Animal(String nom, int age) 
    {
        this.nom = nom;
        this.age = age;

        nbAnimaux++;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return age;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
