package ru.fizteh.fivt.students.kinanAlsarmini.shell;

class ChangeDirCommand extends ExternalCommand {
    public ChangeDirCommand() {
        super("cd", 1);
    }

    public void execute(String[] args, Shell shell) {
        shell.changeDirectory(args[0]);
    }
}
