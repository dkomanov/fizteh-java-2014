package ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CommandMv {
    public static void execute(final String[] args) throws IOException {
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("mv: too few arguments");
            } else if (args.length > 2) {
                throw new IllegalArgumentException("mv: too many arguments");
            } else {
                Path src = Paths.get(Shell.getWorkingDir(), args[0]).normalize();
                Path dest = Paths.get(Shell.getWorkingDir(), args[1]).normalize();
                if (!Files.exists(src)) {
                    throw new IllegalArgumentException("mv: cannot stat '" + args[0]
                            + "': No such file or directory");
                } else if (!Files.exists(dest)) {
                    if (src.getParent().toString().equals(dest.getParent().toString())) {   // rename
                        File srcFile = new File(src.toString());
                        srcFile.renameTo(new File(dest.toString()));
                    } else {    // move
                        if (dest.startsWith(src)) {
                            throw new IllegalArgumentException("mv: cannot move folder in itself");
                        } else {
                            Files.createDirectory(dest);
                            mv(src.toString(), dest.toString());
                        }
                    }
                } else {
                    if (dest.startsWith(src)) {
                        throw new IllegalArgumentException("mv: cannot move folder in itself");
                    } else {
                        mv(src.toString(), dest.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("mv: cannot move");
        }
    }

    private static void mv(final String src, final String dest) throws IOException {
        Path currDirPath = Paths.get(src);
        String[] currDirContent = new File(currDirPath.toString()).list();
        for (String x : currDirContent) {
            if (Files.isDirectory(Paths.get(src, x))) {
                mv(Paths.get(src, x).toString(), Paths.get(dest, x).toString());
            } else {
                Files.move(Paths.get(src, x), Paths.get(dest, x));
            }
        }
        Files.move(currDirPath, Paths.get(dest, currDirPath.getFileName().toString()));
    }
}
