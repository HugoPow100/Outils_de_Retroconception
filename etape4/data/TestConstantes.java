public class TestConstantes
{
	// Constante (final static + majuscules)
	public static final int MAX_SIZE = 100;
	public static final String APP_NAME = "MonApp";
	public static String ECHEC = "Bertrand";
	public final String ECHEC2 = "Julie";

	// Variable statique (pas une constante)
	public static int compteur = 0;

	// Variable d'instance normale
	private String nom;
	private int age;

	public TestConstantes(String nom, int age)
	{
		this.nom = nom;
		this.age = age;
	}

	public String getNom()
	{
		return this.nom;
	}

	public int getAge()
	{
		return this.age;
	}
}