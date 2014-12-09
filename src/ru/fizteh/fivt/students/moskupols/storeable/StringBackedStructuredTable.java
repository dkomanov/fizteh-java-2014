package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

/**
 * Created by moskupols on 09.12.14.
 */
public class StringBackedStructuredTable extends AbstractStructuredTable implements KnownDiffStructuredTable {
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
            return myProvider.deserialize(this, stringTable.put(key, myProvider.serialize(this, value)));
        } catch (ParseException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Storeable remove(String key) {
        try {
            return myProvider.deserialize(this, stringTable.remove(key));
        } catch (ParseException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int size() {
        return stringTable.size();
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
    public String getName() {
        return stringTable.getName();
    }

    @Override
    public Storeable get(String key) {
        try {
            return myProvider.deserialize(this, stringTable.get(key));
        } catch (ParseException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int diff() {
        return stringTable.diff();
    }
}
