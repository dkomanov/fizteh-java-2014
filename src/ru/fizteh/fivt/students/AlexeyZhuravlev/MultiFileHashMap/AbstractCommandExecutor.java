package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public interface AbstractCommandExecutor {
    void executeNextCommand(CommandGetter getter, DataBaseDir dbDir) throws Exception;
}
