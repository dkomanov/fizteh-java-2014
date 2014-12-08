package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MultiFileHashMapExit implements Command {

    @Override
    public final String getName() {
        return "exit";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 0) {
            System.out.println("incorrect number of arguments");
            return;
        }

        if (((MultiFileHashMap) shell).getMFHMState().getFlag() == 1) {
            File fileForWrite = ((MultiFileHashMapTable)
                    ((MultiFileHashMap) shell).getMFHMState().getCurrentTable()).getDataFile();
            Map<String, String> mapForWrite = ((MultiFileHashMapTable)
                    ((MultiFileHashMap) shell).getMFHMState().getCurrentTable()).getDataBase();
            MultiFileHashMapUtils.write(fileForWrite, mapForWrite);
        }
        System.exit(0);
    }
}
