package ru.fizteh.fivt.students.SmirnovAlexandr.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class CommandCat {
    public static void execute(final String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("cat: too few arguments");
        } else if (args.length > 1) {
            throw new IllegalArgumentException("cat: too many arguments");
        } else {
            try (BufferedReader in = Files.newBufferedReader(Paths.get(Shell.getWorkingDir(), args[0]))) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new IOException("cat: " + args[0] + ": No such file or directory");
            }
        }
    }
}
