package ru.fizteh.fivt.students.SmirnovAlexandr.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class CommandCp {
    public static void execute(final String[] args) throws IOException {
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("cp: too few arguments");
            } else if (args.length > 3) {
                throw new IllegalArgumentException("cp: too many arguments");
            } else if (args.length == 2) {
                if (args[0].equals("-r")) {
                    throw new IllegalArgumentException("cp: wrong syntax");
                } else if (!Files.exists(Paths.get(Shell.getWorkingDir(), args[0]))) {
                    throw new IllegalArgumentException("cp: '" + args[0] + "': No such file or directory");
                } else {
                    String src = Paths.get(Shell.getWorkingDir(), args[0]).normalize().toString();
                    String dest = Paths.get(Shell.getWorkingDir(), args[1]).normalize().toString();
                    cp(src, dest);
                }
            } else {
                if (!args[0].equals("-r")) {
                    throw new IllegalArgumentException("cp: wrong syntax");
                } else if (!Files.exists(Paths.get(Shell.getWorkingDir(), args[1]))) {
                    throw new IllegalArgumentException("cp: '" + args[1] + "': No such file or directory");
                } else {
                    String src = Paths.get(Shell.getWorkingDir(), args[1]).normalize().toString();
                    String dest = Paths.get(Shell.getWorkingDir(), args[2]).normalize().toString();
                    cpRecursivelyParser(src, dest);
                }
            }
        } catch (IOException e) {
            throw new IOException("cp: cannot copy");
        }
    }

    private static void cp(final String src, final String dest) throws IOException {
        Path srcPath = Paths.get(src);
        Path destPath = Paths.get(dest);
        if (Files.isDirectory(srcPath) && !isEmptyDir(srcPath)) {
            throw new IllegalArgumentException("cp: omitting directory '" + srcPath.getFileName().toString() + "'");
        }
        if (Files.exists(destPath)) {
            if (Files.isDirectory(destPath)) {
                Files.copy(srcPath, Paths.get(destPath.toString(),
                        srcPath.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (Files.isDirectory(srcPath)) {
                    throw new IllegalArgumentException("cp: cannot copy dir to file");
                } else {
                    Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } else {
            Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void cpRecursivelyParser(final String src, final String dest) throws IOException {
        Path srcPath = Paths.get(src);
        Path destPath = Paths.get(dest);
        if (destPath.startsWith(srcPath)) {
            throw new IllegalArgumentException("cp: cannot copy folder in itself");
        } else if (!Files.isDirectory(srcPath)) {
            cp(src, dest);
        } else {
            if (Files.exists(destPath)) {
                cpRecursively(src, dest);
            } else {
                Files.createDirectory(destPath);
                cpRecursively(src, dest);
            }
        }
    }

    private static void cpRecursively(final String src, final String dest) throws IOException {
        Path currDirPath = Paths.get(src);
        String[] currDirContent = new File(currDirPath.toString()).list();
        for (String x : currDirContent) {
            Path srcFilePath = Paths.get(src, x);
            Path destFilePath = Paths.get(dest, x);
            if (Files.isDirectory(srcFilePath)) {
                Files.createDirectory(destFilePath);
                cpRecursively(srcFilePath.toString(), destFilePath.toString());
            } else {
                Files.copy(srcFilePath, destFilePath);
            }
        }
    }

    private static boolean isEmptyDir(final Path dirPath) {
        if (Files.isDirectory(dirPath)) {
            File[] dirContent = new File(dirPath.toString()).listFiles();
            if (dirContent.length == 0) {
                return true;
            }
        }
        return false;
    }
}
