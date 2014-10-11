package ru.fizteh.fivt.students.andrewzhernov.shell;

public class Pwd {
    public static void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Usage: pwd");
        } else {
            System.out.println(System.getProperty("user.dir"));
        }
    }
}
