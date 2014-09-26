package ru.fizteh.fivt.students.dnovikov.shell;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Shell {
    private String currPath = new String();

    Shell() {
        currPath = System.getProperty("user.dir");
    }

    void packageMode(String[] str) {
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

    void interactiveMode() {

        Scanner scanner = new Scanner(System.in);
        String str;
        System.out.print("$ ");
        do {
            str = scanner.nextLine();
            try {
                runCommands(str);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("$ ");

        } while (true);
    }

    void runCommands(String str) throws Exception {
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
        if (newPath.exists() == true && newPath.isDirectory() == true) {
            System.setProperty("user.dir", newPath.getCanonicalPath());
        } else if (newPath.exists() == false)
            throw new Exception("path '" + newPath.toString() + "' doesn't exist");
        else
            throw new Exception(argPath + " is not a directory");
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
            fis = new FileInputStream(toRead);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            while (dis.available() != 0) {
                System.out.println(dis.readLine());
            }
        } catch (FileNotFoundException e) {
            throw new Exception("cat: '" + fileName + "': no such file");
        } finally {
            if (fis != null)
                fis.close();
            if (bis != null)
                bis.close();
            if (dis != null)
                dis.close();
        }

    }

    private void mkdirCommand(String dirName) throws Exception {

        File newPath = new File(currPath + File.separator + dirName);
        try {
            Files.createDirectory(newPath.toPath());
        } catch (FileAlreadyExistsException e) {
            System.err.println(dirName + ": already exists");
        }

    }

    private void delete(File toDelete) throws Exception {
        boolean isDeleted = false;

        if (toDelete.isFile()) {
            isDeleted = toDelete.delete();
            if (!isDeleted)
                throw new Exception(toDelete.toString() + ":" + " cannot remove file or directory");
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
        if (toDelete.isFile() && isRec) {
            throw new Exception("cannot remove '" + fileName + "' is file");
        }

        if (toDelete.isFile() && !isRec) {
            isDeleted = toDelete.delete();
            if (!isDeleted)
                throw new Exception(fileName + ":" + " cannot remove file or directory");
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
            throw new Exception(destination + ": no such file or directory");
        }
        if (!destination.isDirectory()) {
            throw new Exception(dest + ": is not directory");
        }
        if (source.isDirectory() && !isRec) {
            throw new Exception(src + " is directory");
        }

        copy(source, destination);
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

        if (src.equals("dest")) {
            throw new Exception("cannot move to itself");
        }
        if (!source.exists()) {
            throw new Exception(src + ": no such file or directory");
        }
        if (!destination.exists()) {
            throw new Exception(destination + ": no such file or directory");
        }
        if (!destination.isDirectory()) {
            throw new Exception(dest + ": is not directory");
        }
        if (source.isDirectory() && !isRec) {
            throw new Exception(src + " is directory");
        }
        move(source, destination);
    }
}
