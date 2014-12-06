package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import static ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Rm.remove;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFileHashMapTableProvider {
    private Map<String, MultiFileHashMapTable> mapOfTables;
    private File currentDir;
    public MultiFileHashMapTableProvider(final File inDir) throws IOException {
        mapOfTables = new HashMap<>();
        currentDir = inDir;
        File[] fileMas = currentDir.listFiles();
        if (fileMas.length != 0) {
            for (int i = 0; i < fileMas.length; ++i) {
                if (fileMas[i].isDirectory()) {
                    mapOfTables.put(fileMas[i].getName(), new MultiFileHashMapTable(fileMas[i]));
                }
            }
        }
    }
    public final MultiFileHashMapTable getTable(final String name) throws IOException {
        if (!mapOfTables.containsKey(name)) {
            return null;
        }
        return new MultiFileHashMapTable(new File(currentDir, name));
    }
    public final MultiFileHashMapTable createTable(final String name) throws IOException {
        File dirOfTable = new File(currentDir, name);
        if (!dirOfTable.mkdir()) {
            return null;
        }
        MultiFileHashMapTable table = new MultiFileHashMapTable(dirOfTable);
        mapOfTables.put(name, table);
        return table;
    }
    public final void removeTable(final String name) throws IOException {
        File dirOfTable = new File(currentDir, name);
        remove(dirOfTable.getCanonicalFile().toPath());
        mapOfTables.remove(name);
    }
    public final Set<String> getTables() {
        Set<String> tables = mapOfTables.keySet();
        return tables;
    }
}
