package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class FileFromDataBaseIsNotDirectoryException extends Exception{
    public final String childName;
    FileFromDataBaseIsNotDirectoryException(String childNameConstructor) {
        childName = childNameConstructor;
    }
}
