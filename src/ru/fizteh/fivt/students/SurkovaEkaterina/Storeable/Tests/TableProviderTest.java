package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseTableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.*;

public class TableProviderTest {
    private static final String STORAGE = "\\temp\\storeable_provider_test";;
    TableProviderFactory factory = new DatabaseTableProviderFactory();
    TableProvider provider;
    Table currentTable;

    @Before
    public void setUp() {
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            provider = factory.create(STORAGE);
            ((DatabaseTableProvider) provider).clear();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);

        try {
            currentTable = provider.createTable("providerTestTable", columnTypes);
        } catch (IOException e) {
            System.out.println("Unexpected error! Something has gone wrong!");
        }
    }

    @Test(expected = IOException.class)
    public void createProviderUnavailableShouldFail() throws IOException {
        factory.create("/not_existing_dir");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderEmptyShouldFail() {
        try {
            factory.create("");
        } catch (IOException e) {
            System.out.println("Unexpected error! Something has gone wrong!");
        }
    }

    @Test(expected = ColumnFormatException.class)
    public void createForShouldFailTest() {
        List<Object> values = new ArrayList<Object>() { {
            add(900);
            add(888);
        } };
        provider.createFor(currentTable, values);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForShouldFailTest2() {
        List<Object> values = new ArrayList<Object>() { {
            add(888);
            add("aa");
            add("onemore");
        }};
        provider.createFor(currentTable, values);
    }
}
