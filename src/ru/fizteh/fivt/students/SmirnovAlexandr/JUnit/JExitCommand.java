package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.ExceptionExitCommand;

public class JExitCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        throw new ExceptionExitCommand();
    }

    protected final int arg = 0;

    protected int getArg() {
        return arg;
    }
}
