package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

import java.io.IOException;

public class MultiFileHashMapOperations
        extends FileMapShellOperations
        implements MultiFileHashMapOperationsInterface<ATable> {
        public MultiFileHashMapTableOperations tableOperations = null;

        public final ATable useTable(final String name) {
            table = tableOperations.getTable(name);
            return table;
        }

        public final void dropTable(final String name) {
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
        }

        public final int exit() {
            try {
                tableOperations.exit();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return -1;
            }
            return 0;
        }
}
