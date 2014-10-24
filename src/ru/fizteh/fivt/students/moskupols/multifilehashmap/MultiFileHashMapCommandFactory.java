package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

import java.io.IOException;
import java.util.StringJoiner;

/**
 * Created by moskupols on 23.10.14.
 */
public class MultiFileHashMapCommandFactory implements CommandFactory {

    private final DataBaseCursor db;
    private final MultiFileMapProvider provider;

    public MultiFileHashMapCommandFactory(DataBaseCursor db, MultiFileMapProvider provider) {
        this.db = db;
        this.provider = provider;
    }

    private void checkArgumentNumber(Command c, int expected, int real) throws CommandExecutionException {
        if (real != expected) {
            throw new CommandExecutionException(c, "Unexpected number of arguments");
        }
    }

    private boolean checkTableNotNull(MultiFileMap table) {
        if (null == table) {
            System.out.println("no table");
        }
        return null != table;
    }

    @Override
    public Command fromString(String s) throws UnknownCommandException {
        final String[] argv = s.trim().split("\\s+");

        final MultiFileMap table = db.getCurrentTable();
        switch (argv[0]) {
            case "put":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 3, argv.length);
                        if (checkTableNotNull(table)) {
                            String old;
                            try {
                                old = table.put(argv[1], argv[2]);
                            } catch (Exception e) {
                                throw new CommandExecutionException(this, e.getMessage());
                            }
                            if (null == old) {
                                System.out.println("new");
                            } else {
                                System.out.println("overwrite");
                                System.out.println(old);
                            }
                        }
                    }

                    @Override
                    public String name() {
                        return "put";
                    }
                };

            case "get":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        if (checkTableNotNull(table)) {
                            String value;
                            try {
                                value = table.get(argv[1]);
                            } catch (Exception e) {
                                throw new CommandExecutionException(this, e.getMessage());
                            }
                            System.out.println(value == null ? "not found" : value);
                        }
                    }

                    @Override
                    public String name() {
                        return "get";
                    }
                };

            case "remove":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        if (checkTableNotNull(table)) {
                            try {
                                System.out.println(table.remove(argv[1]) == null ? "not found" : "removed");
                            } catch (Exception e) {
                                throw new CommandExecutionException(this, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public String name() {
                        return "remove";
                    }
                };

            case "list":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 1, argv.length);
                        if (checkTableNotNull(table)) {
                            StringJoiner joiner = new StringJoiner(", ");
                            table.list().forEach(joiner::add);
                            System.out.println(joiner.toString());
                        }
                    }

                    @Override
                    public String name() {
                        return "list";
                    }
                };

            case "exit":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 1, argv.length);
                        try {
                            db.getCurrentTable().flush();
                        } catch (IOException e) {
                            throw new IllegalStateException(
                                    String.format("Couldn't save %s", db.getCurrentTable().getName()));
                        }
                        throw new StopProcessingException();
                    }

                    @Override
                    public String name() {
                        return "exit";
                    }
                };

            case "create":
                return new Command() {
                    @Override
                    public String name() {
                        return "create";
                    }

                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        final MultiFileMap newTable = provider.createTable(argv[1]);
                        if (newTable == null) {
                            System.out.println(String.format("%s exists", argv[1]));
                        } else {
                            System.out.println("created");
                            db.setCurrentTable(newTable);
                        }
                    }
                };

            case "drop":
                return new Command() {
                    @Override
                    public String name() {
                        return "drop";
                    }

                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        if (provider.removeTable(argv[1])) {
                            System.out.println("dropped");
                        } else {
                            System.out.println(String.format("%s not exists", argv[1]));
                        }
                    }
                };

            case "use":
                return new Command() {
                    @Override
                    public String name() {
                        return "use";
                    }

                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        final MultiFileMap newTable = provider.getTable(argv[1]);
                        if (newTable == null) {
                            System.out.println(String.format("%s not exists", argv[1]));
                        } else {
                            System.out.println(String.format("using %s", argv[1]));
                            db.setCurrentTable(newTable);
                        }
                    }
                };

            case "show":
                if (!"tables".equals(argv[1])) {
                    throw new UnknownCommandException("show " + argv[1]);
                }
                return new Command() {
                    @Override
                    public String name() {
                        return "show tables";
                    }

                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        checkArgumentNumber(this, 2, argv.length);
                        for (String name : provider.listNames()) {
                            final int size;
                            try {
                                size = provider.getTable(name).size();
                            } catch (IllegalStateException | IllegalArgumentException e) {
                                throw new CommandExecutionException(this, e.getMessage());
                            }
                            System.out.println(String.format("%s %d", name, size));
                        }
                    }
                };

            default:
                throw new UnknownCommandException(argv[0]);
        }
    }
}