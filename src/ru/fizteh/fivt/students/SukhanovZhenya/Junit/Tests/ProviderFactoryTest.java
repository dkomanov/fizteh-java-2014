package ru.fizteh.fivt.students.SukhanovZhenya.Junit.Tests;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Junit.ProviderFactory;

public class ProviderFactoryTest {

    private static ProviderFactory testing;

    @Before
    public void setUp() throws Exception {
        testing = new ProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        testing.create(null);
    }

    @Test
    public void testCreateNotNull() {
        Assert.assertNotNull(testing.create(System.getProperty("fizteh.db.dir")));
    }
}
