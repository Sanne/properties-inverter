import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * We take advantage of knowing exactly what Properties#store0 is invoking (!),
 * so to not have to reimplement the decoding & encoding rules.
 * Ignores the calls to {@link #write(String)} until {@link #newLine()} is invoked
 * at least once: that's done to remove the annoying comments that the Properties
 * class otherwise adds as prefix.
 */
public class CheatingBufferWriter extends BufferedWriter {

	private boolean enabled = false;

	public CheatingBufferWriter(Writer out) {
		super( out );
	}

	@Override
	public void write(String str) throws IOException {
		if (enabled)
			super.write( str );
	}

	@Override
	public void newLine() {
		enabled = true;
	}
}
