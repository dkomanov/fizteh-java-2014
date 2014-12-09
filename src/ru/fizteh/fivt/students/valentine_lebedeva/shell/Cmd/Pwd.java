package ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd;

public final class Pwd {
    public static void execute(final String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        System.out.println(System.getProperty("user.dir"));
    }

    private Pwd() {
    }
}
