package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.IOException;

public class Parser {
    private Parser() {
    }

    public static void parse(final String[] args, boolean flag) throws IOException, InterruptedException {
        switch (args[0]) {
            case "exit":
                System.exit(0);
            case "cd":
                Cd.run(args);
                break;
            case "mkdir":
                Mkdir.run(args);
                break;
            case "pwd":
                Pwd.run();
                break;
            case "rm":
                Rm.run(args, flag);
                break;
            case "cp":
                Cp.run(args, flag);
                break;
            case "mv":
                Mv.run(args);
                break;
            case "ls":
                Ls.run();
                break;
            case "cat":
                Cat.run(args);
                break;
            default:
                break;
        }
    }
}
