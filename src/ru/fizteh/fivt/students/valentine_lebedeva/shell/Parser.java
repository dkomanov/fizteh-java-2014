package ru.fizteh.fivt.students.valentine_lebedeva.shell;

import java.io.File;
import java.io.IOException;

public final class Parser {
    public static void parse(final String cmdArgs,
            final Boolean cmdMode) throws Exception {
        try {
            String[] parseArgs = cmdArgs.split(" ");
            switch (parseArgs[0]) {
            case "cd":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Cd.execute(parseArgs);
                break;
            case "mkdir":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Mkdir.execute(parseArgs);
                break;
            case "pwd":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Pwd.execute(parseArgs);
                break;
            case "rm":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Rm.execute(parseArgs);
                break;
            case "cp":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Cp.execute(parseArgs);
                break;
            case "mv":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Mv.execute(parseArgs);
                break;
            case "ls":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Ls.execute(parseArgs);
                break;
            case "exit":
                System.exit(0);
            case "cat":
                ru.fizteh.fivt.students.valentine_lebedeva.shell
                .Cmd.Cat.execute(parseArgs);
                break;
            default:
                throw new Exception("Incorrect arguments");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (cmdMode) {
                System.exit(1);
            }
        }
    }

    public static int step(final String arg) {
        if (arg.equals("-r")) {
            return 2;
        } else {
            return 1;
        }
    }

    public static File getFile(final String arg) throws IOException {
        File f = new File(arg);
        if (!f.isAbsolute()) {
            f = new File(System.getProperty("user.dir"), arg);
        }
        return f.getCanonicalFile();
    }

    private Parser() {
    }
}
