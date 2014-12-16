package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.IllegalNumberOfArgumentsException;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.TableCorruptedException;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.WrongTableNameException;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.interpreter.InterpreterCommand;

import java.io.IOException;
import java.io.PrintStream;

public abstract class DbCommand implements InterpreterCommand {
    protected static int numOfArgs;
    protected String[] args;
    protected String name;
    protected DbTableProvider tableProvider;

    /**
     *
     * @param name Command name.
     * @param numOfArgs Valid number of arguments that command accepts.
     * @param args  Argument values.
     */
    public DbCommand(final String name, int numOfArgs, final String[] args, final DbTableProvider tableProvider) {
        this.numOfArgs = numOfArgs;
        this.args = args;
        this.name = name;
        this.tableProvider = tableProvider;
    }

    /**
     *
     * @return true if args.length == numOfArgs, otherwise returns false
     */
    public boolean checkArgs() {
        return (numOfArgs == args.length);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Executes command if args.length is valid, otherwise writes an error in 'err'
     * @param out Output stream.
     * @param err Error stream.
     */
    @Override
    public void exec(final PrintStream out, final PrintStream err) {
        if (checkArgs()) {
            try {
                execChecked(out);
            } catch (IOException e) {
                err.println(e.getMessage());
            } catch (TableIsNotChosenException e) {
                out.println(e.getMessage());
            } catch (WrongTableNameException e) {
                out.println(e.getMessage());
            } catch (TableCorruptedException e) {
                out.println(e.getMessage());
            }
        } else {
            err.println(new IllegalNumberOfArgumentsException(name).getMessage());
        }
    }

    /**
     * Executes command. Here it is guaranteed that args.length is valid
     * @param out Output stream.
     * @param err Error stream.
     */
    protected abstract void execChecked(final PrintStream out) throws IOException, TableIsNotChosenException;

    protected Table currentTable() {
        return tableProvider.getCurrentTable();
    }
}
