package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Mv implements Command {
    public final String getName() {
        return "mv";
    }
    private void move(final Path source, final Path target) throws IOException {
        if (source.toFile().isFile()) {
            Files.copy(source, target);
        } else {
            File[] masOfSource = source.toFile().listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : masOfSource != null ? masOfSource : new File[0]) {
                move(sourceEntry.toPath(), target);
            }
        }
        source.toFile().delete();
    }
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (2 == args.length) {
            Path source = shell.getState().getPath().resolve(args[0]).normalize();
            Path target = shell.getState().getPath().resolve(args[1]).normalize();
            if (source.toFile().isFile() && target.toFile().isFile()) {
                throw new IOException("Cannot move file to file");
            }
            if (source.equals(target)) {
                throw new IOException("Cannot move file on itself");
            }
            if (!source.toFile().exists()) {
                throw new IOException("Source file doesn't exist");
            }
            if (source.toFile().isDirectory() && target.toFile().isFile()) {
                throw new IOException("Cannot move directory into file");
            }
            if (source.toFile().isDirectory() && !target.toFile().exists()) {
                throw new IOException("Target directory doesn't exist");
            }
            if (source.toFile().isDirectory() && target.toFile().isDirectory() && target.startsWith(source)) {
                throw new IOException("Cannot move parent directory into kid's directory");
            }
            if (source.toFile().isFile() && !target.toFile().exists()) {
                Files.copy(source, target);
                source.toFile().delete();
            } else if (source.toFile().isFile() && target.toFile().isDirectory()) {
                Files.copy(source, target.resolve(source.getFileName()));
                source.toFile().delete();
            } else if (source.toFile().isDirectory() && target.toFile().isDirectory()) {
                File[] masOfSource = source.toFile().listFiles();
                target.toFile().mkdir();
                if (masOfSource != null) {
                    for (File sourceEntry : masOfSource) {
                        move(sourceEntry.toPath(), target);
                    }
                }
            }
        } else {
            throw new IOException("Incorrect number of arguments");
        }
        Path path = shell.getState().getPath();
        while (!path.toFile().isDirectory()) {
            path = path.getParent();
        }
        shell.setState(path);
    }
}
