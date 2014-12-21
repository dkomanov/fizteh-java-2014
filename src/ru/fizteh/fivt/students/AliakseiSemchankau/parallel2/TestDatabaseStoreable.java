package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabaseStoreable {

    public static DatabaseProvider dProvider;
    public static Table dTable;
    SerializeFunctions serializer;
    MakeRandom randomizer;
    List<Class<?>> signature;
    DatabaseFactory dFactory;
    TemporaryFolder tmp;
    String tmpName;
    @Before
    public void init() {
        // dProvider = new DatabaseProvider("C:\\JavaTests\\DatabaseForTests");
        tmp = new TemporaryFolder();
        tmpName = tmp.toString();
        dFactory = new DatabaseFactory();
        dProvider = dFactory.create(tmpName);
        signature = new LinkedList<>();
        signature.add(Integer.class);
        signature.add(String.class);
        signature.add(Long.class);
        signature.add(Float.class);
        signature.add(Boolean.class);
        signature.add(Byte.class);
        signature.add(Double.class);
        try {
            dTable = dProvider.createTable("newTable", signature);
        } catch (IOException ioexc) {
            assertTrue(false);
        }
        serializer = new SerializeFunctions();
        randomizer = new MakeRandom();
    }

    @Test
    public void testGets() {
        List<Object> toPut = randomizer.takeRandomValue(signature);
        DatabaseStoreable store = new DatabaseStoreable(toPut);

        for (int i = 0; i < signature.size(); ++i) {
            assertEquals(store.getColumnAt(i), toPut.get(i));
        }

        try {
            store.setColumnAt(-1, null);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobexc) {
            assertTrue(true);
        }

        try {
            store.setColumnAt(signature.size(), null);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobexc) {
            assertTrue(true);
        }
    }

    @After
    public void clearing() {
        dProvider.removeTable("newTable");
    }

}
