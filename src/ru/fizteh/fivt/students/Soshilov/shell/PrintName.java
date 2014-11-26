package ru.fizteh.fivt.students.Soshilov.shell;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 03 October 2014
 * Time: 14:12
 */
public class PrintName {
    /**
     * Print name of current directory like UNIX pwd
     * @param cd Current directory where we are now
     */
    public static void printNameRun(final CurrentDirectory cd) {
//        pwd works in spite of extra arguments
//        if (currentArgs.length > 1) {
//            System.err.println("extra arguments are detected");
//            System.exit(1);
//        }
        System.out.println(cd.getCurrentDirectory());
    }
}
