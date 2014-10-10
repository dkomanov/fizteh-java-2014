package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.FileMap;

import java.io.IOException;
import java.util.Map;

abstract public class Command {
    protected final String[] args;
    protected Map<String, String> hashMap = FileMap.getFileHashMap();

    public Command(String[] args_) { args = args_; }
    public String getName() { return args[0]; }
    abstract public void execute() throws IOException;
}