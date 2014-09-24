package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CpCommand extends AbstractCommand {
    CpCommand(Shell shell) {
        super(shell);
        identifier = "cp";
    }

    @Override
    public void execute(String... args) {
        boolean recursive;
        if (args.length < 2 || ((recursive = args[1].equals("-r")) && args.length < 3)) {
            shell.error("cp: missing file operand");
            return;
        }

        Path src = null;
        try {
            src = shell.getWd().resolve(args[1]).toRealPath();
        } catch (IOException e) {
            shell.error("cp: '" + args[1] + "': No such file or directory");
            return;
        }

        boolean isSourceDir = Files.isDirectory(src);
        if (isSourceDir && !recursive) {
            shell.error("cp: omitting directory '" + args[1] + "'");
            return;
        }

        Path target = shell.getWd().resolve(args[2]);
        boolean isTargetDir = Files.isDirectory(target);
        Path dest = (!isTargetDir) ? target : target.resolve(src.getFileName());

		/*
         * output(src.toString()); output(target.toString());
		 * output(dst.toString());
		 */

        try {
            Files.copy(src, dest, REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}