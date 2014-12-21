package ru.fizteh.fivt.students.LevkovMiron.ParallelTest;

import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Parallel.CTableProviderFactory;

import java.io.File;

/**
 * Created by Мирон on 11.11.2014 ru.fizteh.fivt.students.LevkovMiron.StorableTest.
 */
public class TableProviderFactoryTest {
    @Test
    public void createTest() {
        CTableProviderFactory factory = new CTableProviderFactory();
        Assert.assertNull(factory.create(null));
        Assert.assertNull(factory.create("a/a/a/a"));
        Assert.assertNotNull(factory.create("TestDir"));
        new File("TestDir").delete();
    }
}
