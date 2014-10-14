package DbCommands;

import TableManager.TableManager;

import java.io.IOException;
import java.util.Map;

public abstract class DbCommand {
    protected String[] args = null;
    protected String msg = null;

    public DbCommand(final String[] arguments) {
        args = arguments;
    }
    public String getName() {
        return args[0];
    }
    public String getMsg() {
        return msg;
    }
    public abstract void execute() throws IOException;
}
