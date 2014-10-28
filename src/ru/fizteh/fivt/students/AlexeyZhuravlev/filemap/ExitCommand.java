package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends Command {

    public int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBase base) throws ExitCommandException {
        throw new ExitCommandException();
    }
}
