package ru.fizteh.fivt.students.dnovikov.shell;

public class Main {
    public static void main(String[] args) {
        Shell s = new Shell();
        if (args.length == 0)
            s.interactiveMode();
        else
            s.packageMode(args);
    }
}
