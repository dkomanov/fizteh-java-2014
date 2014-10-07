package ru.fizteh.fivt.students.artem_gritsay.Shell;


public class Shell {
    public static void main(String args[ ]) {
        try {
            Funcshell f = new Funcshell();
            f.currentDirectory = System.getProperty("user.dir");
            if (args.length > 0) f.parsLine(args);
            else f.interactive();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
            }
    }
}
