package ru.fizteh.fivt.students.sautin1.storeable.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTable;
import ru.fizteh.fivt.students.sautin1.storeable.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.storeable.shell.UserInterruptException;

import java.text.ParseException;

/**
 * Put command.
 * Created by sautin1 on 10/12/14.
 */
public class PutCommand extends AbstractStoreableDatabaseCommand {

    public PutCommand() {
        super("put", 2, Integer.MAX_VALUE - 1);
    }

    /**
     * Put new key and value to the active table.
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
        StoreableTable table = state.getActiveTable();
        if (table != null) {
            try {
                String serialized = "";
                for (int argIndex = 2; argIndex < args.length; ++argIndex) {
                    serialized += args[argIndex] + " ";
                }
                serialized = serialized.trim();
                Storeable storeable = state.getTableProvider().deserialize(table, serialized);
                Storeable value = table.put(args[1], storeable);
                if (value == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(value);
                }
            } catch (ColumnFormatException | ParseException e) {
                throw new CommandExecuteException("wrong type (" + e.getMessage() + ")");
            } catch (Exception e) {
                throw new CommandExecuteException(e.getMessage());
            }
        } else {
            System.err.println("no table");
        }
    }
}
