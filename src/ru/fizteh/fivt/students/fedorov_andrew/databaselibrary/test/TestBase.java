package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.BeforeClass;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TestBase {
    protected static final Path DB_ROOT =
            Paths.get(System.getProperty("user.home"), "test", "JUnitDB");

    @BeforeClass
    public static void globalPrepareTestBase() throws IOException {
        if (Files.exists(DB_ROOT)) {
            Utility.rm(DB_ROOT, "JUnit Test: global prepare");
        }
    }

    protected void cleanDBRoot() throws IOException {
        if (Files.exists(DB_ROOT)) {
            TestUtils.removeFileSubtree(DB_ROOT);
        }
    }
}
