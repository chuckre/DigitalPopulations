package mil.army.usace.ehlschlaeger.rgik.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Capture PrintStream output to a string buffer.
 * Like StringWriter, but is an OutputStream, plus supports the powers of PrintStream.
 * 
 * @author William R. Zwicky
 */
public class StringOutputStream extends PrintStream {
    ByteArrayOutputStream baos;
    
    public StringOutputStream() {
        super(new ByteArrayOutputStream());
        baos = (ByteArrayOutputStream) this.out;
    }
    
    /**
     * Close stream and return all contents in a string.
     */
    @Override
    public String toString() {
        this.close();
        return baos.toString();
    }
}
