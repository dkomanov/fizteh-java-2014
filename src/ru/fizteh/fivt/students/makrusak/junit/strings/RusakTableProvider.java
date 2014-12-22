package junit.strings;

import java.io.IOException;

import junit.util.DatabaseHandler;
import junit.util.TableHandler;

public class RusakTableProvider implements TableProvider {
    private DatabaseHandler handler;

    public RusakTableProvider(DatabaseHandler getHandler) {
        handler = getHandler;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        TableHandler tableHandler = handler.getTableHandler(name);
        if (tableHandler == null) {
            return null;
        }
        return new RusakTable(tableHandler);
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        try {
            String resString = handler.create(name);
            if (!resString.equals("created")) {
                return null;
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException();
        }
        return getTable(name);
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        String resString = handler.drop(name);
        if (!resString.equals("dropped")) {
            throw new IllegalStateException();
        }
    }

}
