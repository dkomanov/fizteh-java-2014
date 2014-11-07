package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataBaseTableTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void Low() {
        Assert.assertEquals(4, 2 * 2);
    }
}