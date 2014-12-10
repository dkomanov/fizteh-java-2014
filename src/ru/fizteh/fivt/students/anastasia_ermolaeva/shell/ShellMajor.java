package ru.fizteh.fivt.students.anastasia_ermolaeva.shell;

final class ShellMajor {
    private ShellMajor() {
        //
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            Shell.userMode();
        } else {
            Shell.batchMode(args);
        }
    }
}
