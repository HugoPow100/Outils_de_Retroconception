public class TestConventions
{
	private String nom;
	private int age;

	// Constructeur en convention Allman (accolade sur ligne suivante)
	public TestConventions(String nom, int age)
	{
		this.nom = nom;
		this.age = age;
	}

	// Méthode en convention K&R (accolade sur même ligne)
	public String getNom()
	{
		return this.nom;
	}

	// Méthode en convention Allman
	public int getAge()
	{
		return this.age;
	}

	// Méthode avec paramètres en K&R
	public void setNom(String nom)
	{
		this.nom = nom;
	}

	// Méthode avec paramètres en Allman
	public void setAge(int age)
	{
		this.age = age;
	}

	// Méthode static en K&R
	public static int calculer(int a, int b)
	{
		return a + b;
	}

	// Méthode static en Allman
	public static int multiplier(int a, int b)
	{
		return a * b;
	}

	// Méthode avec type de retour complexe en K&R
	public String toString()
	{
		return nom + " - " + age;
	}

	// Méthode avec type de retour complexe en Allman
	public boolean equals(Object obj)
	{
		if (obj instanceof TestConventions)
		{
			TestConventions other = (TestConventions) obj;
			return this.nom.equals(other.nom) && this.age == other.age;
		}
		return false;
	}
}
