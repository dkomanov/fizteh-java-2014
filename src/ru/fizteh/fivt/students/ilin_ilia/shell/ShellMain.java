package ru.fiztech.fivt.students.theronsg.shell;

public final class ShellMain {
    public static void main(String[] args) {
        if (args.length == 0) {
            Shell.interactiveMode();
        } else {
            Shell.commandMode(args);
        }
    }
}
