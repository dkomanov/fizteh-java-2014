package ru.fizteh.fivt.students.ilivanov.shell;

class Main {
    public static void main(String[] args) {
        Shell shell = new Shell();
        if (args.length == 0)
            shell.runInteractive();
        else
            shell.runPackage(args);

    }
}
