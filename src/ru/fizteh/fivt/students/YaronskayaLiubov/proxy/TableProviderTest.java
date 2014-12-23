package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by luba_yaronskaya on 17.11.14.
 */
@RunWith(Parameterized.class)
public class TableProviderTest {
    private TableProviderFactory factory;
    private TableProvider provider;
    static List<Class<?>> columnTypes;
    static List<Class<?>> columnTypes2;
    static Class<?>[] availableTypes = {Integer.class, Long.class,
            Byte.class, Float.class, Double.class, Boolean.class, String.class};
    static Storeable row;
    static String serializedRow = "<row><col>1</col><col>value</col></row>";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setColumnTypes() {
        columnTypes = new ArrayList<>();
        columnTypes = Arrays.asList(availableTypes);
        columnTypes2 = new ArrayList<>();
        columnTypes2.add(availableTypes[0]);
        columnTypes2.add(availableTypes[6]);
    }

    @Before
    public void setUpProvider() throws IOException {
        provider = factory.create(testFolder.newFolder().getCanonicalPath());
        Table table = provider.createTable("testTable", columnTypes2);
        row = provider.createFor(table);
        row.setColumnAt(0, 1);
        row.setColumnAt(1, "value");
    }

    public TableProviderTest(TableProviderFactory factory) {
        this.factory = factory;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][]{
                {new StoreableDataTableProviderFactory()}
        });

    }

    @Test
    public void testGetExistentTable() throws Exception {
        provider.createTable("new table", columnTypes);
        assertNotNull(provider.getTable("new table"));
    }

    @Test
    public void testGetNonexistentTable() throws Exception {
        assertNull(provider.getTable("nonexistent table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmptyName() throws Exception {
        provider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableDotName() throws Exception {
        provider.getTable(".");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableSemiName() throws Exception {
        provider.getTable(";");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableSlashName() throws Exception {
        provider.getTable("/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableTwoBackSlashName() throws Exception {
        provider.getTable("\\");
    }

    @Test
    public void testCreateTableNew() throws Exception {
        for (int i = 0; i < 100; ++i) {
            assertNotNull(provider.createTable("new table" + i, columnTypes));
        }
    }

    @Test
    public void testCreateTableExisting() throws Exception {
        for (int i = 0; i < 100; ++i) {
            provider.createTable("new table" + i, columnTypes);
        }
        for (int i = 0; i < 100; ++i) {
            assertNull(provider.createTable("new table" + i, columnTypes));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        provider.createTable(null, columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullTypes() throws Exception {
        provider.createTable("new table", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIllegalName() throws Exception {
        provider.createTable("", columnTypes);
    }

    @Test
    public void testRemoveExistentTable() throws Exception {
        assertNotNull(provider.createTable("table", columnTypes));
        provider.removeTable("table");
        assertNull(provider.getTable("table"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNonexistentTable() throws Exception {
        provider.removeTable("nonexistent table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        provider.removeTable(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalName() throws Exception {
        provider.removeTable("");
    }

    @Test
    public void testSerialize() throws Exception {
        List<Class<?>> columnTypes2 = new ArrayList<>();
        columnTypes2.add(availableTypes[0]);
        columnTypes2.add(availableTypes[6]);
        Table table = provider.createTable("table", columnTypes2);
        Storeable item = provider.createFor(table);
        item.setColumnAt(0, 1);
        item.setColumnAt(1, "value");
        assertEquals("<row><col>1</col><col>value</col></row>", provider.serialize(table, item));
    }

    @Test(expected = ColumnFormatException.class)
    public void testSerializeMismatchTypes() throws Exception {
        Table table = provider.createTable("table", columnTypes);
        provider.serialize(table, row);
    }

    @Test()
    public void testDeserialize() throws Exception {
        Table table = provider.createTable("table2", columnTypes2);
        Storeable actualRow = provider.deserialize(table, serializedRow);
        for (int i = 0; i < columnTypes2.size(); ++i) {
            assertEquals(row.getColumnAt(i), actualRow.getColumnAt(i));
        }
    }

    @Test(expected = ParseException.class)
    public void testDeserializeMismatchTypes() throws Exception {
        Table table = provider.createTable("table", columnTypes);
        provider.deserialize(table, serializedRow);
    }

    @Test(expected = ParseException.class)
    public void testDeserializeWrongXMLFormat() throws Exception {
        Table table = provider.createTable("table", columnTypes);
        provider.deserialize(table, "<wrongrow><col>1</col><col>value</col></wrongrow>");
    }
}
