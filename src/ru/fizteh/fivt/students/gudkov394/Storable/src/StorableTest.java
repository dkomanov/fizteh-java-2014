package ru.fizteh.fivt.students.gudkov394.Storable.src;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;

/**
 * Created by kagudkov on 10.11.14.
 */
public class StorableTest {
    TableContents storable = null;
    Utils utils = new Utils();

    @Before
    public void createStorable() throws IOException {
        List<Object> objects = new ArrayList<Object>();
        objects.add(Integer.class);
        objects.add(Long.class);
        objects.add(Byte.class);
        objects.add(Float.class);
        objects.add(Double.class);
        objects.add(Boolean.class);
        objects.add(String.class);
        storable = new TableContents(objects);
    }

    @Test
    public void goodType() {
        Integer a = 1;
        storable.setColumnAt(0, a);
        storable.setColumnAt(1, Long.valueOf(2));
        storable.setColumnAt(2, Byte.valueOf((byte) 3));
        storable.setColumnAt(3, Float.valueOf((float) 1.2));
        storable.setColumnAt(4, Double.valueOf(1.02));
        storable.setColumnAt(5, true);
        storable.setColumnAt(6, "I need sleep");
        Assert.assertEquals(storable.getIntAt(0), (Integer) 1);
        Assert.assertEquals((long) storable.getLongAt(1), ((long) 2));
        Assert.assertEquals(storable.getByteAt(2), Byte.valueOf((byte) 3));
        Assert.assertEquals(storable.getFloatAt(3), Float.valueOf((float) 1.2));
        Assert.assertEquals(storable.getDoubleAt(4), (Double) 1.02);
        Assert.assertEquals(storable.getBooleanAt(5), true);
        Assert.assertEquals(storable.getStringAt(6), "I need sleep");
    }

}
