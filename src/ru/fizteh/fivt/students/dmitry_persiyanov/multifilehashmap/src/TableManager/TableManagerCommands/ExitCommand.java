package TableManager.TableManagerCommands;

import DbCommands.DbCommand;
import DbCommands.WrongSyntaxException;
import TableManager.TableManager;

import java.io.IOException;

public class ExitCommand extends TableManagerCommand {
    public ExitCommand(final String[] args) {
       super(args);
    }

    @Override
    public final void execute() throws IOException {
        if (args.length != 1) {
            throw new WrongSyntaxException("exit");
        }
        TableManager.dumpHashMap();
        TableManager.getDbFile().close();
        System.exit(0);
    }
}
