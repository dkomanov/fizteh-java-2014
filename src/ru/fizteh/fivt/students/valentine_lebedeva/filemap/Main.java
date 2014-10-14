package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

public final class Main {
    private Main() {
        // Never called. Added only for checkstyle.
    }

    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            Modes.interactive();
        } else {
            Modes.bath(args);
        }
    }
}
