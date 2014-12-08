package ru.fizteh.fivt.students.akhtyamovpavel.shell;

public class ShellMain {

    public static void main(String[] args) {
        Shell shell = new Shell();

        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
