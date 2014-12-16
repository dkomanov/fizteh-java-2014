package ru.fizteh.fivt.students.sautin1.telnet.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTable;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

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
                List<String> typeNameList = Arrays.asList(args).subList(2, args.length);
                String serialized = String.join(" ", typeNameList);
                Storeable storeable = state.getTableProvider().deserialize(table, serialized);
                Storeable value = table.put(args[1], storeable);
                if (value == null) {
                    state.getOutStream().println("new");
                } else {
                    state.getOutStream().println("overwrite");
                    state.getOutStream().println(value);
                }
            } catch (ColumnFormatException | ParseException e) {
                throw new CommandExecuteException("wrong type (" + e.getMessage() + ")");
            } catch (Exception e) {
                throw new CommandExecuteException(e.getMessage());
            }
        } else {
            throw new CommandExecuteException("no table");
        }
    }
}
