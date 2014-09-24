package ru.fizteh.fivt.students.andreyzakharov.shell;

public class ExitCommand extends AbstractCommand {
    ExitCommand(Shell shell) {
        super("exit", shell);
    }

    @Override
    public void execute(String... args) {
        System.exit(0);
    }
}
