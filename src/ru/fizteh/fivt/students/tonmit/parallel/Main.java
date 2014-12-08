package ru.fizteh.fivt.students.tonmit.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.tonmit.parallel.data_base_structure.*;
import ru.fizteh.fivt.students.tonmit.parallel.interpreter.Interpreter;
import ru.fizteh.fivt.students.tonmit.parallel.interpreter.Command;
import ru.fizteh.fivt.students.tonmit.parallel.util.CastMaker;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public final class Main {

    public static void main(String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.err.println("You must specify DataBase directory via -Dfizteh.db.dir JVM parameter");
            System.exit(1);
        }
        try {
            run(new DataBaseState(new TableManagerFactory().create(rootDirectory)), args);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void run(final DataBaseState state, String[] args) {
        Interpreter dbInterpreter = new Interpreter(state, DefaultCommandArray.cmdArray);

        dbInterpreter.setExitHandler(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                TableClass table = (TableClass) state.getCurrentTable();
                if (table != null && (table.getNumberOfUncommittedChanges() > 0)) {
                    System.out.println(table.getNumberOfUncommittedChanges() + " unsaved changes");
                    return false;
                }
                return true;
            }

        });

        try {
            System.exit(dbInterpreter.run(args));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Unexpected error in function run");
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
