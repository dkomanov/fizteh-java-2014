package ru.fizteh.fivt.students.hromov_igor.shell;

import ru.fizteh.fivt.students.hromov_igor.shell.cmd.*;

public class StringParser {

    public static void parse(String[] strParsed, boolean fl) {
        try {
            switch (strParsed[0]) {
            case "exit":
                System.exit(0);
            case "ls":
                Ls.run(strParsed);
                break;
            case "pwd":
                Pwd.run(strParsed);
                break;
            case "cat":
                Cat.run(strParsed);
                break;
            case "cp":
                Cp.run(strParsed);
                break;
            case "rm":
                Rm.run(strParsed);
                break;
            case "mkdir":
                Mkdir.run(strParsed);
                break;
            case "cd":
                Cd.run(strParsed);
                break;
            case "mv":
                Mv.run(strParsed);
                break;
            default:
                System.err.println(strParsed[0] + " : no such command");
                if (fl) {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (fl) {
                System.exit(1);
            }
        }
    }

}
