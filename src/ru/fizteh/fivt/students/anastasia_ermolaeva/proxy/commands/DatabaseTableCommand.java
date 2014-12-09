package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DatabaseTableCommand extends DatabaseCommand {
    public DatabaseTableCommand(TableHolder tableHolder, String name, int numArguments, BiConsumer<TableHolder, String[]> callback) {
        super(tableHolder, name, numArguments, callback);
    }
    public DatabaseTableCommand(TableHolder tableHolder, String name, int numArguments,
                                BiConsumer<TableHolder, String[]> callback, Function<String[], String[]> handler) {
        super(tableHolder, name, numArguments, callback, handler);
    }


    @Override
    public String[] checkAndCorrectArguments(final String[] arguments) {
        String[] newArguments = super.checkAndCorrectArguments(arguments);
        tableHolder.checkActiveTable();
        return newArguments;
    }

}
