package ru.fizteh.fivt.students.ryad0m.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Runner {
    public boolean fail = false;
    private Path curPath = Paths.get("").toAbsolutePath().normalize();

    Runner() {
    }

    void exit(String[] args) {
        if (args.length > 1) {
            System.out.println("Command 'exit' must have no arguments, but exiting...");
            fail = true;
        }
        System.exit(0);
    }

    void cp(String[] args) {
        if (args.length == 4) {
            if (!args[1].equals("-r")) {
                System.out.println("The only acceptable option is -r");
                fail = true;
            } else {
                Path filePath = curPath.resolve(args[2]).normalize();
                Path resPath = curPath.resolve(args[3]).normalize();
                File file = filePath.toFile();
                if (resPath.toFile().isDirectory()) {
                    resPath = resPath.resolve(file.getName()).normalize();
                }
                File resFile = resPath.toFile();
                if (!file.exists() || resFile.exists()) {
                    System.err.println("File not found.");
                    fail = true;
                } else {
                    try {
                        Files.copy(filePath, resPath);
                    } catch (IOException e) {
                        System.err.println("Error with copying " + e.getMessage());
                        fail = true;
                    }
                }
            }
        } else if (args.length == 3) {
            Path filePath = curPath.resolve(args[1]).normalize();
            Path resPath = curPath.resolve(args[2]).normalize();
            File file = filePath.toFile();
            if (resPath.toFile().isDirectory()) {
                resPath = resPath.resolve(file.getName()).normalize();
            }
            File resFile = resPath.toFile();
            if (!file.exists() || resFile.exists()) {
                System.err.println("File not found.");
                fail = true;
            } else if (file.isDirectory()) {
                System.out.println("cp: " + args[1] + " is a directory (not copied).");
                fail = true;
            } else {
                try {
                    Files.copy(filePath, resPath);
                } catch (IOException e) {
                    System.err.println("Error with copying ");
                    fail = true;
                }
            }
        } else {
            System.err.println("Wrong arguments");
            fail = true;
        }
    }

    void mkdir(String[] args) {
        if (args.length != 2) {
            System.err.println("Command cd must take only 1 argument.");
            fail = true;
        } else {
            if (!curPath.resolve(args[1]).normalize().toFile().mkdir()) {
                System.err.println("Error with mkdir");
                fail = true;
            }
        }
    }

    void cd(String[] args) {
        if (args.length != 2) {
            System.err.println("Command cd must take only 1 argument.");
            fail = true;
        } else {
            Path newPath = curPath.resolve(args[1]).normalize();
            File file = newPath.toFile();
            if (!file.isDirectory()) {
                System.out.println("cd: '" + args[1] + "': No such file or directory");
                fail = true;
            } else {
                curPath = newPath;
            }
        }
    }

    void pwd(String[] args) {
        if (args.length > 1) {
            System.err.println("Command 'pwd' must have no arguments, but...");
            fail = true;
        }
        System.out.println(curPath.toAbsolutePath().toString());
    }


    void deleteDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else if (!f.delete()) {
                    System.err.println("Delete error");
                    fail = true;
                }
            }
        }
        if (!dir.delete()) {
            System.err.println("Delete error");
            fail = true;
        }
    }

    void rm(String[] args) {
        if (args.length == 3) {
            if (!args[1].equals("-r")) {
                System.err.println("The only acceptable option is -r");
                fail = true;
            } else {
                Path filePath = curPath.resolve(args[2]).normalize();
                File file = filePath.toFile();
                if (file.isDirectory()) {
                    deleteDir(file);
                } else if (!file.exists()) {
                    System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory");
                    fail = true;
                } else if (!file.delete()) {
                    System.err.println("Can't delete " + args[2]);
                    fail = true;
                }
            }
        } else if (args.length == 2) {
            Path filePath = curPath.resolve(args[1]).normalize();
            File file = filePath.toFile();
            if (file.isDirectory()) {
                System.out.println("rm: " + args[1] + ": is a directory");
                fail = true;
            } else if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                fail = true;
            } else if (!file.delete()) {
                System.err.println("Can't delete " + args[1]);
                fail = true;
            }
        } else {
            System.err.println("Wrong arguments");
            fail = true;
        }
    }

    void ls(String[] args) {
        if (args.length != 1) {
            System.err.println("ls: Wrong arguments.");
            fail = true;
        }
        String[] files = curPath.toFile().list();
        for (String file : files) {
            System.out.println(file);
        }
    }


    void cat(String[] args) {
        if (args.length != 2) {
            System.err.println("Command cat must take only 1 argument.");
            fail = true;
        } else {
            Path filePath = curPath.resolve(args[1]).normalize();
            File file = filePath.toFile();
            if (!file.exists()) {
                System.out.println("cat: " + args[1] + ": No such file or directory");
                fail = true;
            } else if (!file.canRead()) {
                System.err.println("Can't read from file.");
                fail = true;
            } else {
                byte[] buff = null;
                try {
                    buff = Files.readAllBytes(filePath);
                } catch (IOException e) {
                    System.err.println("Read error: " + e.getMessage());
                    fail = true;
                }
                if (buff != null) {
                    try {
                        System.out.write(buff);
                    } catch (IOException e) {
                        System.err.println("Write error: " + e.getMessage());
                        fail = true;
                    }
                }
            }
        }
    }


    void mv(String[] args) {
        if (args.length != 3) {
            System.err.println("Wrong arguments");
            fail = true;
        } else {
            File file = curPath.resolve(args[1]).normalize().toFile();
            Path res = curPath.resolve(args[2]).normalize();
            if (res.toFile().isDirectory()) {
                res = res.resolve(file.getName()).normalize();
            }
            File newFile = res.toFile();
            if (!file.renameTo(newFile)) {
                System.err.println("Error");
                fail = true;
            }
        }
    }

    void runCommand(String[] args) {
        if (args.length == 0) {
            return;
        }
        if (args[0].equals("exit")) {
            exit(args);
        } else if (args[0].equals("cp")) {
            cp(args);
        } else if (args[0].equals("mkdir")) {
            mkdir(args);
        } else if (args[0].equals("cd")) {
            cd(args);
        } else if (args[0].equals("pwd")) {
            pwd(args);
        } else if (args[0].equals("rm")) {
            rm(args);
        } else if (args[0].equals("ls")) {
            ls(args);
        } else if (args[0].equals("cat")) {
            cat(args);
        } else if (args[0].equals("mv")) {
            mv(args);
        } else {
            System.err.println("Command '" + args[0] + "' not found");
            fail = true;
        }
    }
}
