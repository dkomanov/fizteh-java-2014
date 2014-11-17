package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class ExitCommand extends Command {
    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        throw new ExitCommandException();
    }
}
