package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends Command {

    protected int numberOfArguments() {
        return 0;
    }
    @Override
    public void execute(DataBaseDir base) throws Exception {
        throw new ExitCommandException();
    }
}
