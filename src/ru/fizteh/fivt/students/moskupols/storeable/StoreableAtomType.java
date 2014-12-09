package ru.fizteh.fivt.students.moskupols.storeable;

/**
* Created by moskupols on 03.12.14.
*/
enum StoreableAtomType {
    INT(Integer.class, "int"),
    DOUBLE(Double.class, "double"),
    BYTE(Byte.class, "byte"),
    STRING(String.class, "String"),
    LONG(Long.class, "long"),
    BOOLEAN(Boolean.class, "boolean"),
    FLOAT(Float.class, "float");

    final Class<?> boxedClass;
    final String printedName;

    StoreableAtomType(Class<?> type, String name) {
        boxedClass = type;
        printedName = name;
    }

    static StoreableAtomType withPrintedName(String name) {
        for (StoreableAtomType type : StoreableAtomType.values()) {
            if (type.printedName.equals(name)) {
                return type;
            }
        }
        return null;
    }

    static StoreableAtomType fromBoxedClass(Class<?> cls) {
        for (StoreableAtomType type : StoreableAtomType.values()) {
            if (type.boxedClass.equals(cls)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return printedName;
    }
}
