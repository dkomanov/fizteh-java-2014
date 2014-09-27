package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CatCommand extends AbstractCommand {
    CatCommand(Shell shell) {
        super("cat", shell);
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            shell.error("cat: missing file operand");
            return;
        }
        if (args.length > 2) {
            shell.error("cat: too many arguments");
            return;
        }
        Path path = shell.getWd().resolve(args[1]);
        try (InputStream in = Files.newInputStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                shell.output(line);
            }
        } catch (IOException e) {
            shell.error("cat: " + args[1] + ": No such file or directory");
        }
    }
}
