public class Dog extends Animal implements Playable
{
	private final String NAME;
	private static final String TYPE = "Chien";

	public Dog(String name)
	{
		super(name);
	}

	@Override
	public void speak()
	{
		System.out.println(name + " aboie.");
	}

	@Override
	public void play()
	{
		System.out.println(name + " joue avec une balle.");
	}
}