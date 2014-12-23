package ru.fizteh.fivt.students.titov.JUnit.file_map;

public abstract class FileMapCommand extends Command<FileMap> {
    public void initialize(String commandName, int n) {
        name = commandName;
        numberOfArguments = n;
    }
}
