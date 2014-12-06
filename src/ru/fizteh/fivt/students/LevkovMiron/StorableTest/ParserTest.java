package ru.fizteh.fivt.students.LevkovMiron.StorableTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Storeable.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Мирон on 11.11.2014 ru.fizteh.fivt.students.LevkovMiron.StorableTest.
 */
public class ParserTest {

    private CStoreable thisStoreable;
    private ArrayList<Object> values;
    private ArrayList<Class<?>> classes;
    private Parser parser;
    private Table table;

    private void drop(File f) {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (File inFile : f.listFiles()) {
                drop(inFile);
            }
        }
        f.delete();
    }

    @Before
    public void beforeTest() throws IOException {
        File f = new File("StoreableTestDir");
        drop(f);
        f.mkdir();
        parser = new Parser();
        classes = new ArrayList<>(Arrays.asList(Integer.class, Long.class, String.class,
                                    Double.class, Boolean.class, Byte.class));
        values = new ArrayList<>(Arrays.asList(1, 10L, "abc", 1.1d, true, 0));
        thisStoreable = new CStoreable(values);
        table = new CTableProviderFactory().create(f.getAbsolutePath()).createTable("t", classes);
    }

    @Test
    public void serializeTest() {
        CStoreable storeable = new CStoreable(values);
        Assert.assertEquals(parser.serialize(table, storeable), "[1,10,\"abc\",1.1,true,0]");
    }

    @Test
    public void deserializeTest() throws ParseException {
        CStoreable storeable = new CStoreable(values);
        Storeable result = parser.deserialize(table, "[1, 10, \"abc\", 1.1, true, 0]");
        for (int i = 0; i < values.size(); i++) {
            Assert.assertEquals(result.getColumnAt(i).toString(), storeable.getColumnAt(i).toString());
        }

    }

    @Test(expected = ParseException.class)
    public void deserializeExceptionTest() throws ParseException {
        parser.deserialize(table, "[1, 10, \"abc\", 1.1, true, 0");
    }


}
