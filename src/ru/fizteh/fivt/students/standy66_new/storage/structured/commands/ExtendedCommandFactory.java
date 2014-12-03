package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.StorageCommandFactory;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by andrew on 14.11.14.
 */
public class ExtendedCommandFactory extends StorageCommandFactory {
    public ExtendedCommandFactory(PrintWriter writer, StructuredDatabase database) {
        super(writer, database.getBackendDatabase());
        context = new ExtendedContext(database);
    }

    @Override
    public Map<String, Command> getCommandsMap() {
        Map<String, Command> old = super.getCommandsMap();
        old.put("signs", getShowSignatureCommand());
        return old;
    }

    @Override
    protected Command putCommand() {
        return new StructuredPut(writer, (ExtendedContext) context);
    }

    @Override
    protected Command useCommand() {
        return new StructuredUse(writer, (ExtendedContext) context);
    }

    @Override
    protected Command createCommand() {
        return new StructuredCreate(writer, (ExtendedContext) context);
    }

    protected Command getShowSignatureCommand() {
        return new ShowSignature(writer, (ExtendedContext) context);
    }


}
