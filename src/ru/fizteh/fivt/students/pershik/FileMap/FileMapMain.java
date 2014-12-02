package ru.fizteh.fivt.students.pershik.FileMap;

/**
 * Created by pershik on 9/25/14.
 */
public class FileMapMain {
    public static void main(String[] args) {
        try {
            String dbFile = System.getProperty("db.file");
            if (dbFile == null) {
                System.err.println("No database path given");
                System.exit(-1);
            }
            FileMap db = new FileMap(dbFile);
            if (args.length == 0) {
                db.runInteractive();
            } else {
                db.runBatch(args);
            }
        } catch (Exception e) {
            System.err.println("An error occurred");
            System.exit(-1);
        }
    }
}
