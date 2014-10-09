package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public interface Command {
    String name();
    void execute() throws StopProcess;
}
