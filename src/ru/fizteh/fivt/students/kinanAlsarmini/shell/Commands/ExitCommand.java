package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

public class ExitCommand<State> extends AbstractCommand<State> {
    public ExitCommand() {
        super("exit", "exit");
    }

    public void executeCommand(String params, State shellState) {
        System.out.println("good bye!");
        System.exit(0);
    }
}
