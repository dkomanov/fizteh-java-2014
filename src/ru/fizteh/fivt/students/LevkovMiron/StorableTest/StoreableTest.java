package ru.fizteh.fivt.students.LevkovMiron.StorableTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.LevkovMiron.Storeable.CStoreable;
import ru.fizteh.fivt.students.LevkovMiron.Storeable.ColumnFormatException;

import java.util.ArrayList;

/**
 * Created by Мирон on 27.10.2014 PACKAGE_NAME.
 */
public class StoreableTest {
    private CStoreable thisStoreable;
    private ArrayList<Object> classes = new ArrayList<>();

    @Before
    public void beforeTest() {
        classes.add(1);
        classes.add(10L);
        classes.add("abc");
        classes.add(1.1d);
        classes.add(true);
        classes.add(0);
        thisStoreable = new CStoreable(classes);
    }

    @Test
    public void setTest() {
        thisStoreable.setColumnAt(0, 20);
        Assert.assertEquals(thisStoreable.getColumnAt(0), 20);
    }

    @Test(expected = ColumnFormatException.class)
    public void setExceptionTest() {
        thisStoreable.setColumnAt(0, "@");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setExceptionTest2() {
        thisStoreable.setColumnAt(100, 20);
    }

    @Test
    public void getDoubleTest() {
        Assert.assertEquals((Double) 1.1, thisStoreable.getDoubleAt(3));
    }
}
