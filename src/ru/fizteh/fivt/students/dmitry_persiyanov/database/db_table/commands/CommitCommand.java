package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class CommitCommand extends TableManagerCommand {
    public CommitCommand(final String[] args, final DbTable relatedTable) {
        super("commit", 0, args, relatedTable);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (relatedTable == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(relatedTable.commit());
        }
    }
}
