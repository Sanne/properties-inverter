import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		apply("/home/sanne/sources/hibernate-orm-5.6/rules/jakarta-direct.properties");
		apply("/home/sanne/sources/hibernate-orm-5.6/rules/jakarta-direct-modelgen.properties");
		apply("/home/sanne/sources/hibernate-orm-5.6/rules/jakarta-renames.properties");
	}

	private static void apply(String path) throws IOException {
		Inverter.create(path)
				.invertedCopy()
				.writeBack();
	}
}
