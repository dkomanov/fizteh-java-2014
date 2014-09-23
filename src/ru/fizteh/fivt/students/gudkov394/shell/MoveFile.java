package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/*работает*/

public class MoveFile {
    public MoveFile(String[] current_args, CurrentDirectory cd) {
        if (current_args.length > 3) {
            System.err.println("extra arguments for mv");
            System.exit(1);
        }
        CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
        File from = new File(current_args[1]);
        File to = new File(current_args[2]);
        if (!from.isAbsolute()) {
            from = new File(cd.getCurrentDirectory(), current_args[1]);
        }
        if (!to.isAbsolute()) {
            to = new File(cd.getCurrentDirectory(), current_args[2]);
        }
        to = new File(to.getAbsolutePath(), from.getName());
        try {
            Files.move(from.toPath(), to.toPath(), options);
        } catch (IOException e) {
            System.err.println("problem with move");
            System.exit(4);
        }
    }
}
