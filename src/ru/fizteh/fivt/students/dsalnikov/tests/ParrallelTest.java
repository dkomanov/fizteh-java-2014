package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class ParrallelTest {
    TableProvider tableProvider;
    Table testTable;
    Storeable testStoreable;
    List<Class<?>> typesTestListOne;
    File sandBoxDirectory = new File(System.getProperty("fizteh.db.dir"));

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        tableProvider = new StorableTableProvider(sandBoxDirectory);

        typesTestListOne = FileMapUtils.createListOfTypesFromString("int int int");
        testTable = tableProvider.createTable("testParallelTable", typesTestListOne);
        testStoreable = tableProvider.deserialize(testTable, "[1,2,3]");
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testParallelTable");
        FileUtils.forceRemoveDirectory(sandBoxDirectory);
    }

    @Test
    public void threadSimpleWorkTest() throws Exception {
        Thread firstTestThread = new Thread(() -> {
            testTable.put("firstKey", testStoreable);
            try {
                for (int i = 0; i < 100000000; ++i) {
                    //do nothing
                }
                testTable.commit();
            } catch (IOException e) {
                throw new IllegalArgumentException("thread simple test: commit error");
            }
        });

        Thread secondTestThread = new Thread(() -> {
            testTable.put("secondKey", testStoreable);
            try {
                for (int i = 0; i < 100000000; ++i) {
                    //do nothing
                }
                testTable.commit();
            } catch (IOException e) {
                throw new IllegalArgumentException("thread simple test: commit error");
            }
        });

        firstTestThread.run();
        secondTestThread.run();

        firstTestThread.interrupt();
        secondTestThread.interrupt();

        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTable, testTable.get("firstKey")));
        Assert.assertEquals("[1,2,3]", tableProvider.serialize(testTable, testTable.get("secondKey")));
    }

    @Test
    public void threadCreateTablesTest() throws Exception {
        Thread firstTestThread = new Thread(() -> {
            try {
                tableProvider.createTable("parallelTable", typesTestListOne);
                tableProvider.getTable("parallelNotExTable");
            } catch (IOException e) {
                throw new IllegalArgumentException("thread create table test: error");
            }
        });

        Thread secondTestThread = new Thread(() -> {
            try {
                tableProvider.createTable("parallelNotExTable", typesTestListOne);
                tableProvider.createTable("parallelTable", typesTestListOne);
                tableProvider.getTable("parallelTable");
            } catch (IOException e) {
                throw new IllegalArgumentException("thread create table test: error");
            }
        });

        firstTestThread.run();
        secondTestThread.run();

        firstTestThread.interrupt();
        secondTestThread.interrupt();
    }
}
