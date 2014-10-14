package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

/**
 * Created by kuzmi_000 on 14.10.14.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Copy {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 23;
    private static final int NO_SRC_EXIT_CODE = 24;
    private static final int SRC_DEST_EQ_EXIT_CODE = 25;
    private static final int COPY_EXCEPTION_EXIT_CODE = 26;
    private static final int INVALID_ARGUMENTS_EXIT_CODE = 27;
    private static final int MINUS_R_WITH_FILE_EXIT_CODE = 28;
    private static final int LISTFILES_EXCEPTION_EXIT_CODE = 29;
    private static final int NEED_R_OPTION_EXIT_CODE = 30;

    private void recursiveCopy(final File src, final File dest) {
        CopyOption [] option = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
        try {
            Files.copy(src.toPath(), dest.toPath(), option);
        } catch (IOException exception) {
            System.err.println("copy exception");
            System.exit(COPY_EXCEPTION_EXIT_CODE);
        }
        if (src.isDirectory()) {
            File [] fileList = null;
            try {
                fileList = src.listFiles();
            } catch (NullPointerException exception) {
                System.err.println("listFiles exception in recursive copy");
                System.exit(LISTFILES_EXCEPTION_EXIT_CODE);
            }
            if (fileList != null) {
                for (File fileInList : fileList) {
                    File newDest = new File(dest.getAbsolutePath(), fileInList.getName());
                    recursiveCopy(fileInList, newDest);
                }
            }
        }

    }

    public Copy(final String [] args, final CurrentDir dir) {
        if (args.length > 4 || args.length < 3) {
            System.err.println("invalid number of arguments for cp");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        /*System.err.println("Copy args.length = " + args.length);
        for (int j = 1; j < args.length; j++)
            System.err.println("args number " + j + " " + args[j]);*/

        File dest;
        File src;
        if (args.length == 3) {
            src = new File(args[1]);
            dest = new File(args[2]);
        } else {
            src = new File(args[2]);
            dest = new File(args[3]);
        }
        if (!src.isAbsolute()) {
            src = new File(dir.getCurrentDir(), src.getPath());
        }
        if (!src.exists()) {
            System.err.println("there is no such file/directory: " + src.getPath());
            System.exit(NO_SRC_EXIT_CODE);
        }
        if (!dest.isAbsolute()) {
            dest = new File(dir.getCurrentDir(), dest.getPath());
        }
        if (dest.toString().equals(src.toString())) {
            System.err.println("source and destination are the same ");
            System.exit(SRC_DEST_EQ_EXIT_CODE);
        }
        CopyOption [] option = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
        if (args.length == 3) {
            if (src.isDirectory()) {
                    System.err.println("can't copy directory without -r option ");
                    System.exit(NEED_R_OPTION_EXIT_CODE);
            }

            try {
                Files.copy(src.toPath(), dest.toPath());
            } catch (IOException exception) {
                System.err.println("copy exception");
                System.exit(COPY_EXCEPTION_EXIT_CODE);
            }
        } else {
            if (!args[1].equals("-r")) {
                System.err.println("invalid arguments for cp");
                System.exit(INVALID_ARGUMENTS_EXIT_CODE);
            }
            if (src.isDirectory()) {
                boolean flagNoException = true;
                try {
                    Files.copy(src.toPath(), dest.toPath(), option);
                } catch (IOException exception) {
                    flagNoException = false;
                    System.err.println("file already exists");
                }
                if (flagNoException) {
                    recursiveCopy(src, dest);
                }
            } else {
                System.err.println("option -r is available only for directories");
                System.exit(MINUS_R_WITH_FILE_EXIT_CODE);
            }
        }
    }
}
