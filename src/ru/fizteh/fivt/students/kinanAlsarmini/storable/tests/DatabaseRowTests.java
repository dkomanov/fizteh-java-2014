package ru.fizteh.fivt.students.kinanAlsarmini.storable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseRow;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRowTests {
    Storeable storeable;

    @Before
    public void setUp() {
        List<Class<?>> columnTypes = new ArrayList<>();

        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        storeable = new DatabaseRow(columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        storeable = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValueShouldFail() {
        storeable.setColumnAt(1, "    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putEmptyValueShouldFail() {
        storeable.setColumnAt(1, "");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putValueOutOfBound() {
        storeable.setColumnAt(3, null);
    }

    @Test
    public void putNullValueShouldPass() {
        storeable.setColumnAt(0, null);
        storeable.setColumnAt(1, null);
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType1ShouldFail() {
        storeable.setColumnAt(0, "adasda");
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType2ShouldFail() {
        storeable.setColumnAt(1, 2);
    }

    @Test(expected = ColumnFormatException.class)
    public void putIncorrectType3ShouldFail() {
        storeable.setColumnAt(1, new SimpleClass());
    }

    @Test
    public void putCorrectValueShouldPass() throws Exception {
        storeable.setColumnAt(1, "String");
    }
}

class SimpleClass {
}
