package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONComplexObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONField;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONMaker;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParsedObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParser;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class JSONTest {
    @Test
    public void testSimpleObject() throws ParseException {
        @JSONComplexObject
        class MyObj {
            @JSONField(name = "changedName")
            String stringField;

            @JSONField
            int intField;

            @JSONField
            double doubleField;

            @JSONField
            Boolean booleanField;

            @JSONField
            String stringField2;

            @JSONField
            boolean booleanField2;

            Object notRecordableField;
        }

        MyObj obj = new MyObj();
        obj.stringField = "hello";
        obj.intField = 123;
        obj.doubleField = -1.23;
        obj.booleanField = null;
        obj.stringField2 = "\"\\\\\\\"\"";
        obj.booleanField2 = false;

        String json = JSONMaker.makeJSON(obj);
        System.err.println(json);
        JSONParsedObject parsedObject = JSONParser.parseJSON(json);

        assertEquals(6, parsedObject.size());

        assertEquals(obj.stringField, parsedObject.getString("changedName"));
        assertEquals(obj.intField, (long) parsedObject.getLong("intField"));
        assertEquals(obj.doubleField, parsedObject.getDouble("doubleField"), 0.0);
        assertEquals(obj.booleanField, parsedObject.getBoolean("booleanField"));
        assertEquals(obj.stringField2, parsedObject.getString("stringField2"));
        assertEquals(obj.booleanField2, parsedObject.getBoolean("booleanField2"));
        assertFalse(parsedObject.containsField("notRecordableField"));
    }

    @Test
    public void testSimpleArray() throws ParseException {
        Object[] array = new Object[6];

        array[0] = "String";
        array[1] = true;
        array[2] = 12345678901234L;
        array[3] = 14.08;
        array[4] = new String[] {"a", "sdf", "asdfbs", "{}:,][[]]]"};
        array[5] = Arrays.asList(2L, false, -1.0, "string");

        String json = JSONMaker.makeJSON(array);
        JSONParsedObject parsedObject = JSONParser.parseJSON(json);

        assertEquals(array[0], parsedObject.get(0));
        assertEquals(array[1], parsedObject.get(1));
        assertEquals(array[2], parsedObject.get(2));
        assertEquals(array[3], parsedObject.get(3));
        assertArrayEquals((Object[]) array[4], parsedObject.getObject(4).asArray());
        assertArrayEquals(((List<Object>) array[5]).toArray(), parsedObject.getObject(5).asArray());
    }

    @Test
    public void testCyclicLinks() throws ParseException {
        CyclicA a = new CyclicA();
        CyclicB b = new CyclicB();

        a.b = b;
        b.a = a;

        String json = JSONMaker.makeJSON(a);
        assertEquals("{\"b\":{\"a\":cyclic}}", json);
    }

    @JSONComplexObject
    class CyclicA {
        @JSONField
        CyclicB b;
    }

    @JSONComplexObject
    class CyclicB {
        @JSONField
        CyclicA a;
    }
}
