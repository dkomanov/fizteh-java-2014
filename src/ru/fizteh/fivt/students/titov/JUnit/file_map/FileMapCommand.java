package ru.fizteh.fivt.students.titov.JUnit.file_map;

public abstract class FileMapCommand extends Command<FileMap> {
    public void initialize(String command_name, int n) {
        name = command_name;
        numberOfArguments = n;
    }
}
