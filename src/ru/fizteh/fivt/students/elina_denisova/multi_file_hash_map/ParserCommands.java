package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class ParserCommands {

    public static void commandsExecution(String[] command, TableProviderFactory directory)
            throws IllegalMonitorStateException {
        if (command.length == 0) {
            return;
        }
        try {
            Commands request = Commands.getCommand(command[0]);
            switch (request) {
                case CREATECOMMAND:
                    try {
                        if (directory.tables.containsKey(command[1])) {
                            System.out.println(command[1] + " exists");
                        } else {
                            File newTable = new File(directory.parentDirectory, command[1]);
                            if (!newTable.mkdir()) {
                                throw new UnsupportedOperationException("ParserCommands.commandsExecution.create: "
                                        + "Unable to create working directory for new table");
                            }
                            directory.tables.put(command[1], new TableProvider(newTable));
                            System.out.println("created");
                        }

                    } catch (UnsupportedOperationException e) {
                        HandlerException.handler(e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("create: need argument" + e);
                    }
                    break;
                case DROPCOMMAND:
                    directory.getUsing().commit();
                    try {
                        if (!directory.tables.containsKey(command[1])) {
                            System.out.println(command[1] + " not exist");
                        } else {
                            directory.tables.get(command[1]).drop();
                            directory.tables.remove(command[1]);
                            System.out.println("dropped");
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("drop: need argument" + e);
                    }
                    break;
                case USECOMMAND:
                    try {
                        if (!directory.tables.containsKey(command[1])) {
                            System.err.println("use: " + command[1] + " not exists");
                        } else {
                            directory.using = command[1];
                            System.out.println("using " + command[1]);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("use: need argument" + e);
                    }
                    break;
                case SHOWCOMMAND:
                    try {
                        if (command[1].equals("tables")) {
                            for (Map.Entry<String, TableProvider> entry: directory.tables.entrySet()) {
                                String name = entry.getKey();
                                int size = entry.getValue().recordsNumber();
                                System.out.println(name + " " + size);
                            }

                        } else {
                            throw new IllegalArgumentException();
                        }

                    } catch (IllegalArgumentException e) {
                        System.err.println(command[0] + " - unknown command" + e);
                    }
                    break;
                case PUTCOMMAND:
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            int hashCode = Math.abs(command[1].hashCode());
                            int dir = hashCode % 16;
                            int file = hashCode / 16 % 16;
                            if (directory.getUsing().databases[dir][file] == null) {
                                File subDir = new File(directory.getUsing().mainDir, dir + ".dir");
                                if (!subDir.exists()) {
                                    if (!subDir.mkdir()) {
                                        throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                                + " Unable to create directories in working catalog");
                                    }
                                }
                                File dbFile = new File(subDir, file + ".dat");
                                if (!dbFile.exists()) {
                                    if (!dbFile.createNewFile()) {
                                        throw new UnsupportedOperationException("ParserCommands.commandsExecution.put:"
                                                + " Unable to create database files in working catalog");
                                    }
                                }
                                directory.getUsing().databases[dir][file] = new Table(dbFile.toString());
                            }
                            directory.getUsing().databases[dir][file].dbPut(command[1], command[2]);
                        }
                    } catch (UnsupportedOperationException e) {
                        HandlerException.handler(e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("put: need two arguments" + e);
                    } catch (IOException e) {
                        HandlerException.handler("ParserCommands.commandsExecution.put:"
                                + " Problem with creation of file", e);
                    } catch (Exception e) {
                        HandlerException.handler("ParserCommands.commandsExecution.put: Unknown error", e);
                    }
                    break;
                case GETCOMMAND:
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            int hashCode = Math.abs(command[1].hashCode());
                            int dir = hashCode % 16;
                            int file = hashCode / 16 % 16;
                            Table db = directory.getUsing().databases[dir][file];
                            if (db == null) {
                                System.out.println("not found");
                            } else {
                                directory.getUsing().databases[dir][file].dbGet(command[1]);
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("get: need argument" + e);
                    }
                    break;
                case REMOVECOMMAND:
                    directory.getUsing().commit();
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            int hashCode = Math.abs(command[1].hashCode());
                            int dir = hashCode % 16;
                            int file = hashCode / 16 % 16;
                            if (directory.getUsing().databases[dir][file] == null) {
                                System.out.println("not found");
                            } else {
                                Table db = directory.getUsing().databases[dir][file];
                                db.dbRemove(command[1]);
                                if (db.recordsNumber() == 0) {
                                    File dbFile = new File(db.dbFileName.toString());
                                    Path parentFile = dbFile.getParentFile().toPath();
                                    try {
                                        Files.delete(dbFile.toPath());
                                    } catch (IOException e) {
                                        HandlerException.handler("ParserCommands.commandsExecution.remove: "
                                                + "cannon delete database file ", e);
                                    }
                                    directory.getUsing().databases[dir][file] = null;

                                    int k = 0;
                                    for (int j = 0; j < 16; j++) {
                                        if (directory.getUsing().databases[dir][j] == null) {
                                            k++;
                                        }
                                    }
                                    if (k == 16) {
                                        try {
                                            Files.delete(parentFile);
                                        } catch (DirectoryNotEmptyException e) {
                                            HandlerException.handler("Cannot remove table subdirectory. "
                                                    + "Redundant files", e);
                                        } catch (IOException e) {
                                            HandlerException.handler("ParserCommands.commandsExecution.remove: "
                                                    + "cannot delete database subdirectory", e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("remove: need argument" + e);
                    }
                    break;
                case LISTCOMMAND:
                    if (directory.getUsing() == null) {
                        System.out.println("no table");
                    } else {
                        StringBuilder allKeys = new StringBuilder();
                        for (int i = 0; i < 16; i++) {
                            for (int j = 0; j < 16; j++) {
                                Table cur = directory.getUsing().databases[i][j];
                                if (cur != null) {
                                    cur.dbList();
                                }
                            }
                        }
                        System.out.println(allKeys.toString());
                    }
                    break;
                case EXITCOMMAND:
                    directory.getUsing().commit();
                    throw new IllegalMonitorStateException("Exit");
                default:
                    System.err.println(command[0] + " - unknown command");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.toString());
        }
    }



    public static void commandsExecution(ArrayList<String> commands, TableProviderFactory directory) {
        if (commands.size() == 0) {
            return;
        }
        String[] com = new String[commands.size()];
        com = commands.toArray(com);
        commandsExecution(com, directory);
    }
}
