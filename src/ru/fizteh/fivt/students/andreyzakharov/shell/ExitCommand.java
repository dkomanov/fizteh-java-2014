package ru.fizteh.fivt.students.andreyzakharov.shell;

public class ExitCommand extends AbstractCommand {
    ExitCommand(Shell shell) {
        super(shell);
        identifier = "exit";
    }

    @Override
    public void execute(String... args) {
        System.exit(0);
    }
}