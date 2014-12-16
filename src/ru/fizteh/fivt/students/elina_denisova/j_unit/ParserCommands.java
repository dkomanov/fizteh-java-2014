package ru.fizteh.fivt.students.elina_denisova.j_unit;

import java.util.ArrayList;
import java.util.Map;

public class ParserCommands {

    private static int numberChanges = 0;

    public static void commandsExecution(String[] command, MyTableProvider directory)
            throws IllegalMonitorStateException {
        if (command.length == 0) {
            return;
        }
        try {
            Commands request = Commands.getCommand(command[0]);
            switch (request) {
                case CREATECOMMAND:
                    try {
                        if (directory.createTable(command[1]) == null) {
                            System.out.println(command[1] + " exists");
                        }
                        else {
                            System.out.println("created");
                        }
                    } catch (UnsupportedOperationException e) {
                        HandlerException.handler(e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("create: need argument");
                    }
                    break;
                case PUTCOMMAND:
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            String oldValue = directory.getUsing().put(command[1], command[2]);
                            if (oldValue != null) {
                                System.out.println("overwrite");
                                System.out.println(oldValue);
                            } else {
                                System.out.println("new");
                            }
                            numberChanges++;
                        }
                    } catch (UnsupportedOperationException e) {
                        HandlerException.handler(e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("put: need two arguments");
                    } catch (NullPointerException e) {
                        HandlerException.handler(e);
                    } catch (Exception e) {
                        HandlerException.handler("ParserCommands.commandsExecution.put: Unknown error", e);
                    }
                    break;
                case GETCOMMAND:
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            if (directory.getUsing().get(command[1]) != null) {
                                System.out.println("found");
                                System.out.println(directory.getUsing().get(command[1]));
                            } else {
                                System.out.println("not found");
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        HandlerException.handler(e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("get: need argument");
                    }
                    break;
                case REMOVECOMMAND:
                    try {
                        if (directory.getUsing() == null) {
                            System.out.println("no table");
                        } else {
                            String oldValue = directory.getUsing().remove(command[1]);
                            if (oldValue != null) {
                                System.out.println("removed");
                                numberChanges++;
                            } else {
                                System.out.println("not found");
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("remove: need argument");
                    }
                    break;
                case DROPCOMMAND:
                    try {
                        if (directory.getTable(command[1]) == directory.getUsing()) {
                            numberChanges = 0;
                        }
                        directory.removeTable(command[1]);
                        System.out.println("dropped");
                    } catch (IllegalStateException e) {
                        System.out.println(command[1] + " not exist");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("drop: need argument");
                    }
                    break;
                case COMMITCOMMAND:
                    directory.getUsing().commit();
                    System.out.println(numberChanges);
                    numberChanges = 0;
                    break;
                case ROLLBACKCOMMAND:
                    System.out.println(directory.getUsing().rollback());
                    numberChanges = 0;
                    break;
                case USECOMMAND:
                    try {
                        if (numberChanges == 0) {
                            if (!directory.containsKey(command[1])) {
                                System.err.println("use: " + command[1] + " not exists");
                            } else {
                                directory.changeUsingTable(command[1]);
                                System.out.println("using " + command[1]);
                            }
                        }
                        else
                            System.err.println(numberChanges + "  unsaved changes");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("use: need argument");
                    }
                    break;
                case SHOWCOMMAND:
                    try {
                        if (command[1].equals("tables")) {
                            for (Map.Entry<String, MyTable> entry: directory.entrySet()) {
                                String name = entry.getKey();

                                int size = entry.getValue().size();
                                System.out.println(name + " " + size);
                            }

                        } else {
                            throw new IllegalArgumentException();
                        }

                    } catch (IllegalArgumentException e) {
                        System.err.println(command[0] + " - unknown command");
                    }
                    break;
                case LISTCOMMAND:
                    if (directory.getUsing() == null) {
                        System.out.println("no table");
                    } else {
                        System.out.println(directory.getUsing().list());
                    }
                    break;
                case EXITCOMMAND:
                    if (numberChanges == 0) {
                        throw new IllegalMonitorStateException("Exit");
                    }
                    else {
                        System.err.println(numberChanges + "  unsaved changes");
                    }
                    break;
                default:
                    System.err.println(command[0] + " - unknown command");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.toString());
        }
    }



    public static void commandsExecution(ArrayList<String> commands, MyTableProvider directory) {
        if (commands.size() == 0) {
            return;
        }
        String[] com = new String[commands.size()];
        com = commands.toArray(com);
        commandsExecution(com, directory);
    }
}
