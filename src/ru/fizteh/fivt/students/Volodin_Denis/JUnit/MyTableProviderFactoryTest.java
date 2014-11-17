package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyTableProviderFactoryTest {

    TableProviderFactory tpf;

    @Before
    public void setUp() {
        tpf = new MyTableProviderFactory();
    }

    @Test
    public void testCreate() throws Exception {
        String randomString = "dgzfgzdfbnfuzsfvzdvzuanrbunerbneron";
        if (!Paths.get(randomString).toFile().exists()) {
            tpf.create(randomString);
            Files.delete(Paths.get(randomString));
        } else {
            throw new Exception("test: create TableProvider");
        }
    }
}