package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LsCommand extends AbstractCommand {
    LsCommand(Shell shell) {
        super("ls", shell);
    }

    @Override
    public void execute(String... args) {
        if (args.length > 1) {
            shell.error("ls: too many arguments");
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(shell.getWd())) {
            for (Path file : stream) {
                shell.output(file.getFileName().toString());
            }
        } catch (IOException e) {
            shell.error("ls: cannot access " + shell.getWd().getFileName() + ": No such file or directory");
        }
    }
}
