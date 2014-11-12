package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class SingleTableTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    public String dbDirPath;

    @Before
    public void setUp() throws IOException {
        TableProviderFactory factory = new DataBaseProviderFactory();
        dbDirPath = tmpFolder.newFolder().getAbsolutePath();
        TableProvider provider = factory.create(dbDirPath);
        table = provider.createTable("table");
    }

}