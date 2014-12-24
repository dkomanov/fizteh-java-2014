package ru.fizteh.fivt.students.SmirnovAlexandr.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CommandRm {
    public static void execute(final String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("rm: too few arguments");
        } else if (!args[0].equals("-r")) {
            if (args.length > 1) {
                throw new IllegalArgumentException("rm: too many arguments");
            } else {
                Path rmPath = Paths.get(Shell.getWorkingDir(), args[0]).normalize();
                if (Files.isDirectory(rmPath)) {
                    String[] rmPathContent = new File(rmPath.toString()).list();
                    if (rmPathContent.length == 0) {
                        try {
                            rm(rmPath);
                        } catch (IOException e) {
                            throw new IllegalArgumentException("rm: cannot remove '" + args[0]
                                    + "': No such file or directory");
                        }
                    } else {
                        throw new IllegalArgumentException("rm: '" + args[0] + "' is a directory");
                    }
                } else {
                    try {
                        rm(rmPath);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("rm: cannot remove '" + args[0]
                                + "': No such file or directory");
                    }
                }
            }
        } else {
            if (args.length > 2) {
                throw new IllegalArgumentException("rm: too many arguments");
            } else {
                Path rmPath = Paths.get(Shell.getWorkingDir(), args[1]).normalize();
                try {
                    rm(rmPath);
                } catch (IOException e) {
                    throw new IllegalArgumentException("rm: cannot remove '" + args[1]
                            + "': No such file or directory");
                }
            }
        }
    }

    private static void rm(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.delete(path);
        } else {
            File[] currDir = new File(path.toString()).listFiles();
            for (File x : currDir) {
                rm(x.toPath());
            }
            Files.delete(path);
        }
    }
}
