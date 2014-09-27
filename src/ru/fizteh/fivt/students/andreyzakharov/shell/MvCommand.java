package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MvCommand extends AbstractCommand {
    MvCommand(Shell shell) {
        super("mv", shell);
    }

    @Override
    public void execute(String... args) {
        if (args.length < 2) {
            shell.error("mv: missing file operand");
            return;
        }
        if (args.length > 3) {
            shell.error("cat: too many arguments");
            return;
        }
        Path src = shell.getWd().resolve(args[1]);
        Path target = shell.getWd().resolve(args[2]);
        if (!Files.exists(src)) {
            // note: we do not actually call stat
            shell.error("mv: cannot stat '" + args[1] + "': No such file or directory");
            return;
        }
        if (Files.isDirectory(src) && !Files.isDirectory(target)) {
            shell.error("mv: cannot overwrite non-directory '" + args[2] + "' with directory '" + args[1] + "'");
            return;
        }
        /*if (!Files.exists(target.getParent())) {
            shell.error("mv: cannot move '" + args[1] + "' to '" + args[2] + "': Cannot overwrite existing file");
            return;
        }*/
        Path dest = Files.isDirectory(target) ? target.resolve(src.getFileName()) : target;
        try {
            try {
                Files.move(src, dest, REPLACE_EXISTING);
            } catch (DirectoryNotEmptyException e) {
                // the folder is not empty and the actual physical move is required
                AbstractCommand cp = new CpCommand(shell);
                AbstractCommand rm = new RmCommand(shell);
                cp.execute("cp", "-r", src.toString(), dest.toString());
                rm.execute("rm", "-r", src.toString(), dest.toString());
            }
        } catch (IOException e) {
            shell.error("mv: i/o error");
        }
    }
}
