package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.nio.file.Files;
import java.nio.file.Path;

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
        Path src = shell.getWd().resolve(args[1]);
        Path target = shell.getWd().resolve(args[2]);
        if (!Files.exists(src)) {
            // note: we do not actually call stat
            shell.error("mv: cannot stat '"+args[1]+"': No such file or directory");
            return;
        }
        if (Files.isDirectory(target)) {
//            Files.move(src, target);
        }
    }
}
