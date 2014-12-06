package ru.fizteh.fivt.students.sautin1.multifilemap.shell;

/**
 * A typical shell command.
 * Created by sautin1 on 10/4/14.
 */
public abstract class AbstractShellCommand extends AbstractCommand<ShellState> {

    public AbstractShellCommand(String name, int minArgNumber, int maxArgNumber) {
        super(name, minArgNumber, maxArgNumber);
    }
}
