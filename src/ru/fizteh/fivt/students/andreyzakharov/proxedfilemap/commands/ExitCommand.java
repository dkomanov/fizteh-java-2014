package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProvider;

public class ExitCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getCurrent() != null && connector.getCurrent().getNumberOfUncommittedChanges() > 0) {
            return connector.getCurrent().getNumberOfUncommittedChanges() + " unsaved changes";
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
