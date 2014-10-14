package TableManager.TableManagerCommands;

import DbCommands.DbCommand;
import DbCommands.WrongSyntaxException;

public class PutCommand extends TableManagerCommand {
    public PutCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 3) {
            throw new WrongSyntaxException("put");
        }
        String oldValue = hashMap.put(args[1], args[2]);
        if (oldValue == null) {
            msg = new String("new");
        } else {
            msg = new String("overwrite" + System.lineSeparator() + oldValue);
        }
    }
}
