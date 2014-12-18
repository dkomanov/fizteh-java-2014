package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Serializer;

public class SerializerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = ColumnFormatException.class)
    public void testSerializerThrowsExceptionSettingIncorrectTypeOfObject() {
        Class<?>[] structure = {Integer.class};
        Serializer serializer = new Serializer(Arrays.asList(structure));
        serializer.setColumnAt(0, 5.5);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSerializerThrowsExceptionSettingObjectAtIncorrectIndex() {
        Class<?>[] structure = {Integer.class, String.class};
        Serializer serializer = new Serializer(Arrays.asList(structure));
        serializer.setColumnAt(2, null);
    }
    
    @Test
    public void testSerializerSetColumnAtMethodForCorrectTypes() {
        Class<?>[] structure = {Integer.class, String.class};
        Serializer serializer = new Serializer(Arrays.asList(structure));
        serializer.setColumnAt(0, 5);
        serializer.setColumnAt(1, null);
    }
    
    @Test
    public void testSerializerGetColumnAtMethodForNullObjects() {
        Class<?>[] structure = {Integer.class, Byte.class, Float.class, 
                Double.class, Long.class, Boolean.class, String.class};
        Serializer serializer = new Serializer(Arrays.asList(structure));
        for (int i = 0; i < structure.length; i++) {
            serializer.setColumnAt(i, null);
        }
        for (int i = 0; i < structure.length; i++) {
            assertEquals(null, serializer.getColumnAt(i));
        }
        assertEquals(null, serializer.getIntAt(0));
        assertEquals(null, serializer.getByteAt(1));
        assertEquals(null, serializer.getFloatAt(2));
        assertEquals(null, serializer.getDoubleAt(3));
        assertEquals(null, serializer.getLongAt(4));
        assertEquals(null, serializer.getBooleanAt(5));
        assertEquals(null, serializer.getStringAt(6));
    }
    
    @Test
    public void testSerializerGetColumnAtMethodForNonNullObjects() {
        Class<?>[] structure = {Integer.class, Byte.class, Float.class, 
                Double.class, Long.class, Boolean.class, String.class};
        Serializer serializer = new Serializer(Arrays.asList(structure));
        serializer.setColumnAt(0, 0);
        serializer.setColumnAt(1, (byte) 0);
        serializer.setColumnAt(2, (float) 0.0);
        serializer.setColumnAt(3, 0.0);
        serializer.setColumnAt(4, (long) 0);
        serializer.setColumnAt(5, false);
        serializer.setColumnAt(6, "abc");
        assertEquals(Integer.valueOf(0), serializer.getIntAt(0));
        assertEquals(Byte.valueOf((byte) 0), serializer.getByteAt(1));
        assertEquals(Float.valueOf(0), serializer.getFloatAt(2));
        assertEquals(Double.valueOf(0), serializer.getDoubleAt(3));
        assertEquals(Long.valueOf(0), serializer.getLongAt(4));
        assertEquals(false, serializer.getBooleanAt(5));
        assertEquals("abc", serializer.getStringAt(6));
    }
    
    @After
    public void tearDown() throws Exception {
    }

}
