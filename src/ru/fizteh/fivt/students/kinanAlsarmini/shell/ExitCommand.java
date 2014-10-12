package ru.fizteh.fivt.students.kinanAlsarmini.shell;

public class ExitCommand extends ExternalCommand {
    public ExitCommand() {
        super("exit", 0);
    }

    public void execute(String[] args, Shell shell) {
        shell.terminate();
    }
}
