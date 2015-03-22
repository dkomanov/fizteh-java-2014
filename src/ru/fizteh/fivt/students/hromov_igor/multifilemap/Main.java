package ru.fizteh.fivt.students.hromov_igor.multifilemap;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.*;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                new Interpreter();
            } else {
                new Interpreter(args);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
