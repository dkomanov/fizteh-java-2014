package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 22:20
 */
public class Cat {
    /**
     * We write everything from file like UNIX cat
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param currentDirectory Current directory where we are now
     */
    public static void catRun(final String[] currentArgs, final CurrentDirectory currentDirectory) {
        if (currentArgs.length == 1) {
            System.err.println("cat: missing operand");
            System.exit(1);
        }
        for (int i = 1; i < currentArgs.length; ++i) {
            String read;
            File f = new File(currentDirectory.getCurrentDirectory(), currentArgs[i]);
            if (!f.exists()) {
                System.err.println("cat: " + f.toString() + ": No such file or directory");
                continue;
            }
            try {
                Scanner sc = new Scanner(f);
                while (sc.hasNextLine()) {
                    read = sc.nextLine();
                    System.out.println(read);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error: cannot read file");
            }
        }
    }
}
