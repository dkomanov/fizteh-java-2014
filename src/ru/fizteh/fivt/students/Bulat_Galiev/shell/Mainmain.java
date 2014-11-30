package ru.fizteh.fivt.students.Bulat_Galiev.shell;

public final class Mainmain {
    private Mainmain() {
        // not called
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            Mymainshell.interactiveMode();
        } else {
            Mymainshell.packageMode(args);
        }
    }
}
