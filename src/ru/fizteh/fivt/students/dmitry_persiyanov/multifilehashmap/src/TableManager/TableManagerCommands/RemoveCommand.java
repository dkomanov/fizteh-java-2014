package TableManager.TableManagerCommands;

import DbCommands.DbCommand;
import DbCommands.WrongSyntaxException;

public class RemoveCommand extends TableManagerCommand {
    public RemoveCommand(final String[] args) {
        super(args);
    }

    @Override
    public final void execute() {
        if (args.length != 2) {
            throw new WrongSyntaxException("remove");
        }
        String value = hashMap.remove(args[1]);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("removed");
        }
    }
}
