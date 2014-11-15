package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

public interface AbstractCommandExecutor {
    void executeNextCommand(CommandGetter getter, DataBaseDir dbDir) throws Exception;
}

