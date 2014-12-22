package junit.strings;

import java.io.File;
import java.io.IOException;

import junit.util.DatabaseHandler;

public class RusakTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        try {
            DatabaseHandler handler = new DatabaseHandler(new File(dir));
            return new RusakTableProvider(handler);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }

    }

}
