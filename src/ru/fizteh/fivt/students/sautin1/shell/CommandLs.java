package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandLs extends Command {

    public CommandLs() {
        minArgNumber = 0;
        commandName = "ls";
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(presentWorkingDirectory)) {
            for (Path entry : directoryStream) {
                Path filePath = entry.getFileName();
                if (!Files.isHidden(filePath)) {
                    System.out.println(filePath.toString());
                }
            }
        } catch (IOException e) {
            throw new IOException(toString() + ": " + e.getMessage());
        }
    }

}
