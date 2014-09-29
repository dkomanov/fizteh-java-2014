package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.IOException;

public class Parser {
    private Parser() {
    }

    public static void parse(final String[] args, boolean flag, boolean mode, int j) throws IOException {
        switch (args[0]) {
            case "exit":
                System.exit(0);
            case "cd":
                Cd.run(args, j);
                break;
            case "mkdir":
                Mkdir.run(args, j);
                break;
            case "pwd":
                Pwd.run(args, j);
                break;
            case "rm":
                Rm.run(args, flag, j);
                break;
            case "cp":
                Cp.run(args, flag, j);
                break;
            case "mv":
                Mv.run(args, j);
                break;
            case "ls":
                Ls.run(args, j);
                break;
            case "cat":
                Cat.run(args, j);
                break;
            default:
                System.err.println(args[0] + ": no such command");
                if (mode) {
                    System.exit(1);
                }
                break;
        }
    }
}
