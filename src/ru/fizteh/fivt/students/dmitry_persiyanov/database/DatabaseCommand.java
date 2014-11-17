package ru.fizteh.fivt.students.dmitry_persiyanov.database;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.IllegalNumberOfArgumentsException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.InterpreterCommand;

import java.io.IOException;
import java.io.PrintStream;

public abstract class DatabaseCommand implements InterpreterCommand {
    protected static int numOfArgs;
    protected String[] args;
    protected String name;

    /**
     *
     * @param name Command name.
     * @param numOfArgs Valid number of arguments that command accepts.
     * @param args  Argument values.
     */
    public DatabaseCommand(final String name, int numOfArgs, final String[] args) {
        this.numOfArgs = numOfArgs;
        this.args = args;
        this.name = name;
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
                execute(out);
            } catch (IOException e) {
                err.println(e.getMessage());
            } catch (TableIsNotChosenException e) {
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
    protected abstract void execute(final PrintStream out) throws IOException, TableIsNotChosenException;
}
