package ru.fizteh.fivt.students.valentine_lebedeva.shell;


public final class Main {
    public static void main (final String[] args) throws Exception {
        try {
            if (args.length == 0) {
                Modes.interactive();
            } else {
                Modes.bath(args);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    private Main() {
    }
}
