package ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.FileMap;

import java.io.IOException;

public class ExitCommand extends Command {
    public ExitCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "exit: wrong syntax (arguments are prohibited)");
        }
        FileMap.saveChangesToFile();
        FileMap.getDbFile().close();
        System.exit(0);
    }
}
