package ru.fizteh.fivt.students.deserg.telnet;

/**
 * Created by deserg on 12.12.14.
 */
public enum StoreableMap {

    INT(Integer.class, "int"),
    DOUBLE(Double.class, "double"),
    BYTE(Byte.class, "byte"),
    STRING(String.class, "String"),
    LONG(Long.class, "long"),
    BOOLEAN(Boolean.class, "boolean"),
    FLOAT(Float.class, "float");

    private final Class<?> boxedClass;
    private final String printedName;

    private StoreableMap(Class<?> type, String name) {
        boxedClass = type;
        printedName = name;
    }

    public static StoreableMap getFromString(String name) {
        for (StoreableMap type : StoreableMap.values()) {
            if (type.getString().equals(name)) {
                return type;
            }
        }
        throw new EnumConstantNotPresentException(StoreableMap.class, name);
    }

    public static StoreableMap getFromClass(Class<?> cls) {
        for (StoreableMap type : StoreableMap.values()) {
            if (type.getBoxedClass().equals(cls)) {
                return type;
            }
        }
        throw new EnumConstantNotPresentException(StoreableMap.class, cls.getSimpleName());
    }

    @Override
    public String toString() {
        return getString();
    }

    public Class<?> getBoxedClass() {
        return boxedClass;
    }

    public String getString() {
        return printedName;
    }

}
