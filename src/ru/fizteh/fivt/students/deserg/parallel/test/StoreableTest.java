package ru.fizteh.fivt.students.deserg.parallel.test;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.deserg.parallel.TableRow;

import java.util.ArrayList;

import static org.junit.Assert.*;
/**
 * Created by deserg on 04.12.14.
 */
public class StoreableTest {

    ArrayList<Class<?>> signature = new ArrayList<>();

    @Before
    public void init() {

        signature.add(Integer.class);
        signature.add(Long.class);
        signature.add(Byte.class);
        signature.add(Float.class);
        signature.add(Double.class);
        signature.add(Boolean.class);
        signature.add(String.class);

    }

    @Test
    public void testSetColumnAt() {
        Storeable value = new TableRow(signature);


        try {
            value.setColumnAt(20, 123);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.setColumnAt(0, "123");
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Test
    public void testGetColumnAt() {
        Storeable value = new TableRow(signature);

        try {
            value.getColumnAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testGetIntAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(0, 50);

        try {
            value.getIntAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getIntAt(1);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getIntAt(0);
        assertEquals(obj.getClass(), Integer.class);

    }

    @Test
    public void testGetLongAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(1, 50L);

        try {
            value.getLongAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getLongAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getLongAt(1);
        assertEquals(obj.getClass(), Long.class);
    }


    @Test
    public void testGetByteAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(2, (byte) 50);

        try {
            value.getByteAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getByteAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getByteAt(2);
        assertEquals(obj.getClass(), Byte.class);

    }


    @Test
    public void testGetFloatAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(3, 500.103F);

        try {
            value.getFloatAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getFloatAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getFloatAt(3);
        assertEquals(obj.getClass(), Float.class);

    }

    @Test
    public void testGetDoubleAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(4, 1341.241D);

        try {
            value.getDoubleAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getDoubleAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getDoubleAt(4);
        assertEquals(obj.getClass(), Double.class);
    }

    @Test
    public void testGetBooleanAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(5, true);

        try {
            value.getBooleanAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getBooleanAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getBooleanAt(5);
        assertEquals(obj.getClass(), Boolean.class);
    }

    @Test
    public void testGetStringAt() {
        Storeable value = new TableRow(signature);
        value.setColumnAt(6, "someString");

        try {
            value.getStringAt(20);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            value.getStringAt(0);
        } catch (ColumnFormatException ex) {
            System.out.println(ex.getMessage());
        }

        Object obj = value.getStringAt(6);
        assertEquals(obj.getClass(), String.class);
    }

}
