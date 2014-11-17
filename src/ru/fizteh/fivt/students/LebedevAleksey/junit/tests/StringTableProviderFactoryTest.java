package ru.fizteh.fivt.students.LebedevAleksey.junit.tests;

import org.junit.Test;
import ru.fizteh.fivt.students.LebedevAleksey.junit.StringTableProviderFactory;

public class StringTableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWhenCreateArgumentNull() {
        new StringTableProviderFactory().create(null);
    }
}
