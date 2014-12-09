package ru.fizteh.fivt.students.moskupols.storeable;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlSerializerTest {

    @Test
    public void testSerialize() throws Exception {
        final XmlSerializer serializer = new XmlSerializer();
        final List<StoreableAtomType> signature = Arrays.asList(
                StoreableAtomType.BYTE,
                StoreableAtomType.STRING,
                StoreableAtomType.BOOLEAN,
                StoreableAtomType.FLOAT,
                StoreableAtomType.STRING
        );
        final Storeable stor = new StoreableImpl(signature);
        stor.setColumnAt(0, (byte) 42);
        stor.setColumnAt(1, "str");
        stor.setColumnAt(2, true);
        stor.setColumnAt(3, 55.8F);
        stor.setColumnAt(4, null);
        String expected =
                "<row>"
                + "<col>42</col>"
                + "<col>str</col>"
                + "<col>true</col>"
                + "<col>55.8</col>"
                + "<null/>"
                + "</row>";
        assertEquals(expected, serializer.serialize(stor));
    }
}
