package ru.fizteh.fivt.studenrts.theronsg.multifilehashmap;

import java.io.File;

public class MFHMMain {
    
    public static void main(final String [] args) {
        try {
            String path = System.getProperty("fizteh.db.dir");
            if (!new File(path).exists()) {
                throw new Exception("\"" + path + "\"" + " does not exists!");
            }
            if (!new File(path).isDirectory()) {
                throw new Exception("\"" + path + "\"" + " is not a directory!");
            }
            TableList tL = new TableList(path);
            if (args.length == 0) {
                Interpreter.interactiveMode(tL);
            } else {
                Interpreter.batchMode(args, tL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
