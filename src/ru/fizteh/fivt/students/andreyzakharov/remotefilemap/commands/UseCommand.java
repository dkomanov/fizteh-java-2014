package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.ConnectionInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class UseCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("use: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("use: too many arguments");
        }

        if (connector.getTable(args[1]) != null) {
            if (connector.getCurrent() != null) {
                if (connector.getCurrent().getNumberOfUncommittedChanges() > 0) {
                    return connector.getCurrent().getNumberOfUncommittedChanges() + " unsaved changes";
                }
                try {
                    connector.getCurrent().unload();
                } catch (ConnectionInterruptException e) {
                    throw new CommandInterruptException("use: can't unload active table");
                }
            }
            connector.useTable(args[1]);
            return "using " + args[1];
        } else {
            return args[1] + " not exists";
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String toString() {
        return "use";
    }
}
