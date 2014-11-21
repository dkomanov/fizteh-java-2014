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
    private PrintWriter writer;

    public CommandFactory(PrintWriter writer, TableProvider provider) throws IllegalArgumentException {
        if (provider == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.context = new Context(provider);
        this.writer = writer;
    }

    public Command putCommand() {
        return new PutCommand(writer, context);
    }

    public Command createCommand() {
        return new CreateCommand(writer, context);
    }

    public Command dropCommand() {
        return new DropCommand(writer, context);
    }

    public Command getCommand() {
        return new GetCommand(writer, context);
    }

    public Command listCommand() {
        return new ListCommand(writer, context);
    }

    public Command removeCommand() {
        return new RemoveCommand(writer, context);
    }

    public Command showTablesCommand() {
        return new ShowTablesCommand(writer, context);
    }

    public Command useCommand() {
        return new UseCommand(writer, context);
    }

    public Command sizeCommand() {
        return new SizeCommand(writer, context);
    }

    public Command commitCommand() {
        return new CommitCommand(writer, context);
    }

    public Command rollbackCommand() {
        return new RollbackCommand(writer, context);
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

}
