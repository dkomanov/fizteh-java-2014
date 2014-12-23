package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DatabaseTableCommand extends DatabaseCommand {
    public DatabaseTableCommand(DataBase dataBase, String name,
                                int numArguments, BiConsumer<DataBase, String[]> callback) {
        super(dataBase, name, numArguments, callback);
    }

    public DatabaseTableCommand(DataBase dataBase, String name, int numArguments,
                                BiConsumer<DataBase, String[]> callback, Function<String[], String[]> handler) {
        super(dataBase, name, numArguments, callback, handler);
    }


    @Override
    public String[] checkAndCorrectArguments(final String[] arguments) {
        String[] newArguments = super.checkAndCorrectArguments(arguments);
        dataBase.checkActiveTable();
        return newArguments;
    }

}
