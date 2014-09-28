package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.*;

public class DropCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("drop: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("drop: too many arguments");
        }

        if (!connector.tableExists(args[1])) {
            return args[1]+" not exists";
        }

        if (connector.db.dbPath.equals(connector.dbRoot.resolve(args[1]))) {
            connector.db = null;
        }
        try {
            Files.walkFileTree(connector.dbRoot.resolve(args[1]), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
//                    Files.delete(file);
                    return CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                    Files.delete(dir);
                    return CONTINUE;
                }
            });
            return "deleted";
        } catch (IOException e) {
            throw new CommandInterruptException("drop: "+e.getMessage());
        }
    }
}
