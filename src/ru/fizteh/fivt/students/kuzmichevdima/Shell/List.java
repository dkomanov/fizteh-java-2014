/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;

public class List implements CommandInterface{
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 17;
    private static final int LISTFILES_EXCEPTION_EXIT_CODE = 18;
    public void apply(final String [] args, final CurrentDir dir) {
        if (args.length > 1) {
            System.err.println("invalid number of arguments for ls");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        File f = new File(dir.getCurrentDir());
        File [] fileList = null;
        try {
            fileList = f.listFiles();
        } catch (NullPointerException exception) {
            System.err.println("listFiles exception");
            System.exit(LISTFILES_EXCEPTION_EXIT_CODE);
        }
        if (fileList != null) {
            for (File fileFromList : fileList) {
                System.out.println(fileFromList.getName());
            }
        }
    }
    public List() {}
}
