package TableManager.TableManagerCommands;

import DbCommands.DbCommand;
import TableManager.TableManager;

import java.util.Map;

public abstract class TableManagerCommand extends DbCommand {
    public TableManagerCommand(final String[] arguments) {
        super(arguments);
    }

    protected Map<String, String> hashMap = TableManager.getFileHashMap();
}
