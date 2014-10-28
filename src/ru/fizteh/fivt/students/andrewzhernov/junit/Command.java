package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

public class Command {
    public static void remove(Path directory) throws Exception {
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    remove(entry);
                }
            }
        }
        if (!directory.toFile().delete()) {
            throw new Exception("Can't remove " + directory.toString());
        }
    }

    public static String[] parseInput(String[] args) throws Exception {
        StringBuilder input = new StringBuilder();
        for (String cmd : args) {
            input.append(cmd).append(';');
        }
        return input.toString().split("\\s*;\\s*");
    }

    public static String[] parseCmd(String cmd) throws Exception {
        return cmd.trim().split("\\s+");
    }

    public static void exec(String[] cmd, TableProvider database) throws Exception {
        if (cmd.length > 0 && cmd[0].length() > 0) {
            if (cmd[0].equals("create")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: create <tablename>");
                }
                database.createTable(cmd[1]);
                database.printCommandOutput();
            } else if (cmd[0].equals("drop")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: drop <tablename>");
                }
                database.removeTable(cmd[1]);
                database.printCommandOutput();
            } else if (cmd[0].equals("use")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: use <tablename>");
                }
                database.useTable(cmd[1]);
                database.printCommandOutput();
            } else if (cmd[0].equals("show")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: show tables");
                } else if (cmd[1].equals("tables")) {
                    database.showTables();
                    database.printCommandOutput();
                }
            } else if (cmd[0].equals("size")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: size");
                }
                database.getCurrentTable().size();
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("put")) {
                if (cmd.length != 3) {
                    throw new Exception("Usage: put <key> <value>");
                }
                database.getCurrentTable().put(cmd[1], cmd[2]);
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("get")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: get <key>");
                }
                database.getCurrentTable().get(cmd[1]);
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("remove")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: remove <key>");
                }
                database.getCurrentTable().remove(cmd[1]);
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("list")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: list");
                }
                database.getCurrentTable().list();
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("commit")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: commit");
                }
                database.getCurrentTable().commit();
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("rollback")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: rollback");
                }
                database.getCurrentTable().rollback();
                database.getCurrentTable().printCommandOutput();
            } else if (cmd[0].equals("exit")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: exit");
                }
                database.exit();
            } else {
                throw new Exception(cmd[0] + ": no such command");
            }
        }
    }
}
