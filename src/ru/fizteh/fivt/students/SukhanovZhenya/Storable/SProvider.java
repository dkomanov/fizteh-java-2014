package ru.fizteh.fivt.students.SukhanovZhenya.Storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SProvider implements TableProvider {
    private File currentDir;

    public SProvider(String dir) {
        currentDir = new File(dir);
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (!tmp.exists()) {
            return null;
        }
        return new STable(currentDir + "/" + name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File tmp = new File(currentDir.getAbsolutePath() + "/" + name);
        if (tmp.exists()) {
            return null;
        }

        if (!tmp.mkdir()) {
            System.err.println("Can not create Table");
            System.exit(1);
        }

        return new STable(currentDir.getAbsolutePath() + "/" + name, columnTypes);
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument");
        }

        File table = new File(currentDir.getAbsolutePath() + "/" + name);
        if (!table.exists()) {
            throw new IllegalStateException("Doesn't exist");
        }

        STable tmp = (STable) getTable(name);
        tmp.remove();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            return new SStoreable(XMLCoder.deserializeString(value,
                    ((STable) table).getTypesList()), ((STable) table).getTypesList());
        } catch (IllegalArgumentException e) {
            throw new ParseException("Can not parse " + value, 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (((STable) table).getTypesList() != ((SStoreable) value).getTypes()) {
            throw new ColumnFormatException("Wrong type");
        }

        return XMLCoder.serializeObjects(((SStoreable) value).getObjects());
    }

    @Override
    public Storeable createFor(Table table) {
        int size = ((STable) table).getTypesList().size();
        List<Object> values = new ArrayList<Object>(size);
        return new SStoreable(values, ((STable) table).getTypesList());
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        int size = ((STable) table).getTypesList().size();
        if (size != values.size()) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }

        SStoreable tmp = (SStoreable) createFor(table);
        for (int i = 0; i < size; ++i) {
            tmp.setColumnAt(i, values.get(i));
        }

        return tmp;
    }

    @Override
    public String[] getTableNames() {
        return currentDir.list();
    }


}
