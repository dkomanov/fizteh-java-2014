package ru.fizteh.fivt.students.kinanAlsarmini.shell;

class PrintDirCommand extends ExternalCommand {
    public PrintDirCommand() {
        super("pwd", 0);
    }

    public void execute(String[] args, Shell shell) {
        System.out.println(shell.getCurrentPath().toString());
    }
}
