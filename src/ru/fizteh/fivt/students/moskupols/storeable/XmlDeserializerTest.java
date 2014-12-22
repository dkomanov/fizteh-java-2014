package ru.fizteh.fivt.students.moskupols.storeable;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XmlDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        final XmlDeserializer deserializer = new XmlDeserializer();
        final List<StoreableAtomType> signature = Arrays.asList(
                StoreableAtomType.BYTE,
                StoreableAtomType.STRING,
                StoreableAtomType.BOOLEAN,
                StoreableAtomType.FLOAT,
                StoreableAtomType.STRING
        );
        String input =
                "<row>"
                + "<col>42</col>"
                + "<col>str</col>"
                + "<col>true</col>"
                + "<col>55.8</col>"
                + "<null/>"
                + "</row>";
        Storeable stor = deserializer.deserialize(signature, input);
        assertEquals(Byte.valueOf((byte) 42), stor.getByteAt(0));
        assertEquals("str", stor.getStringAt(1));
        assertEquals(true, stor.getBooleanAt(2));
        assertEquals(Float.valueOf(55.8F), stor.getFloatAt(3));
        assertNull(stor.getStringAt(4));
    }
}
