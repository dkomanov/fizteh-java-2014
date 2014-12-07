/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;

public class Remove implements CommandInterface{
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 9;
    private static final int DOESNT_EXIST_EXIT_CODE = 10;
    private static final int CANT_DELETE_EXIT_CODE = 11;
    private static final int INVALID_ARGUMENTS_EXIT_CODE = 12;
    private static final int LISTFILES_EXCEPTION_EXIT_CODE = 13;
    private static final int NEED_R_OPTION_EXIT_CODE = 14;

    private void recursiveDelete(final File f) {
        File [] fileList = null;
        if (f.isDirectory()) {
            try {
                fileList = f.listFiles();
            } catch (NullPointerException exception) {
                System.err.println("listFiles exception");
                System.exit(LISTFILES_EXCEPTION_EXIT_CODE);
            }
            if (fileList != null) {
                for (File fileInDir : fileList) {
                    recursiveDelete(fileInDir);
                }
            }
        }
        if (!f.exists()) {
            System.err.println("file/directory " + f.getName() + " doesn't exist");
            System.exit(DOESNT_EXIST_EXIT_CODE);
        }
        if (!f.delete()) {
            System.err.println("can't delete " + f.getName());
            System.exit(CANT_DELETE_EXIT_CODE);
        }
    }

    public void apply(final String [] args, final CurrentDir dir) {
        if (args.length < 2 || args.length > 3) {
            System.err.println("invalid number of arguments for rm");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        if (args.length == 2) {
            File f = new File(dir.getCurrentDir(), args[1]);
            /*System.err.println("in Remove " + f.getPath());
            System.err.flush();*/
            if (f.isDirectory()) {
                System.err.println("can't remove directory without -r option");
                System.exit(NEED_R_OPTION_EXIT_CODE);
            }
            if (!f.exists()) {
                System.err.println("file/directory " + args[1] + " doesn't exist");
                System.exit(DOESNT_EXIST_EXIT_CODE);
            }
            if (!f.delete()) {
                System.err.println("can't delete " + args[1]);
                System.exit(CANT_DELETE_EXIT_CODE);
            }
            return;
        }
        if (!args[1].equals("-r")) {
            System.err.println("invalid arguments for rm");
            System.exit(INVALID_ARGUMENTS_EXIT_CODE);
        }
        File f = new File(dir.getCurrentDir(), args[2]);
        if (!f.exists()) {
            System.err.println("file/directory " + args[2] + " doesn't exist");
            System.exit(DOESNT_EXIST_EXIT_CODE);
        }
        recursiveDelete(f);
    }
    public Remove() {}
}
