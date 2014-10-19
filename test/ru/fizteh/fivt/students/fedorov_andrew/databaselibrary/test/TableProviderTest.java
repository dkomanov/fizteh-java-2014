package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

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
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.DBTableProviderFactory;

@RunWith(JUnit4.class)
public class TableProviderTest extends TestBase {
    private static TableProviderFactory factory;
    private TableProvider provider;

    @BeforeClass
    public static void globalPrepare() {
	factory = new DBTableProviderFactory();
    }

    @Before
    public void prepareProvider() {
	provider = factory.create(dbRoot.toString());
    }

    @After
    public void cleanupProvider() throws IOException {
	cleanDBRoot();
	provider = null;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetTableNull() {
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Table name must not be null");
	provider.getTable(null);
    }

    private void expectTableNameIsNotCorrect() {
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage("Table name is not correct");
    }

    @Test
    public void testGetTableBadName() {
	expectTableNameIsNotCorrect();
	provider.getTable(Paths.get("..", "t").toString());
    }

    @Test
    public void testGetTableBadName1() {
	expectTableNameIsNotCorrect();
	provider.getTable(Paths.get("..", dbRoot.getFileName().toString(), "t")
		.toString());
    }

    @Test
    public void testGetTableBadName2() {
	expectTableNameIsNotCorrect();
	provider.getTable(Paths.get("subdir", "t1").toString());
    }

    @Test
    public void testGetTableNotExistent() {
	Table table = provider.getTable("not existent");
	assertNull("Not existent table should be null", table);
    }

    @Test
    public void testCreateTableExistent() {
	provider.createTable("table");
	Table table = provider.createTable("table");
	assertNull("Cannot create duplicate table, should be null", table);
    }

    @Test
    public void testRemoveTableNotExistent() {
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(allOf(startsWith("Table"),
		endsWith("not exists")));
	provider.removeTable("!not existent!");
    }
}
