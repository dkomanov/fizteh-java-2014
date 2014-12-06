package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

/**
 * @author AlexeyZhuravlev
 */
public class ExitTableCommand extends TableCommand {

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        throw new ExitCommandException();
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
