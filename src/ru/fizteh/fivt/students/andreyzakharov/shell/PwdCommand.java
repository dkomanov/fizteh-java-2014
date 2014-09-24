package ru.fizteh.fivt.students.andreyzakharov.shell;

public class PwdCommand extends AbstractCommand {
    PwdCommand(Shell shell) {
        super("pwd", shell);
    }

    @Override
    public void execute(String... args) {
        shell.output(shell.getWd().toString());
    }
}
