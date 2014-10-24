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

    @Override
    public Command fromString(String s) throws UnknownCommandException {
        final String[] argv = s.trim().split("\\s+");

        final MultiFileMap table = db.getCurrentTable();
        switch (argv[0]) {
            case "put":
                return new Put(argv);

            case "get":
                return new Get(argv);

            case "remove":
                return new Remove(argv);

            case "list":
                return new List(argv);

            case "exit":
                return new Exit(argv);

            case "create":
                return new Create(argv);

            case "drop":
                return new Drop(argv);

            case "use":
                return new Use(argv);

            case "show":
                if (!"tables".equals(argv[1])) {
                    throw new UnknownCommandException("show " + argv[1]);
                }
                return new ShowTables(argv);

            default:
                throw new UnknownCommandException(argv[0]);
        }
    }

    private abstract class MultiFileHashMapCommand implements Command {
        protected final String[] argv;
        protected final MultiFileMap table;
        protected final String name;

        protected MultiFileHashMapCommand(String name, String[] argv) {
            this.argv = argv;
            this.name = name;
            table = db.getCurrentTable();
        }

        @Override
        public String name() {
            return name;
        }

        protected void checkArgumentNumber(Command c, int expected, int real) throws CommandExecutionException {
            if (real != expected) {
                throw new CommandExecutionException(c, "Unexpected number of arguments");
            }
        }

        protected boolean checkTableNotNull(MultiFileMap table) {
            if (null == table) {
                System.out.println("no table");
            }
            return null != table;
        }
    }

    public class Create extends MultiFileHashMapCommand {
        protected Create(String[] argv) {
            super("create", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            final MultiFileMap newTable;
            try {
                newTable = provider.createTable(argv[1]);
                if (newTable == null) {
                    System.out.println(String.format("%s exists", argv[1]));
                } else {
                    System.out.println("created");
                    db.setCurrentTable(newTable);
                }
            } catch (IOException e) {
                throw new CommandExecutionException(
                        this,
                        "Error while creating: " + e.getMessage());
            }
        }
    }

    public class Drop extends MultiFileHashMapCommand {
        protected Drop(String[] argv) {
            super("drop", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            try {
                if (provider.removeTable(argv[1])) {
                    System.out.println("dropped");
                } else {
                    System.out.println(String.format("%s not exists", argv[1]));
                }
            } catch (IOException e) {
                throw new CommandExecutionException(
                        this,
                        "Error while dropping: " + e.getMessage());
            }
        }
    }

    private class Use extends MultiFileHashMapCommand {
        protected Use(String[] argv) {
            super("use", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            final MultiFileMap newTable;
            try {
                newTable = provider.getTable(argv[1]);
                if (newTable == null) {
                    System.out.println(String.format("%s not exists", argv[1]));
                } else {
                    System.out.println(String.format("using %s", argv[1]));
                    db.setCurrentTable(newTable);
                }
            } catch (IOException e) {
                throw new CommandExecutionException(
                        this,
                        "Error while loading database: " + e.getMessage());
            }
        }
    }

    public class ShowTables extends MultiFileHashMapCommand {
        protected ShowTables(String[] argv) {
            super("show tables", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            for (String name : provider.listNames()) {
                final int size;
                try {
                    size = provider.getTable(name).size();
                } catch (IOException e) {
                    throw new CommandExecutionException(this, e.getMessage());
                }
                System.out.println(String.format("%s %d", name, size));
            }

        }
    }

    private class Exit extends MultiFileHashMapCommand {
        protected Exit(String[] argv) {
            super("exit", argv);
        }

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
    }

    private class List extends MultiFileHashMapCommand {
        protected List(String[] argv) {
            super("list", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 1, argv.length);
            if (checkTableNotNull(table)) {
                StringJoiner joiner = new StringJoiner(", ");
                try {
                    table.list().forEach(joiner::add);
                } catch (IOException e) {
                    throw new CommandExecutionException(this, e.getMessage());
                }
                System.out.println(joiner.toString());
            }
        }
    }

    private class Remove extends MultiFileHashMapCommand {
        protected Remove(String[] argv) {
            super("remove", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            if (checkTableNotNull(table)) {
                try {
                    System.out.println(table.remove(argv[1]) == null ? "not found" : "removed");
                } catch (IOException e) {
                    throw new CommandExecutionException(this, e.getMessage());
                }
            }
        }
    }

    private class Get extends MultiFileHashMapCommand {
        protected Get(String[] argv) {
            super("get", argv);
        }

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
    }

    private class Put extends MultiFileHashMapCommand {
        protected Put(String[] argv) {
            super("put", argv);
        }

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
    }
}
