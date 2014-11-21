package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.standy66_new.commands.Command;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by astepanov on 20.10.14.
 */
public class CommandFactory {
    protected Context context;
    protected PrintWriter writer;

    public CommandFactory(PrintWriter writer, TableProvider provider) throws IllegalArgumentException {
        if (provider == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.context = new Context(provider);
        this.writer = writer;
    }

    public Map<String, Command> getCommandsMap() {
        Map<String, Command> map = new HashMap<>();
        map.put("put", putCommand());
        map.put("get", getCommand());
        map.put("list", listCommand());
        map.put("remove", removeCommand());
        map.put("show", showTablesCommand());
        map.put("create", createCommand());
        map.put("drop", dropCommand());
        map.put("use", useCommand());
        map.put("size", sizeCommand());
        map.put("commit", commitCommand());
        map.put("rollback", rollbackCommand());
        return map;
    }

    protected Command putCommand() {
        return new PutCommand(writer, context);
    }

    protected Command createCommand() {
        return new CreateCommand(writer, context);
    }

    protected Command dropCommand() {
        return new DropCommand(writer, context);
    }

    protected Command getCommand() {
        return new GetCommand(writer, context);
    }

    protected Command listCommand() {
        return new ListCommand(writer, context);
    }

    protected Command removeCommand() {
        return new RemoveCommand(writer, context);
    }

    protected Command showTablesCommand() {
        return new ShowTablesCommand(writer, context);
    }

    protected Command useCommand() {
        return new UseCommand(writer, context);
    }

    protected Command sizeCommand() {
        return new SizeCommand(writer, context);
    }

    protected Command commitCommand() {
        return new CommitCommand(writer, context);
    }

    protected Command rollbackCommand() {
        return new RollbackCommand(writer, context);
    }

}
