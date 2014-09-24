package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.nio.file.Files;
import java.nio.file.Path;

public class MvCommand extends AbstractCommand {
    MvCommand(Shell shell) {
        super(shell);
        identifier = "mv";
    }

    @Override
    public void execute(String... args) {
        if (args.length < 2) {
            shell.error("mv: missing file operand");
            return;
        }
        Path src = shell.getWd().resolve(args[1]);
        Path dest = shell.getWd().resolve(args[2]);
        if (Files.isDirectory(dest)) {
            // TODO: NIY
        }
    }
}