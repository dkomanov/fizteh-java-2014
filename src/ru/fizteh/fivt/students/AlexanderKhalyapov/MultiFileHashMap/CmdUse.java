package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CmdUse implements Command {
    @Override
    public final String getName() {
        return "use";
    }
    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }
        if (1 == ((MultiFileHashMap) shell).getMFHMState().getFlag()) {
            File fileForWrite = ((MultiFileHashMapTable)
                    ((MultiFileHashMap) shell).getMFHMState().getCurrentTable()).getDataFile();
            Map<String, String> mapForWrite = ((MultiFileHashMapTable)
                    ((MultiFileHashMap) shell).getMFHMState().getCurrentTable()).getDataBase();
            MultiFileHashMapUtils.write(fileForWrite, mapForWrite);
        }
        if (((MultiFileHashMap) shell).getMFHMState().getFlag() == 0) {
            ((MultiFileHashMap) shell).getMFHMState().changeFlag();
        }
        ((MultiFileHashMap) shell).getMFHMState().setCurrentTable(args[0]);
        System.out.println("using " + args[0]);
    }
}
