package ru.fizteh.fivt.students.ilivanov.shell;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandCp implements Command {
    private ArrayList<String> parameters;
    private boolean recursively;

    CommandCp(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 3 && parameters.size() != 4) {
            throw new Exception("wrong number of parameters");
        } else {
            if (parameters.size() == 4 && !parameters.get(1).equals("-r")) {
                throw new Exception("wrong parameters");
            } else {
                this.parameters = new ArrayList<>(parameters);
                recursively = (parameters.size() == 4);
            }
        }
    }

    @Override
    public int execute() {
        String source;
        String destination;
        if (!recursively) {
            source = parameters.get(1);
            destination = parameters.get(2);
        } else {
            source = parameters.get(2);
            destination = parameters.get(3);
        }
        try {
            Path srcPath = Paths.get(Shell.currentDirectory.getCanonicalPath());
            srcPath = srcPath.resolve(source);
            File srcFile = new File(srcPath.toAbsolutePath().toString());
            if (!srcFile.exists()) {
                System.err.println("cp: \"" + source + "\": no such file");
                return -1;
            } else {

                Path destPath = Paths.get(Shell.currentDirectory.getCanonicalPath());
                destPath = destPath.resolve(destination);
                File destFile = new File(destPath.toAbsolutePath().toString());
                if (!destFile.exists() || !destFile.isDirectory()) {
                    System.err.println("cp: \"" + destination + "\": no such directory");
                    return -1;
                }
                if (destFile.equals(srcFile)) {
                    System.err.println("cp: source and destination are the same");
                    return -1;
                }
                if (new File(destFile, source).exists()) {
                    System.err.println("cp: such file or directory already exists");
                    return -1;
                }

                if (srcFile.isDirectory() && recursively) {
                    copyRecursively(srcFile, new File(destFile, source));
                } else {
                    if (srcFile.isDirectory()) {
                        if (!new File(destFile, source).mkdir()) {
                            System.err.println("cp: can't copy");
                            return -1;
                        }
                    } else {
                        copyFile(srcFile, new File(destFile, source));
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("cp: " + e.getMessage());
            return -1;
        }
        return 0;
    }

    void copyRecursively(final File source, final File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private void copyDirectory(final File source, final File destination) throws IOException {
        if (!destination.exists()) {
            if (!destination.mkdir()) {
                return;
            }
        }
        for (String f : source.list()) {
            copyRecursively(new File(source, f), new File(destination, f));
        }
    }

    private void copyFile(final File source, final File destination) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }
}
