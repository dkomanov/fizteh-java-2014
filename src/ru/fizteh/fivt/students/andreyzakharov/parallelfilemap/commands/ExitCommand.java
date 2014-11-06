package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.MultiFileTableProvider;

public class ExitCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getCurrent() != null && connector.getCurrent().getPending() > 0) {
            return connector.getCurrent().getPending() + " unsaved changes";
        }
        connector.close();
        System.exit(0);
        return null; // silly Java
    }

    @Override
    public String toString() {
        return "exit";
    }
}
