package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.DBTableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.TableImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TableProviderTest extends TestBase {
    private static TableProviderFactory factory;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private TableProvider provider;

    @BeforeClass
    public static void globalPrepare() {
        factory = new DBTableProviderFactory();
    }

    @Before
    public void prepareProvider() throws DatabaseException {
        provider = factory.create(DB_ROOT.toString());
    }

    @After
    public void cleanupProvider() throws IOException {
        cleanDBRoot();
        provider = null;
    }

    @Test
    public void testGetTableNull() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Table name must not be null");
        provider.getTable(null);
    }

    private void expectTableNameIsNotCorrect() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Table name is not correct");
    }

    @Test
    public void testGetTableBadName() throws DatabaseException {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", "t").toString());
    }

    @Test
    public void testGetTableBadName1() throws DatabaseException {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("..", DB_ROOT.getFileName().toString(), "t").toString());
    }

    @Test
    public void testGetTableBadName2() throws DatabaseException {
        expectTableNameIsNotCorrect();
        provider.getTable(Paths.get("subdir", "t1").toString());
    }

    @Test
    public void testGetTableNotExistent() throws DatabaseException {
        Table table = provider.getTable("not existent");
        assertNull("Not existent table should be null", table);
    }

    @Test
    public void testCreateTableExistent() throws DatabaseException {
        provider.createTable("table");
        Table table = provider.createTable("table");
        assertNull("Cannot create duplicate table, should be null", table);
    }

    @Test
    public void testRemoveTableNotExistent() throws DatabaseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(allOf(startsWith("Table"), endsWith("not exists")));
        provider.removeTable("!not existent!");
    }

    @Test
    public void testFailCreateTable() throws IOException {
        String table = "table";

        Files.createFile(DB_ROOT.resolve(table));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Failed to create table directory: " + table);

        provider.createTable(table);
    }

    @Test
    public void testStoreKeysInImproperPlaces() throws IOException, DatabaseException {
        String tmpTable = "tmp";

        TableImpl table = TableImpl.createTable(DB_ROOT.resolve(tmpTable));

        table.put("a", "b");
        table.put("c", "d");
        table.commit();

        List<Path> paths = new LinkedList<>();

        try (DirectoryStream<Path> partDirs = Files.newDirectoryStream(DB_ROOT.resolve(tmpTable))) {
            for (Path path : partDirs) {
                paths.add(path);
            }
        }

        // Suppose we have 0.dir, 1.dir, ..., 15.dir; Let's shift them left:
        // 1.dir becomes 0.dir, 0.dir becomes 15.dir and so on.
        Path tmpPath = DB_ROOT.resolve(tmpTable).resolve("tmp");

        Files.move(paths.get(0), tmpPath);
        Iterator<Path> pathIterator = paths.listIterator(1);

        Path prev = paths.get(0);

        while (pathIterator.hasNext()) {
            Path next = pathIterator.next();
            Files.move(next, prev);
            prev = next;
        }

        Files.move(tmpPath, paths.get(paths.size() - 1));

        prepareProvider();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(
                "Table " + tmpTable + " is corrupt: Some keys are stored in improper places");
        provider.getTable(tmpTable);
    }
}
