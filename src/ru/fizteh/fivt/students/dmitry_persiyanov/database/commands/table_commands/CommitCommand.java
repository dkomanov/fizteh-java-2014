package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class CommitCommand extends DbCommand {
    public CommitCommand(final String[] args, final DbTableProvider tableProvider) {
        super("commit", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException, IOException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(currentTable().commit());
        }
    }
}
