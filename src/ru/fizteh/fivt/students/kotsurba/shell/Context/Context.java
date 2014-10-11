package ru.fizteh.fivt.students.kotsurba.shell.Context;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;

import java.io.*;
import java.nio.channels.FileChannel;


public final class Context {
    private String currentDir;

    public String getCurrentDir() {
        return currentDir;
    }

    public Context() throws IOException {
        currentDir = new File(".").getCanonicalPath();
    }

    private boolean existsFile(final String path) {
        return new File(path).exists();
    }

    private String buildPath(final String curDir, final String relativePath) throws IOException {
        return new File(new File(curDir).getAbsoluteFile(), relativePath).getAbsoluteFile().getCanonicalPath();
    }

    public String changePath(final String curDir, final String path)
            throws IOException {
        if (path.charAt(0) != '.') {
            if (!existsFile(path)) {
                return buildPath(curDir, path);
            }
            return new File(path).getCanonicalPath();
        } else {
            return buildPath(curDir, path);
        }
    }

    public void changeDir(final String path) throws IOException {
        String newDir = changePath(currentDir, path);

        File file = new File(newDir);
        if ((!file.exists()) || (file.isFile())) {
            throw new IOException("Wrong path!");
        }

        currentDir = newDir;
    }

    public void makeDir(final String name) throws IOException {
        File dir = new File(currentDir + File.separator + name);
        if (!dir.mkdir()) {
            throw new IOException("Wrong directory name!");
        }
    }

    public void makeFullDir(final String name) throws IOException {
        File dir = new File(name);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create dirs! " + name);
        }
    }

    public String[] getDirContent() {
        return new File(currentDir).list();
    }

    private void recursiveRemove(final File file) throws IOException {
        if (file.isFile()) {
            if (!file.delete()) {
                throw new IOException("File " + file.getCanonicalPath() + " is undeletable");
            }
        } else {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; ++i) {
                recursiveRemove(new File(file.getCanonicalPath() + File.separator + fileList[i]));
            }
            if (!file.delete()) {
                throw new IOException("Path " + file.getAbsolutePath() + " is undeletable.");
            }
        }
    }

    public void remove(final String path) throws IOException {
        String newPath = changePath(currentDir, path);

        if (!existsFile(newPath)) {
            throw new IOException("Bad path/file name.");
        }

        if (newPath == currentDir) {
            throw new IOException("I can't delete current directory!");
        }
        recursiveRemove(new File(newPath));
    }

    private void copyFile(final String src, final String dest) throws IOException {
        File file = new File(dest);

        if (file.isDirectory()) {
            String newDest = dest + File.separator + (new File(src).getName());
            file = new File(newDest);
        }

        if (!file.createNewFile()) {
            throw new IOException("Cannot create file " + dest);
        }

        FileChannel source = new FileInputStream(src).getChannel();
        FileChannel destination = new FileOutputStream(dest).getChannel();

        destination.transferFrom(source, 0, source.size());

        source.close();
        destination.close();
    }

    private void recursiveCopy(final String source, final String destination, final String addition)
            throws IOException {
        File file = new File(source);
        if (file.isFile()) {
            copyFile(source, destination + addition);
        } else {
            makeFullDir(destination + addition);
            String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                recursiveCopy(source + File.separator + list[i],
                        destination, addition + File.separator + list[i]);
            }
        }
    }

    public void copy(final String src, final String dest) throws IOException {
        String source = changePath(currentDir, src);
        String destination = changePath(currentDir, dest);
        File file = new File(source);

        if (!file.exists()) {
            throw new InvalidCommandException("Source file doesn't exist!");
        }

        if (source.equals(destination)) {
            throw new InvalidCommandException("Cannot move myself to myself.");
        }

        if (file.isFile()) {
            copyFile(source, destination);
        } else {
            if (!new File(destination).exists()) {
                if (!new File(destination).mkdir()) {
                    throw new InvalidCommandException("Destination dir creation error!");
                }
                recursiveCopy(source, destination, "");
            } else {
                recursiveCopy(source, destination, File.separator + (file.getName()));
            }
        }
    }

    public void move(final String src, final String dest) throws IOException {
        String source = changePath(currentDir, src);
        copy(src, dest);
        remove(source);
    }

    public void cat(String src) {
        boolean isOk = false;
        try {
            File f = new File(changePath(currentDir, src));
            if (f.exists() && f.isFile()) {
                BufferedReader fin = new BufferedReader(new FileReader(f.getCanonicalFile()));
                String line;
                while ((line = fin.readLine()) != null) {
                    System.out.println(line);
                }
                isOk = true;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            System.out.println("Bad file!");
        }

        if (!isOk) {
            System.out.println("File not found!");
        }
    }

}
