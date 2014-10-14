package ru.fizteh.fivt.students.vadim_mazaev.shell;

public final class Main {
    private Main() {
        //not called
    }
    public static void main(final String[] args) {
        if (args.length == 0) {
            Shell.interactiveMode();
        } else {
            Shell.commandMode(args);
        }
    }
}
