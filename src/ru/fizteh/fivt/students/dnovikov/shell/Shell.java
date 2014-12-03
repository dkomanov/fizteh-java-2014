package ru.fizteh.fivt.students.dnovikov.shell;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Shell {
    private String currPath = new String();

    Shell() {
        currPath = System.getProperty("user.dir");
    }

    public void packageMode(String[] str) {
        String commands = new String();
        for (String it : str) {
            it += " ";
            commands += it;
        }
        try {
            runCommands(commands);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void interactiveMode() {

        String str;
        System.out.print("$ ");
        try (Scanner scanner = new Scanner(System.in)) {
            do {
                str = scanner.nextLine();
                try {
                    runCommands(str);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            } while (true);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    public void runCommands(String str) throws Exception {
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempStr = new ArrayList<String>(Arrays.asList(str.trim().split(";")));

        for (String it : tempStr) {
            ArrayList<String> t = new ArrayList<String>(Arrays.asList(it.trim().split("\\s+")));
            commands.add(t);
        }

        for (ArrayList<String> it : commands) {

            String cmd = it.get(0);

            if (cmd.equals("exit")) {
                if (it.size() > 1) {
                    throw new Exception(cmd + ": too much arguments");
                }
                System.exit(0);
            } else if (cmd.equals("pwd")) {
                if (it.size() > 1) {
                    throw new Exception(cmd + ": too much arguments");
                }
                pwdCommand();
            } else if (cmd.equals("ls")) {
                if (it.size() > 1) {
                    throw new Exception(cmd + ": too much arguments");
                }
                lsCommand();
            } else if (cmd.equals("cd")) {
                try {
                    if (it.size() > 2) {
                        throw new Exception(cmd + ": too much arguments");
                    }
                    cdCommand(it.get(1));
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: cd <dirname>");
                }
            } else if (cmd.equals("cat")) {
                try {
                    if (it.size() > 2) {
                        throw new Exception(cmd + ": too much arguments");
                    }
                    catCommand(it.get(1));
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: cat <filename>");
                }
            } else if (cmd.equals("mkdir")) {
                try {
                    if (it.size() > 2) {
                        throw new Exception(cmd + ": too much arguments");
                    }
                    mkdirCommand(it.get(1));
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: mkdir <dirname>");
                }

            } else if (cmd.equals("rm")) {
                try {
                    if (it.get(1).equals("-r")) {
                        if (it.size() > 3) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        rmCommand(it.get(2), true);
                    } else {
                        if (it.size() > 2) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        rmCommand(it.get(1), false);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: rm [-r] <filename>");
                }
            } else if (cmd.equals("cp")) {
                try {
                    if (it.get(1).equals("-r")) {
                        if (it.size() > 4) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        cpCommand(it.get(2), it.get(3), true);
                    } else {
                        if (it.size() > 3) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        cpCommand(it.get(1), it.get(2), false);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: cp [-r] <source> <destination>");
                }
            } else if (cmd.equals("mv")) {
                try {
                    if (it.get(1).equals("-r")) {
                        if (it.size() > 4) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        mvCommand(it.get(2), it.get(3), true);
                    } else {
                        if (it.size() > 3) {
                            throw new Exception(cmd + ": too much arguments");
                        }
                        mvCommand(it.get(1), it.get(2), false);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("usage: mv [-r] <source> <destination>");
                }
            } else if (cmd.length() != 0) {
                throw new Exception(cmd + ": not found");
            }
        }


    }

    private void pwdCommand() {
        System.out.println(currPath);
    }

    private void lsCommand() throws Exception {
        File f = new File(currPath);

        File[] files = f.listFiles();
        if (files != null) {
            for (File it : files) {
                System.out.println(it.getName());
            }
        }
    }

    private void cdCommand(String argPath) throws Exception {

        Path path = new File(currPath).toPath();

        File newPath = path.resolve(argPath).toAbsolutePath().toFile();
        if (newPath.exists() && newPath.isDirectory()) {
            System.setProperty("user.dir", newPath.getCanonicalPath());
        } else if (!newPath.exists()) {
            throw new Exception("path '" + newPath.toString() + "' doesn't exist");
        } else {
            throw new Exception(argPath + " is not a directory");
        }
        currPath = System.getProperty("user.dir");
    }

    private void catCommand(String fileName) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            Path path = new File(currPath).toPath();
            File toRead = path.resolve(fileName).toAbsolutePath().toFile();

            fis = new FileInputStream(toRead);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            while (dis.available() != 0) {
                System.out.print((char) dis.read());
            }
            System.out.println();
        } catch (FileNotFoundException e) {
            throw new Exception("cat: '" + fileName + "': no such file");
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (dis != null) {
                dis.close();
            }
        }
    }

    private void mkdirCommand(String dirName) throws Exception {

        File newPath = new File(currPath + File.separator + dirName);
        try {
            Files.createDirectory(newPath.toPath());
        } catch (FileAlreadyExistsException e) {
            throw new Exception(dirName + ": already exists");
        }

    }

    private void delete(File toDelete) throws Exception {
        boolean isDeleted = false;

        if (toDelete.isFile()) {
            isDeleted = toDelete.delete();
            if (!isDeleted) {
                throw new Exception(toDelete.toString() + ":" + " cannot remove file or directory");
            }
        } else {
            String[] files = toDelete.list();
            for (String it : files) {
                delete(new File(toDelete, it));
            }
            isDeleted = toDelete.delete();
        }
        if (!isDeleted) {
            throw new Exception("cannot delete '" + toDelete.getAbsolutePath() + "'");
        }
    }

    private void rmCommand(String fileName, boolean isRec) throws Exception {

        boolean isDeleted = false;
        File toDelete = new File(fileName);
        Path path = new File(currPath).toPath();
        toDelete = path.resolve(fileName).toAbsolutePath().toFile();

        if (!isRec && toDelete.isDirectory()) {
            throw new Exception(fileName + " is directory");
        }
        if (!toDelete.exists()) {
            throw new Exception("cannot remove '" + fileName + "': no such file or directory");
        }

        if (toDelete.isFile() && !isRec) {
            isDeleted = toDelete.delete();
            if (!isDeleted) {
                throw new Exception(fileName + ":" + " cannot remove file or directory");
            }
            return;
        }
        delete(toDelete);
    }


    private void copy(File src, File dest) throws Exception {

        File destination = dest;
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                throw new Exception(dest + " is not directory");
            } else {
                destination = new File(dest.getCanonicalPath() + File.separator + src.getName());
            }
        }
        Files.copy(src.toPath(), destination.toPath());

        if (src.isDirectory()) {
            File[] files = src.listFiles();
            for (File f : files) {
                copy(f, destination);
            }
        }
    }

    private void copyFiles(File source, File destination) throws Exception {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(destination);
            int lengthRead;
            byte[] buffer = new byte[4096];
            while ((lengthRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, lengthRead);
            }

        } catch (Exception e) {
            throw new Exception("cannot read file");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private void cpCommand(String src, String dest, boolean isRec) throws Exception {

        Path path = new File(currPath).toPath();
        File source = new File(currPath + File.separator + src);
        File destination = new File(currPath + File.separator + dest);

        if (src.equals(dest)) {
            throw new Exception("cannot copy to itself");
        }
        if (!source.exists()) {
            throw new Exception(src + ": no such file or directory");
        }

        if (!destination.exists()) {
            if (source.isFile()) {
                copyFiles(source, destination);
            } else {
                if (!isRec) {
                    throw new Exception(src + " is directory");
                } else {
                    mkdirCommand(destination.getName());
                    copy(source, destination);
                }
            }
        } else {
            if (source.isFile() && destination.isFile()) {
                copyFiles(source, destination);
            } else if (source.isDirectory() && destination.isDirectory()) {
                if (!isRec) {
                    throw new Exception(src + " is directory");
                } else {
                    copy(source, destination);
                }
            } else if (source.isFile() && destination.isDirectory()) {
                try {
                    File newDest = new File(destination.getCanonicalPath() + File.separator + source.getName());
                    Files.copy(source.toPath(), newDest.toPath());
                } catch (FileAlreadyExistsException e) {
                    rmCommand(e.getMessage().toString(), false);
                    File newDest = new File(destination.getCanonicalPath() + File.separator + source.getName());
                    Files.copy(source.toPath(), newDest.toPath());
                }
            } else {
                throw new Exception("cannot copy directory '" + source.getName() + "' to file '"
                        + destination.getName() + "'");
            }
        }

    }

    private void move(File src, File dest) throws Exception {

        File destination = dest;
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                throw new Exception(dest + " is not directory");
            } else {
                destination = new File(dest.getCanonicalPath() + File.separator + src.getName());
            }
        }
        Files.move(src.toPath(), destination.toPath());

        if (src.isDirectory()) {
            File[] files = src.listFiles();
            for (File f : files) {
                copy(f, destination);
            }
        }
    }

    private void mvCommand(String src, String dest, boolean isRec) throws Exception {

        Path path = new File(currPath).toPath();
        File source = new File(currPath + File.separator + src);
        File destination = new File(currPath + File.separator + dest);

        if (src.equals(dest)) {
            throw new Exception("cannot move to itself");
        }
        if (!source.exists()) {
            throw new Exception(src + ": no such file or directory");
        }

        if (!destination.exists()) {
            if (source.isFile()) {
                copyFiles(source, destination);
                rmCommand(source.getName(), false);
            } else {
                if (!isRec) {
                    throw new Exception(src + " is directory");
                } else {
                    mkdirCommand(destination.getName());
                    move(source, destination);
                }
            }
        } else {
            if (source.isFile() && destination.isFile()) {
                copyFiles(source, destination);
                rmCommand(source.getName(), false);
            } else if (source.isDirectory() && destination.isDirectory()) {
                if (!isRec) {
                    throw new Exception(src + " is directory");
                } else {
                    move(source, destination);
                }
            } else if (source.isFile() && destination.isDirectory()) {
                try {
                    File newDest = new File(destination.getCanonicalPath() + File.separator + source.getName());
                    Files.move(source.toPath(), newDest.toPath());
                } catch (FileAlreadyExistsException e) {
                    rmCommand(e.getMessage().toString(), false);
                    File newDest = new File(destination.getCanonicalPath() + File.separator + source.getName());
                    Files.move(source.toPath(), newDest.toPath());
                }
            } else {
                throw new Exception("cannot move directory '" + source.getName() + "' to file '"
                        + destination.getName() + "'");
            }
        }
    }
}
