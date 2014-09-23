package ru.fizteh.fivt.students.gudkov394.shell;

public class Exit {
    public Exit(String[] current_args) {
        if (current_args.length > 1) {
            System.err.println("extra arguments for exit");
            System.exit(1);
        }
        System.exit(0);
    }
}
