package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

import java.io.IOException;

public class MultiFileHashMapOperations extends FileMapShellOperations {
        public MultiFileHashMapTableProvider tableProvider = null;

        public final ATable getTable(final String name) {
            table = tableProvider.getTable(name);
            return table;
        }

        public final void dropTable(final String name) {
            tableProvider.removeTable(name);
            table = null;
        }

        public final void showTables() {
            tableProvider.showTables();
        }

        public final int exit() {
            try {
                tableProvider.exit();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return -1;
            }
            return 0;
        }
}
