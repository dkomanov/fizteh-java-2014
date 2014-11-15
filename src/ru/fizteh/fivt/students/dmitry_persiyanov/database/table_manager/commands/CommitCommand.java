package ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.TableManager;

import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class CommitCommand extends TableManagerCommand {
    public CommitCommand(final String[] args, final TableManager relatedTable) {
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
