package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperations;
<<<<<<< HEAD

import java.io.IOException;

public class MultiFileHashMapOperations extends FileMapShellOperations {
        public MultiFileHashMapTableProvider tableProvider = null;

        public final ATable getTable(final String name) {
            table = tableProvider.getTable(name);
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

import java.io.IOException;

public class MultiFileHashMapOperations
        extends FileMapShellOperations
        implements MultiFileHashMapOperationsInterface<ATable> {
        public MultiFileHashMapTableOperations tableOperations = null;

        public final ATable useTable(final String name) {
            table = tableOperations.getTable(name);
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
            return table;
        }

        public final void dropTable(final String name) {
<<<<<<< HEAD
            tableProvider.removeTable(name);
            table = null;
        }

        public final void showTables() {
            tableProvider.showTables();
=======
            tableOperations.removeTable(name);
            table = null;
        }

        public final ATable createTable(final String parameters) {
            String[] params = CommandsParser.parseCommandParameters(parameters);
            return tableOperations.createTable(params[0]);
        }

        public final void showTables() {
            tableOperations.showTables();
        }

        public final String getTableName() {
            return table.getName();
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
        }

        public final int exit() {
            try {
<<<<<<< HEAD
                tableProvider.exit();
=======
                tableOperations.exit();
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return -1;
            }
            return 0;
        }
}
