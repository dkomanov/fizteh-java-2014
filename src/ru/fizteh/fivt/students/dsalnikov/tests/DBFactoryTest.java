package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.DBFactory;

public class DBFactoryTest {
    DBFactory testfactory;

    @Before
    public void setUpTestObject() {
        testfactory = new DBFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableFactoryTest() {
        testfactory.create(null);
    }
}
