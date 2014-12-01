package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

public class ExceptionFileFromDataBaseIsNotDirectory extends Exception{
    public final String childName;
    ExceptionFileFromDataBaseIsNotDirectory(String childNameConstructor) {
        childName = childNameConstructor;
    }
}
