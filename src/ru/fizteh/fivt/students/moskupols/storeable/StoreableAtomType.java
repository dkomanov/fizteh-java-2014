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

    final Class<?> typeClass;
    final String printedName;

    StoreableAtomType(Class<?> type, String name) {
        typeClass = type;
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

    @Override
    public String toString() {
        return printedName;
    }
}
