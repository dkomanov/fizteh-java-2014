package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Ignore;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.TestUtils;

public abstract class TestBase {
    protected final static Path dbRoot = Paths.get(
	    System.getProperty("user.home"), "test", "JUnitDB");

    @Ignore
    @BeforeClass
    public static void globalPrepareTestBase() throws IOException {
	if (Files.exists(dbRoot)) {
	    Utility.rm(dbRoot, "JUnit Test: global prepare");
	}
    }

    protected void cleanDBRoot() throws IOException {
	if (Files.exists(dbRoot)) {
	    TestUtils.removeFileSubtree(dbRoot);
	}
    }
}
