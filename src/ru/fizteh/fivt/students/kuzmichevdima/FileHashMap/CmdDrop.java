package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

    public class CmdDrop implements Command {
    @Override
    public void execute(Vector<String> args, DB db) throws IOException {
        if (!db.getDataBase().containsKey(args.get(1))) {
            System.out.println(args.get(1) + " does not exists");
        } else {
            String tableName = args.get(1);
            Path tablePath = db.getPath().resolve(tableName);
            File[] tableDirectories = tablePath.toFile().listFiles();
            for (File dir : tableDirectories) {
                File[] tableFiles = dir.listFiles();
                for (File file : tableFiles) {
                    Files.delete(file.toPath());
                }
                Files.delete(dir.toPath());
            }
            Files.delete(tablePath);
            db.getDataBase().remove(args.get(1));
            System.out.println("dropped");
        }
    }
}
