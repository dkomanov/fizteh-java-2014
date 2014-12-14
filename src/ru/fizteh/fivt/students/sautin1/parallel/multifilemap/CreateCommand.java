package ru.fizteh.fivt.students.sautin1.parallel.multifilemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.*;

import java.text.ParseException;
import java.util.List;

/**
 * Command for creating database table.
 * Created by sautin1 on 10/20/14.
 */
public class CreateCommand extends AbstractStoreableDatabaseCommand {

    public CreateCommand() {
        super("create", 2, Integer.MAX_VALUE - 1);
    }

    /**
     * Create new table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StoreableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        try {
            String signature = "";
            for (int argIndex = 2; argIndex < args.length; ++argIndex) {
                signature += args[argIndex] + " ";
            }
            signature = signature.trim();
            if (signature.startsWith("(") && signature.endsWith(")")) {
                signature = signature.substring(1, signature.length() - 1);
            } else {
                throw new ColumnFormatException("types must be in parentheses");
            }
            List<Class<?>> valueTypes = StoreableXMLUtils.parseSignatureString(signature);
            StoreableTableProvider provider = state.getTableProvider();
            StoreableTable newTable = provider.createTable(args[1], new Object[]{valueTypes});
            if (newTable == null) {
                System.err.println(args[1] + " exists");
            } else {
                System.out.println("created");
            }
        } catch (ColumnFormatException | ParseException e) {
            throw new CommandExecuteException("wrong type (" + e.getMessage() + ")");
        }
    }
}
