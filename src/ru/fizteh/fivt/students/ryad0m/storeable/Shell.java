package ru.fizteh.fivt.students.ryad0m.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Shell {

    private Table table;
    private TableProvider database;
    private HashMap<String, ShellCommand> commands = new HashMap<String, ShellCommand>();


    private interface ShellCommand {
        void exec(String[] args, String line) throws ShellException, ParseException, IOException;
    }

    {
        commands.put("get", (args, line) -> {
            if (args.length != 2) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                Storeable val = table.get(args[1]);
                if (val != null) {
                    System.out.println("found");
                    System.out.println(database.serialize(table, val));
                } else {
                    System.out.println("not found");
                }
            }
        });

        commands.put("put", (args, line) -> {
            if (args.length < 3) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                String value = ParserUtils.getWithoutFirst(ParserUtils.getWithoutFirst(line)).trim();
                if (table.put(args[1], database.deserialize(table, value)) != null) {
                    System.out.println("overwrite");
                    System.out.println("old " + table.get(args[1]));
                } else {
                    System.out.println("new");
                }
            }
        });

        commands.put("list", (args, line) -> {
            if (args.length != 1) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                List<String> keys = table.list();
                for (int i = 0; i + 1 < keys.size(); ++i) {
                    System.out.print(keys.get(i) + ", ");
                }
                if (keys.size() > 0) {
                    System.out.println(keys.get(keys.size() - 1));
                }

            }
        });

        commands.put("remove", (args, line) -> {
            if (args.length != 2) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                if (table.remove(args[1]) != null) {
                    System.out.println("removed");
                } else {
                    System.out.println("not found");
                }
            }
        });
        commands.put("drop", (args, line) -> {
            if (args.length != 2) {
                throw new ShellException();
            } else {
                try {
                    database.removeTable(args[1]);
                } catch (IllegalStateException ex) {
                    System.out.println(args[1] + " not exists");
                    return;
                }
                System.out.println("dropped");
            }
        });
        commands.put("show", (args, line) -> {
            if (args.length != 2) {
                throw new ShellException();
            } else if (args[1].equals("tables")) {
                for (String entry : database.getTableNames()) {
                    System.out.print(entry + " ");
                    System.out.println(database.getTable(entry).size());
                }
            } else {
                System.out.println("unrecognized option");
            }
        });
        commands.put("create", (args, line) -> {
            if (args.length < 2) {
                throw new ShellException();
            } else {
                String value = ParserUtils.getWithoutFirst(ParserUtils.getWithoutFirst(line)).trim();
                value = value.substring(1, value.length() - 1);
                if (database.createTable(args[1], Typer.typeListFromString(value)) == null) {
                    System.out.println(args[1] + " exists");
                } else {
                    System.out.println("created");
                }
            }
        });
        commands.put("size", (args, line) -> {
            if (args.length != 1) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                System.out.println(table.size());
            }
        });
        commands.put("commit", (args, line) -> {
            if (args.length != 1) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                System.out.println(table.commit());
            }
        });
        commands.put("rollback", (args, line) -> {
            if (args.length != 1) {
                throw new ShellException();
            } else if (table == null) {
                System.out.println("No table");
            } else {
                System.out.println(table.rollback());
            }
        });
        commands.put("use", (args, line) -> {
            if (args.length != 2) {
                throw new ShellException();
            } else if (table != null && table.getNumberOfUncommittedChanges() != 0) {
                System.out.print(table.getNumberOfUncommittedChanges());
                System.out.println(" unsaved changes");
            } else {
                table = database.getTable(args[1]);
                if (table != null) {
                    System.out.println("using " + args[1]);
                } else {
                    System.out.println(args[1] + " not exist");
                }
            }
        });
        commands.put("exit", (args, line) -> {
            if (args.length > 1) {
                throw new ShellException();
            }
            System.exit(0);
        });
    }


    public Shell(TableProvider tableProvider) {
        database = tableProvider;
    }

    private void runCommand(String[] args, String line) throws ShellException, IOException, ParseException {
        if (commands.containsKey(args[0])) {
            commands.get(args[0]).exec(args, line);
        } else {
            throw new ShellException();
        }
    }

    private void parseLine(String line) throws ShellException, IOException, ParseException {
        String[] commands = line.split(";");
        for (String someCommand : commands) {
            String command = someCommand.trim().replaceAll("\\s+", " ");
            if (!command.equals("")) {
                runCommand(command.split(" "), line);
            }
        }
    }

    private void interactiveMode() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        System.out.flush();
        while (scanner.hasNextLine()) {
            try {
                parseLine(scanner.nextLine());
            } catch (ShellException e) {
                System.out.println("Command error");
            } catch (ParseException e) {
                System.out.println("Parse exception: " + e.getMessage());
            }
            System.out.print("$ ");
            System.out.flush();
        }
    }

    private void notInteractiveMode(String[] args) throws ShellException, IOException, ParseException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(' ');
        }
        parseLine(sb.toString());
    }

    public void start(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                notInteractiveMode(args);
            }
        } catch (ShellException e) {
            System.out.println("Command parse error");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO error");
            System.exit(1);
        } catch (ParseException e) {
            System.out.println("Parse exception: " + e.getMessage());
            System.exit(0);
        }
    }
}
