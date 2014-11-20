package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;
import java.text.ParseException;

public class PutCommand extends DbCommand {
    public PutCommand(final String[] args, final DbTableProvider tableProvider, final Table table) {
        super("put", 2, args, tableProvider, table);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable == null) {
            throw new TableIsNotChosenException();
        } else {
            String key = args[0];
            String jsonValue = args[1];
            try {
                Storeable oldStoreableValue = currentTable.put(key, tableProvider.deserialize(currentTable, jsonValue));
                String oldValue = tableProvider.serialize(currentTable, oldStoreableValue);
                if (oldValue == null) {
                    out.println("new");
                } else {
                    out.println("overwrite" + System.lineSeparator() + oldValue);
                }
            } catch (ColumnFormatException | IndexOutOfBoundsException e) {
                out.println("( " + e.getMessage() + " )");
            } catch (ParseException e) {
                out.println("( parse error: " + e.getMessage() + " )");
            }
        }
    }
}
