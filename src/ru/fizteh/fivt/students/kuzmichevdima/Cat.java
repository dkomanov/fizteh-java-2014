/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Cat implements CommandInterface{
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 20;
    private static final int DOESNT_EXIST_EXIT_CODE = 21;
    private static final int CAT_EXCEPTION_EXIT_CODE = 22;
    public void apply(final String [] args, final CurrentDir dir) {
        if (args.length != 2) {
            System.err.println("invalid number of arguments for cat");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        File f = new File(dir.getCurrentDir(), args[1]);
        if (!f.exists()) {
            System.err.println("file " + f.getName() + " doesn't exist");
            System.exit(DOESNT_EXIST_EXIT_CODE);
        }
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                System.out.println(str);
            }
            sc.close();
        } catch (FileNotFoundException exception) {
            System.err.println("cat exception");
            System.exit(CAT_EXCEPTION_EXIT_CODE);
        }
    }
    public Cat(){}
}
