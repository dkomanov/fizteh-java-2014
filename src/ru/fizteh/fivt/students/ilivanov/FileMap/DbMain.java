package ru.fizteh.fivt.students.ilivanov.FileMap;

public class DbMain {
    public static void main(final String[] args) throws Exception {
        String filePath = System.getProperty("db.file");
        if (filePath == null) {
            System.err.println("No file");
            System.exit(-1);
        }

        int exitCode = 0;
        FileUsing fileUsing = new FileUsing(filePath);
        try {
            exitCode = fileUsing.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(exitCode);
    }
}
