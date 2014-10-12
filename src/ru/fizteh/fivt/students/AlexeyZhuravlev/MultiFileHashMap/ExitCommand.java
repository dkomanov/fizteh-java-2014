package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends Command {
    @Override
    public void execute(DataBaseDir base) throws Exception {
        throw new ExitCommandException();
    }
}
