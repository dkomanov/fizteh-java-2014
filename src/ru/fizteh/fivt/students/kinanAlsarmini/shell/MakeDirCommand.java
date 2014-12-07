package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

class MakeDirCommand extends ExternalCommand {
    public MakeDirCommand() {
        super("mkdir", 1);
    }

    public void execute(String[] args, Shell shell) {
        try {
            Files.createDirectory(shell.getCurrentPath().resolve(args[0]));
        } catch (FileAlreadyExistsException e) {
            throw new IllegalArgumentException("mkdir: folder already exists!");
        } catch (IOException e) {
            throw new IllegalArgumentException("mkdir: I/O error or the parent directory does not exist!");
        }
    }
}


