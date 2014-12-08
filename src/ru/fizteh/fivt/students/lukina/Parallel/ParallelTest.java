package ru.fizteh.fivt.students.lukina.Parallel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.lukina.DataBase.DBase;
import ru.fizteh.fivt.students.lukina.DataBase.DBaseProviderFactory;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertSame;

public class ParallelTest {
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    DBase test;
    TableProvider prov;
    DBaseProviderFactory fact;
    Storeable row;

    @Before
    public void initializeAndFillingBase() {
        fact = new DBaseProviderFactory();
        try {
            prov = fact.create(root.newFolder().toString());
        } catch (IllegalArgumentException | IOException e1) {
            // not OK
        }
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        try {
            test = (DBase) prov.createTable("testTable", list);
        } catch (IOException e) {
            // not OK
        }
        row = prov.createFor(test);
    }

    @Test
    public void testGet() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "2");
        test.put("1", row);
        test.put("2", row);
        test.put("3", row);
        test.put("4", row);
        test.put("5", row);
        Tread t = new Tread(root, prov, fact);
        t.run(row);
        assertSame(test.get("6").getStringAt(0), "3");
    }

    ;

    private class Tread extends Thread {
        private DBase test;

        Tread(TemporaryFolder root, TableProvider prov, DBaseProviderFactory fact) {
            ArrayList<Class<?>> list = new ArrayList<>();
            list.add(String.class);
            test = (DBase) prov.getTable("testTable");
        }

        public void run(Storeable row) {
            row.setColumnAt(0, "3");
            test.put("6", row);
            try {
                test.commit();
            } catch (IOException e) {
                System.out.println("fail commit");
            }
        }
    }
};

    


