package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.FileMap;

import java.io.IOException;
import java.util.Map;

public abstract class Command {
    protected String[] args = null;
    protected String msg = null;
    protected Map<String, String> hashMap = FileMap.getFileHashMap();

    public Command(final String[] arguments) { args = arguments; }
    public String getName() { return args[0]; }
    public String getMsg() { return msg; }
    public abstract void execute() throws IOException;
}
