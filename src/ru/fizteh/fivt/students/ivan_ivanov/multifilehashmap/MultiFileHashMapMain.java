package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapMain {

    public static void main(final String[] args) throws IOException {


        String currentProperty = System.getProperty("fizteh.db.dir");
        try {


            if (currentProperty == null) {
                throw new IOException("Working directory is not specified");
            }

            if (!new File(currentProperty).exists()) {
            throw new IOException("Working directory does not exist");
            }

            if (!new File(currentProperty).isDirectory()) {
                throw new IOException("Working directory is not a directory");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        File base = new File(currentProperty);
        if (!base.exists()) {
            base.createNewFile();
        }


        try {
            base = base.getCanonicalFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        MultiFileHashMap mfhm = new MultiFileHashMap(base);
        MultiFileHashMapExecutor exec = new MultiFileHashMapExecutor();

        try {
            if (args.length > 0) {
                mfhm.batchState(args, exec);
            } else {
                mfhm.interactiveState(exec);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}
