package ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.TableManager;

import java.io.PrintStream;
import java.util.List;

public class ListCommand extends TableManagerCommand {
    public ListCommand(final String[] args, final TableManager relatedTable) {
        super("list", 0, args, relatedTable);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (relatedTable == null) {
            throw new TableIsNotChosenException();
        } else {
            List<String> allKeys = relatedTable.list();
            if (allKeys.size() != 0) {
                out.println(String.join(", ", allKeys));
            } else {
                out.println("");
            }
        }
    }
}
