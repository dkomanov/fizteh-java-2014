package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.HashMap;

/**
 * Created by Maxim on 07.10.2014.
 */
public class FileMapMain {
    static HashMap<String, String> storage;
    static String [] cmdBuffer;
    public static void main(String[] args) throws Exception {
        storage = new HashMap<String, String>();
        new FillingStorage().fillingStorageFunction();
        if (args.length == 0) {
            new InteractiveMode().interactiveModeFunction();
        } else {
            String commands = "";
            for (int ind = 0; ind < args.length; ++ind) {
                commands = commands + args[ind] + " ";
            }
            new BatchMode().batchModeFunction(commands);
            new FillingDB().fillingDBFunction();
            new Exit().exitFunction();
        }
    }
}

