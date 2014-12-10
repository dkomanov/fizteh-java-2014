package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.database.TableProviderByVolodden;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TableProviderByVoloddenTest {

    public final static String name = "test123321123323";
    public final static String nameTable = "nameTable";
    public final static String end = ".." + File.separator + name;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = IllegalStateException.class)
    public void testGetTableNotExists() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        TableProvider tableProvider = new TableProviderByVolodden(name);
        try {
            Table table = tableProvider.getTable(Paths.get(name, nameTable).toString());
        } catch (IllegalStateException e) {
            Files.deleteIfExists(path);
            throw new IllegalStateException();
        }
    }

    @Test
    public void testGetTable() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        TableProvider tableProvider = new TableProviderByVolodden(name);
        Table table = tableProvider.createTable(nameTable);
        table.put("k", "v");
        table.commit();
        table = tableProvider.getTable(nameTable);
        tableProvider.removeTable(nameTable);
        Files.deleteIfExists(path);
    }

    @Test
    public void testCreateTable() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        TableProvider tableProvider = new TableProviderByVolodden(path.toString());
        Table table = tableProvider.createTable(nameTable);
        assertEquals(true, Paths.get(path.toString(), nameTable).toFile().exists());
        Files.deleteIfExists(Paths.get(path.toString(), nameTable));
        Files.deleteIfExists(path);
    }

    @Test
    public void testCreateTableIfExists() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        TableProvider tableProvider = new TableProviderByVolodden(path.toString());
        Files.createDirectory(Paths.get(path.toString(), nameTable));
        Table table = tableProvider.createTable(nameTable);
        assertEquals(true, Paths.get(path.toString(), nameTable).toFile().exists());
        Files.deleteIfExists(Paths.get(path.toString(), nameTable));
        Files.deleteIfExists(path);
    }

    @Test
    public void testRemoveTable() throws Exception {
        Path path = Paths.get(temporaryFolder.toString(), end).toAbsolutePath().normalize();
        TableProvider tableProvider = new TableProviderByVolodden(path.toString());
        Table table = tableProvider.createTable(nameTable);
        table.put("k", "v");
        table.commit();
        tableProvider.removeTable(nameTable);
        assertEquals(false, Paths.get(path.toString(), nameTable).toFile().exists());
        Files.deleteIfExists(Paths.get(path.toString(), nameTable));
        Files.deleteIfExists(path);
    }
}