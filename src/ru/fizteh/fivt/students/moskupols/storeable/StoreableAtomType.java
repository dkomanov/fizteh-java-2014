package ru.fizteh.fivt.students.moskupols.storeable;

/**
* Created by moskupols on 03.12.14.
*/
public enum StoreableAtomType {
    INT(Integer.class, "int"),
    DOUBLE(Double.class, "double"),
    BYTE(Byte.class, "byte"),
    STRING(String.class, "String"),
    LONG(Long.class, "long"),
    BOOLEAN(Boolean.class, "boolean"),
    FLOAT(Float.class, "float");

    private final Class<?> boxedClass;
    private final String printedName;

    private StoreableAtomType(Class<?> type, String name) {
        boxedClass = type;
        printedName = name;
    }

    public static StoreableAtomType withPrintedName(String name) {
        for (StoreableAtomType type : StoreableAtomType.values()) {
            if (type.getPrintedName().equals(name)) {
                return type;
            }
        }
        throw new EnumConstantNotPresentException(StoreableAtomType.class, name);
    }

    public static StoreableAtomType fromBoxedClass(Class<?> cls) {
        for (StoreableAtomType type : StoreableAtomType.values()) {
            if (type.getBoxedClass().equals(cls)) {
                return type;
            }
        }
        throw new EnumConstantNotPresentException(StoreableAtomType.class, cls.getSimpleName());
    }

    @Override
    public String toString() {
        return getPrintedName();
    }

    public Class<?> getBoxedClass() {
        return boxedClass;
    }

    public String getPrintedName() {
        return printedName;
    }
}
