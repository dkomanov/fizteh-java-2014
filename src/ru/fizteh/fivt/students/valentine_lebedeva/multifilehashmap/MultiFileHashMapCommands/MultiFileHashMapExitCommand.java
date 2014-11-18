package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.valentine_lebedeva.shell.Cmd.Rm;

public final class MultiFileHashMapExitCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager manager) throws IOException {
        File rmDir = new File(System.getProperty("fizteh.db.dir"));
        for (File file : rmDir.listFiles()) {
            if (file.list().length == 0) {
                Rm.rmNorm(file.getAbsolutePath());
            } else {
                Rm.rmRec(file.getAbsolutePath());
            }
            file.mkdir();
        }
        for (MultiFileTable table : manager.getTables().values()) {
            table.close();
        }
        System.exit(0);
    }
}
