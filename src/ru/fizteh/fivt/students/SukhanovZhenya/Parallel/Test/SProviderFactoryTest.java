package ru.fizteh.fivt.students.SukhanovZhenya.Parallel.Test;

import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SProviderFactory;

public class SProviderFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws Exception {
        SProviderFactory test = new SProviderFactory();
        test.create(null);
    }
}