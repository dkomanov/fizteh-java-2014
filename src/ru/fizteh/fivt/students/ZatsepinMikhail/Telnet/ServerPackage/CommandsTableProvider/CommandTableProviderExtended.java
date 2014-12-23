package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public abstract class CommandTableProviderExtended {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean run(TableProviderExtended dataBase, String[] args, PrintStream output);
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
