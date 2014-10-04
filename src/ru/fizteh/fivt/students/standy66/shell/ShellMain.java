package ru.fizteh.fivt.students.standy66.shell;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author standy66
 */
public final class ShellMain {
    /**
     * Hiding default constructor.
     */
    private ShellMain() {

    }

    /**
     * Shell's entry point.
     *
     * @param args program arguments
     */
    public static void main(final String[] args) {
        Shell shell;
        if (args.length == 0) {
            shell = new Shell(System.in, true);
        } else {
            String params = Stream.of(args).collect(Collectors.joining(" "));
            shell = new Shell(new ByteArrayInputStream(params.getBytes()), false);
        }
        shell.run();

    }
}
