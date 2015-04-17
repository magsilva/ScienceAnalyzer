package mariane;

import com.ironiacorp.computer.ComputerSystem;
import com.ironiacorp.computer.Filesystem;
import com.ironiacorp.computer.OperationalSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.ParserResult;

/**
 *
 * @author mariane
 */
public class PrepareDatabase {
    
    private BibtexDatabase database;
    private File inputFile;
    private InputStream inputStream;
    private Charset encoding = Charset.defaultCharset();
    
    public PrepareDatabase(File file) {
        setInputFile(file);
        readData();
    }

    private void setInputFile(File file1) {
        String extension1;

        if ((!file1.exists() || !file1.isFile())) {
            throw new IllegalArgumentException("Invalid BibTeX file");
        }
        this.inputFile = file1;

        OperationalSystem os = ComputerSystem.getCurrentOperationalSystem();
        Filesystem fs = os.getFilesystem();
        extension1 = fs.getExtension(file1);
    }

    private void readData() {
        ParserResult parseResult1;
        Reader reader1 = null;

        try {
            if (inputFile != null) {
                reader1 = new InputStreamReader(new FileInputStream(inputFile), encoding);
            }
            if (inputStream != null) {
                reader1 = new InputStreamReader(inputStream, encoding);
            }
            parseResult1 = BibtexParser.parse(reader1);

            setDatabase(parseResult1.getDatabase());
        } catch (IOException e) {
            throw new RuntimeException("Error reading data", e);
        } finally {
            try {
                reader1.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * @return the database
     */
    public BibtexDatabase getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(BibtexDatabase database) {
        this.database = database;
    }

}
