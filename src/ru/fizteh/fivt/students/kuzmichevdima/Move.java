/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;



public class Move {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 14;
    private static final int SRC_DOESNT_EXIST_EXIT_CODE = 15;
    private static final int MOVE_EXCEPTION_EXIT_CODE = 16;
    public Move(final String [] args, final CurrentDir dir) {
        if (args.length != 3) {
            System.err.println("invalid number of arguments for mv");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        CopyOption [] option = new CopyOption [] {StandardCopyOption.REPLACE_EXISTING};
        File fileSrc = new File(args[1]);
        File fileDest = new File(args[2]);
        if (!fileSrc.isAbsolute()) {
            fileSrc = new File(dir.getCurrentDir(), args[1]);
        }
        if (!fileDest.isAbsolute()) {
            fileDest = new File(dir.getCurrentDir(), args[2]);
        }
        if (!fileSrc.exists()) {
            System.err.println(fileSrc.getPath() + " doesn't exist");
            System.exit(SRC_DOESNT_EXIST_EXIT_CODE);
        }
        /*if (fileSrc.toString().equals(fileSrc.toString()))
        {
            System.err.println("destination and source are the same");
            System.exit(DEST_EQ_SRC_EXIT_CODE);
        }*/
        try {
            Files.move(fileSrc.toPath(), fileDest.toPath(), option);
        } catch (IOException exception) {
            System.err.println("move exception");
            System.exit(MOVE_EXCEPTION_EXIT_CODE);
        }
    }
}
