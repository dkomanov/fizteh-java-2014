package ru.fizteh.fivt.students.Volodin_Denis.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ReturnCodes;

import static org.junit.Assert.*;

public class ReturnCodesTest {

    @Test
    public void testConstructor() {
        ReturnCodes rc = new ReturnCodes();
        assertEquals(rc.SUCCESS, ReturnCodes.SUCCESS);
    }
}
