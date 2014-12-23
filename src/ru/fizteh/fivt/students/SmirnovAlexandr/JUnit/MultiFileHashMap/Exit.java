package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

public class Exit extends Command {

    protected int numberOfArguments() {
        return 0;
    }
    @Override
    public void execute(DataBaseDir base) throws Exception {
        throw new ExceptionExitCommand();
    }
}
