package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class CommandExecutor implements AbstractCommandExecutor{
    @Override
    public final void executeNextCommand(CommandGetter getter, DataBaseDir dbDir) throws Exception {
        String s = getter.nextCommand();
        Command command = Command.fromString(s);
        command.execute(dbDir);
    }
}
