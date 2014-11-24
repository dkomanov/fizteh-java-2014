package ru.fizteh.fivt.students.vadim_mazaev.DataBaseTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class contains static constants, which used in data base tests.
 */
public final class TestHelper {
    //Paths.
    public static final Path TEST_DIR
            = Paths.get(System.getProperty("java.io.tmpdir"), "DbTestDir");
    
    //Strings.
    public static final String TEST_TABLE_NAME = "testTable";
    public static final String STRUCTURE_STRING
            = "int long byte float double boolean String";
    public static final String SERIALIZED_VALUES = "[" + Integer.MAX_VALUE
            + ", " + Long.MAX_VALUE + ", " + Byte.MAX_VALUE
            + ", " + Float.MAX_VALUE + ", " + Double.MAX_VALUE
            + ", " + Boolean.TRUE + ", " + "\"test_value\"" + "]";
    public static final String MIXED_SERIALIZED_VALUES
            = "[0, 0.0, 0, true, \"string\", 0, 0.0]"; 
    public static final String SERIALIZED_STRING_WITH_INCORRECT_BOOL_TOKEN
            = "[0, 0, 0, 0.0, 0.0, notBoolean, \"string\"]";
    public static final String SERIALIZED_STRING_WITH_INCORRECT_NUMBER_TOKEN
            = "[0, 0, 0.5, 0.0, 0.0, true, \"string\"]";
    public static final String SERIALIZED_STRING_WITH_INCORRECT_STRING_TOKEN
            = "[0, 0, 0, 0.0, 0.0, true, unquoted]";
    public static final String SERIALIZED_NULL_VALUES
            = "[null, null, null, null, null, null, null]";
    
    //Maps, lists, etc.
    public static final List<Class<?>> STRUCTURE;
    static {
        List<Class<?>> unitializerList = new ArrayList<>();
        unitializerList.add(Integer.class);
        unitializerList.add(Long.class);
        unitializerList.add(Byte.class);
        unitializerList.add(Float.class);
        unitializerList.add(Double.class);
        unitializerList.add(Boolean.class);
        unitializerList.add(String.class);
        STRUCTURE = Collections.unmodifiableList(unitializerList);
    }
    public static final List<Class<?>> MIXED_STRUCTURE;
    static {
        List<Class<?>> unitializerList = new ArrayList<>();
        unitializerList.add(Integer.class);
        unitializerList.add(Float.class);
        unitializerList.add(Long.class);
        unitializerList.add(Boolean.class);
        unitializerList.add(String.class);
        unitializerList.add(Byte.class);
        unitializerList.add(Double.class);
        MIXED_STRUCTURE = Collections.unmodifiableList(unitializerList);
    }
    public static final List<Object> TEST_VALUES;
    static {
        List<Object> unitializerList = new ArrayList<>();
        unitializerList.add(Integer.MAX_VALUE);
        unitializerList.add(Long.MAX_VALUE);
        unitializerList.add(Byte.MAX_VALUE);
        unitializerList.add(Float.MAX_VALUE);
        unitializerList.add(Double.MAX_VALUE);
        unitializerList.add(Boolean.TRUE);
        unitializerList.add("test_value");
        TEST_VALUES = Collections.unmodifiableList(unitializerList);
    }
}
