package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.HashMap;
import java.util.Map;

public class FileMapMain {
    public static void main(String[] args) throws Exception {
        Map<String, String> storage;
        String [] cmdBuffer = new String[1024];
        storage = new HashMap<String, String>();
        new FillingStorage().fillingStorageFunction(storage);
        if (args.length == 0) {
            new InteractiveMode().interactiveModeFunction(storage, cmdBuffer);
        } else {
            String commands = "";
            for (int ind = 0; ind < args.length; ++ind) {
                commands = commands + args[ind] + " ";
            }
            new BatchMode().batchModeFunction(commands, storage, cmdBuffer);
            new FillingDB().fillingDBFunction(storage);
            new Exit().exitFunction();
        }
    }
}

