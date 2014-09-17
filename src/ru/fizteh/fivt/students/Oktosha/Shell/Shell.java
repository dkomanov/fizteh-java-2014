package ru.fizteh.fivt.students.Oktosha.Shell;

/**
 * The Shell class implements an application,
 * which partially emulates UNIX command line.
 */

public final class Shell {

    /**
     * This is a private constructor, which is never called.
     */
    private Shell() { }

    /**
     *
     * @param args are treated as shell commands
     *             if no arguments are given
     *             Shell runs in interactive mode
     */
    public static void main(final String[] args) {
        System.out.println("Hello, world!");
    }
}
