package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("create: too many arguments");
        }
        if (connector.tables.get(args[1]) != null) {
            return args[1] + " exists";
        }

        FileMap table = new FileMap(connector.dbRoot.resolve(args[1]));
        for (int i = 0; i < 16; i++) {
            if (Files.exists(connector.dbRoot.resolve(args[1]).resolve(i + ".dir/"))) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(connector.dbRoot.resolve(args[1] + "/" + i + ".dir/"))) {
                    for (Path entry: stream) {
                        Files.delete(entry);
                    }
                } catch (IOException e) {
                    throw new CommandInterruptException("create: unable to create table: " + e.getMessage());
                }
            }
        }
        connector.tables.put(args[1], table);
        return "created";
    }
}
