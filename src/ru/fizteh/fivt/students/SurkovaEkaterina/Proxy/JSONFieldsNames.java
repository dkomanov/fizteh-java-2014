package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy;

/**
 * Created by kate on 02.12.14.
 */
enum JSONFieldsNames {
    TIMESTAMP("timestamp"),
    CLASS("class"),
    METHOD("method"),
    ARGUMENTS("arguments"),
    RETURN_VALUE("returnValue"),
    THROWN("thrown"),
    CYCLIC("cyclic");

    public String name;

    JSONFieldsNames(String name) {
        this.name = name;
    }
}
