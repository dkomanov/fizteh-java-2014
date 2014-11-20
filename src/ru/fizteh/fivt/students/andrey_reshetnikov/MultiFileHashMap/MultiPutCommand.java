package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.File;

public class MultiPutCommand extends Command {

    private String key;
    private String value;

    public MultiPutCommand(String newKey, String newValue) {
        key = newKey;
        value = newValue;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int ndirectory = hashCode % NUM_DIRECTORIES;
            int nfile = hashCode / NUM_FILES % NUM_FILES;
            //System.out.println(ndirectory + " " + nfile);
            PutCommand put = new PutCommand(key, value);
            if (base.getUsing().databases[ndirectory][nfile] == null) {
                File insideMainDir = new File(base.getUsing().insideMainDir, String.valueOf(ndirectory) + ".dir");
                if (!insideMainDir.exists()) {
                    if (!insideMainDir.mkdir()) {
                        throw new Exception("You cann't create directory in working catalog");
                    }
                }
                File dataBaseFile = new File(insideMainDir, String.valueOf(nfile) + ".dat");
                if (!dataBaseFile.exists()) {
                    if (!dataBaseFile.createNewFile()) {
                        throw new Exception("You cann't create database of files in working catalog");
                    }
                }
                base.getUsing().databases[ndirectory][nfile] = new DataBaseOneFile(dataBaseFile.toString());
            }
            put.execute(base.getUsing().databases[ndirectory][nfile], false);
        }
    }
}
