package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CommandFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CreateCommand;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.PutCommand;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.UseCommand;
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
    public PutCommand putCommand() {
        return super.putCommand();
    }

    @Override
    public UseCommand useCommand() {
        return super.useCommand();
    }

    @Override
    public CreateCommand createCommand() {
        return super.createCommand();
    }
}
