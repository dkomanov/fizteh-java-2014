package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedStructuredTable extends AbstractStructuredTable implements Table {
    private final TableProvider myProvider;
    private final KnownDiffTable stringTable;

    protected StringBackedStructuredTable(
            TableProvider myProvider, Path path, KnownDiffTable stringTable) throws IOException {
        super(path);
        this.myProvider = myProvider;
        this.stringTable = stringTable;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        try {
            final String ret = stringTable.put(key, myProvider.serialize(this, value));
            if (ret == null) {
                return null;
            }
            return myProvider.deserialize(this, ret);
        } catch (ParseException e) {
            throw new ColumnFormatException(e);
        }
    }

    @Override
    public Storeable remove(String key) {
        try {
            final String ret = stringTable.remove(key);
            if (ret == null) {
                return null;
            }
            return myProvider.deserialize(this, ret);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int size() {
        return stringTable.size();
    }

    @Override
    public List<String> list() {
        return stringTable.list();
    }

    @Override
    public int commit() throws IOException {
        return stringTable.commit();
    }

    @Override
    public int rollback() {
        return stringTable.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return stringTable.diff();
    }

    @Override
    public String getName() {
        return stringTable.getName();
    }

    @Override
    public Storeable get(String key) {
        try {
            final String ret = stringTable.get(key);
            if (ret == null) {
                return null;
            }
            return myProvider.deserialize(this, ret);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
