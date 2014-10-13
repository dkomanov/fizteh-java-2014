package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

public final class Main {
    private Main() {
         //not called only for checkstyle
    }

    public static void main(final String[] args) throws Exception {
         try {
             if (args.length == 0) {
                 Modes.interactive();
             } else {
                 Modes.bath(args);
             }
         } catch (Exception e) {
             System.err.println(e.getMessage());
         }
    }
}
