package ru.fizteh.fivt.students.standy66_new.tests.task5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableSignature;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class SerializationTest {
    private TableSignature signature;
    private TableRow original;
    private String serialized;
    private String canonicalRepresentation;


    public SerializationTest(TableSignature signature, Object[] values, String canonicalRepresentation) {
        this.signature = signature;
        this.canonicalRepresentation = canonicalRepresentation;
        original = new TableRow(signature);
        for (int i = 0; i < values.length; i++) {
            original.setColumnAt(i, values[i]);
        }
        serialized = original.serialize();

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        new TableSignature(Integer.class, Double.class),
                        new Object[]{null, 3.14d},
                        "[null, 3.14]"
                },
                {
                        new TableSignature(Integer.class, Long.class, Byte.class,
                                Float.class, Double.class, Boolean.class, String.class),
                        new Object[]{Integer.MIN_VALUE, Long.MIN_VALUE, Byte.MIN_VALUE,
                                Float.MIN_VALUE, Double.MIN_VALUE, false, "abacaba"},
                        String.format("[%s, %s, %s, %s, %s, %s, %s]",
                                Integer.MIN_VALUE, Long.MIN_VALUE, Byte.MIN_VALUE,
                                Float.MIN_VALUE, Double.MIN_VALUE, "false", "\"abacaba\"")
                },
                {
                        new TableSignature(Double.class, Double.class, Double.class),
                        new Object[]{Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY},
                        String.format("[%s, %s, %s]",
                                Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
                },
                {
                        new TableSignature(Integer.class, Long.class, Byte.class, Float.class,
                                Double.class, Boolean.class, String.class),
                        new Object[]{10, 12345678912345678L, (byte) -128, 3.14f, 100500d, true, ""},
                        "[10, 12345678912345678, -128, 3.14, 100500.0, true, \"\"]"
                },
                {
                        new TableSignature(String.class, String.class),
                        new Object[]{"\"][", null},
                        "[\"\"][\", null]"
                },

        });

    }

    @Test
    public void test() throws Exception {
        assertEquals(canonicalRepresentation, serialized);
        System.out.println(canonicalRepresentation);
        assertEquals(original, TableRow.deserialize(signature, serialized));
    }

}
