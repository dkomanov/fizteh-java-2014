package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.standy66_new.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by astepanov on 20.10.14.
 */
public class CommandFactory {
    protected Context context;

    public CommandFactory(TableProvider provider) throws NullPointerException {
        if (provider == null) {
            throw new NullPointerException("database must not be null");
        }
        this.context = new Context(provider);
    }

    public Command putCommand() {
        return new PutCommand(context);
    }

    public Command createCommand() {
        return new CreateCommand(context);
    }

    public Command dropCommand() {
        return new DropCommand(context);
    }

    public Command getCommand() {
        return new GetCommand(context);
    }

    public Command listCommand() {
        return new ListCommand(context);
    }

    public Command removeCommand() {
        return new RemoveCommand(context);
    }

    public Command showTablesCommand() {
        return new ShowTablesCommand(context);
    }

    public Command useCommand() {
        return new UseCommand(context);
    }

    public Command sizeCommand() {
        return new SizeCommand(context);
    }

    public Command commitCommand() {
        return new CommitCommand(context);
    }

    public Command rollbackCommand() {
        return new RollbackCommand(context);
    }

    public Map<String, Command> getCommandsMap(String locale) {
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
