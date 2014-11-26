package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {
        try {
            FileMap fileMap = new FileMap(System.getProperty("db.file"));

            if (args.length == 0) {
                fileMap.startInteractive();
            } else {
                StringBuilder commands = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    commands.append(args[i]);
                    if (i < args.length - 1) {
                        commands.append(" ");
                    }
                }

                fileMap.startBatch(commands.toString());
            }

            fileMap.close();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
