package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

public class ExitCommand extends Command {
    boolean execute(String[] args) {
        System.exit(Shell.errorOccurred ? 1 : 0);
        return true;
    }

    ExitCommand() {
        name = "exit";
        maxNumberOfArguements = 1;
    }
}
