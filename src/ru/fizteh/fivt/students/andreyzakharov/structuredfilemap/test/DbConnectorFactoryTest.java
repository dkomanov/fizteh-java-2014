package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.DbConnectorFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DbConnectorFactoryTest {
    String root = "/home/norrius/Apps/fizteh-java-2014/test/junit";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testCreate() throws Exception {
        DbConnectorFactory f = new DbConnectorFactory();

        assertNotNull(f.create(root));
    }

    @Test
    public void testCreateNonExisting() throws Exception {
        DbConnectorFactory f = new DbConnectorFactory();

        assertNull(f.create("/zzz/arguably/non/existing/directory"));
    }
}
