package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CommandFactory;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;

/**
 * Created by andrew on 14.11.14.
 */
public class ExtendedCommandFactory extends CommandFactory {
    public ExtendedCommandFactory(StructuredDatabase database) {
        super(database.getBackendDatabase());
        context = new ExtendedContext(database);
    }

    @Override
    public Command putCommand() {
        return new StructuredPut((ExtendedContext) context);
    }

    @Override
    public Command useCommand() {
        return new StructuredUse((ExtendedContext) context);
    }

    @Override
    public Command createCommand() {
        return new StructuredCreate((ExtendedContext) context);
    }
}
