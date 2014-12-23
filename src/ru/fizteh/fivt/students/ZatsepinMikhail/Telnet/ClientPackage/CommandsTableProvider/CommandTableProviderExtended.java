package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.PrintStream;

public abstract class CommandTableProviderExtended {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean run(TableProvider dataBase, String[] args, PrintStream output);

    @Override
    public final String toString() {
            return name;
        }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public String getName() {
        return name;
    }
}
