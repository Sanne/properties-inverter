import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

final class Inverter {

	private final List<String> content;
	private final String originalPath;

	private Inverter(List<String> content, String originalPath) {
		this.content = content;
		this.originalPath = originalPath;
	}

	private static List<String> load(String path) throws IOException {
		return Files.readAllLines( Path.of( path ) );
	}

	public static Inverter create(String path) throws IOException {
		final List<String> loaded = load( path );
		return new Inverter( loaded, path );
	}

	public Inverter invertedCopy() {
		ArrayList<String> newContent = new ArrayList<>( this.content.size() );
		HashSet<String> keyCollisionCheck = new HashSet<>();
		for ( String s : this.content ) {
			newContent.add( invertPropertyMaybe( s, keyCollisionCheck ) );
		}
		return new Inverter( newContent, originalPath );
	}

	private String invertPropertyMaybe(String s, HashSet<String> keyCollisionCheck) {
		if ( s.isBlank() || s.startsWith( "#" ) ) {
			return s;
		}
		else {
			try {
				return invertProperty( s, keyCollisionCheck );
			}
			catch (IOException e) {
				throw new IllegalStateException( "Unexpected" );
			}
		}
	}

	private String invertProperty(String s, HashSet<String> keyCollisionCheck) throws IOException {
		Properties sourceProperty = new Properties();
		sourceProperty.load( new StringReader( s ) );

		Properties invertedProperty = new Properties();
		for ( Map.Entry<Object, Object> entry : sourceProperty.entrySet() ) {
			final String key = checkString( entry.getKey() );
			final String value = checkString( entry.getValue() );
			if ( !keyCollisionCheck.add( value ) ) {
				throw new IllegalStateException( String.format(
						"Non unique key '%s' detected during inversion of properties in file %s",
						value,
						originalPath
				) );
			}
			invertedProperty.put( value, key );
		}

		StringWriter sw = new StringWriter();
		CheatingBufferWriter cbw = new CheatingBufferWriter( sw );
		invertedProperty.store( cbw, null );
		return sw.toString();
	}

	private String checkString(Object o) {
		if ( o == null ) {
			throw new IllegalStateException( "Null not expected here" );
		}
		//We want to check that it's a String - but a ClassCastException is a good fit in this context:
		return (String) o;
	}

	public void writeBack() throws IOException {
		try (FileWriter writer = new FileWriter( this.originalPath )) {
			BufferedWriter bw = new BufferedWriter( writer );
			for ( String s : this.content ) {
				bw.write( s );
				bw.newLine();
			}
			bw.flush();
		}
	}

}
