package ru.fizteh.fivt.students.MaksimovAndrey.shell;

public class Main {
    public static void main(String[] args) {
        Shell wShell = new Shell();

        boolean check = true;

        if (args.length != 0) {
            check = wShell.batch(args);
        } else {
            check = wShell.interactive();
        }

        if (!check) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
