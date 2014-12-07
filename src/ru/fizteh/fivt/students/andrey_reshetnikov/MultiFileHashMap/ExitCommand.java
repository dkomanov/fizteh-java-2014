package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class ExitCommand extends Command {

    protected int numberOfArguments() {
        return 0;
    }
    @Override
    public void execute(DataBaseDir base) throws Exception {
        throw new ExitCommandException();
    }
}
