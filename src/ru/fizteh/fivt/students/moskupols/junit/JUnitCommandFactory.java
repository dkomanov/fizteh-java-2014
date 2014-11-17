package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by moskupols on 17.11.14.
 */
public class JUnitCommandFactory implements CommandFactory {

    private final MultiFileMapTableProvider provider;
    private KnownDiffTable currentTable;

    JUnitCommandFactory(MultiFileMapTableProvider provider) {
        this.provider = provider;
        currentTable = null;
    }

    @Override
    public Command fromString(String s) throws UnknownCommandException {
        final String[] argv = s.trim().split("\\s+");

        switch (argv[0]) {
            case "put":
                return new Put(argv);

            case "get":
                return new Get(argv);

            case "remove":
                return new Remove(argv);

            case "list":
                return new ListCmd(argv);

            case "exit":
                return new Exit(argv);

            case "create":
                return new Create(argv);

            case "drop":
                return new Drop(argv);

            case "use":
                return new Use(argv);

            case "show":
                if (argv.length < 2) {
                    throw new UnknownCommandException("show");
                } else if (!"tables".equals(argv[1])) {
                    throw new UnknownCommandException("show " + argv[1]);
                }
                return new ShowTables(argv);

            case "commit":
                return new Commit(argv);

            case "rollback":
                return new Rollback(argv);

            case "size":
                return new Size(argv);

            default:
                throw new UnknownCommandException(argv[0]);
        }
    }

    protected abstract class JUnitCommand implements Command {
        protected final String[] argv;
        protected final String name;

        protected JUnitCommand(String name, String[] argv) {
            this.argv = argv;
            this.name = name;
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

        protected boolean checkTableNotNull(KnownDiffTable table) {
            if (null == table) {
                System.out.println("no table");
            }
            return null != table;
        }
    }

    protected class Create extends JUnitCommand {
        protected Create(String[] argv) {
            super("create", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            final KnownDiffTable newTable;
            newTable = provider.createTable(argv[1]);
            if (newTable == null) {
                System.out.println(String.format("%s exists", argv[1]));
            } else {
                System.out.println("created");
            }
        }
    }

    protected class Drop extends JUnitCommand {
        protected Drop(String[] argv) {
            super("drop", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            final boolean removingCur = currentTable != null && currentTable.getName().equals(argv[1]);
            final boolean existed = removingCur || provider.getTable(argv[1]) != null;
            if (existed) {
                System.out.println("dropped");
            } else {
                System.out.println(String.format("%s not exists", argv[1]));
            }
            provider.removeTable(argv[1]);
            if (removingCur) {
               currentTable = null;
            }
        }
    }

    protected class Use extends JUnitCommand {
        protected Use(String[] argv) {
            super("use", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            final KnownDiffTable newTable;
            if (currentTable != null) {
                if (currentTable.diff() != 0) {
                    System.out.printf("%d uncommitted changes\n", currentTable.diff());
                }
            }
            newTable = provider.getTable(argv[1]);
            if (newTable == null) {
                System.out.println(String.format("%s not exists", argv[1]));
            } else {
                System.out.println(String.format("using %s", argv[1]));
                currentTable = newTable;
            }
        }
    }

    public class ShowTables extends JUnitCommand {
        protected ShowTables(String[] argv) {
            super("show tables", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            List<String> lines = new LinkedList<>();
            for (String name : provider.listNames()) {
                final KnownDiffTable table =
                        currentTable != null && name.equals(currentTable.getName())
                                ? currentTable
                                : provider.getTable(name);
                lines.add(String.format("%s %d", name, table.size()));
            }
            lines.forEach(System.out::println);
        }
    }

    protected class Exit extends JUnitCommand {
        protected Exit(String[] argv) {
            super("exit", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 1, argv.length);
            if (currentTable != null && currentTable.diff() != 0) {
                System.out.printf("%d uncommitted changes", currentTable.diff());
                return;
            }
            throw new StopProcessingException();
        }
    }

    protected class ListCmd extends JUnitCommand {
        protected ListCmd(String[] argv) {
            super("list", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 1, argv.length);
            if (checkTableNotNull(currentTable)) {
                StringJoiner joiner = new StringJoiner(", ");
                currentTable.list().forEach(joiner::add);
                System.out.println(joiner.toString());
            }
        }
    }

    protected class Remove extends JUnitCommand {
        protected Remove(String[] argv) {
            super("remove", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            if (checkTableNotNull(currentTable)) {
                System.out.println(currentTable.remove(argv[1]) == null ? "not found" : "removed");
            }
        }
    }

    protected class Get extends JUnitCommand {
        protected Get(String[] argv) {
            super("get", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 2, argv.length);
            if (checkTableNotNull(currentTable)) {
                String value = currentTable.get(argv[1]);
                System.out.println(value == null ? "not found" : value);
            }
        }
    }

    protected class Put extends JUnitCommand {
        protected Put(String[] argv) {
            super("put", argv);
        }

        @Override
        public void execute() throws CommandExecutionException, StopProcessingException {
            checkArgumentNumber(this, 3, argv.length);
            if (checkTableNotNull(currentTable)) {
                String old;
                old = currentTable.put(argv[1], argv[2]);
                if (null == old) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(old);
                }
            }
        }
    }

    protected class Commit extends JUnitCommand {
        protected Commit(String[] argv) {
            super("commit", argv);
        }

        @Override
        public void execute() throws CommandExecutionException {
            checkArgumentNumber(this, 1, argv.length);
            if (checkTableNotNull(currentTable)) {
                System.out.println(currentTable.commit());
            }
        }
    }

    protected class Rollback extends JUnitCommand {
        protected Rollback(String[] argv) {
            super("rollback", argv);
        }

        @Override
        public void execute() throws CommandExecutionException {
            checkArgumentNumber(this, 1, argv.length);
            if (checkTableNotNull(currentTable)) {
                System.out.println(currentTable.rollback());
            }
        }
    }

    protected class Size extends JUnitCommand {
        protected Size(String[] argv) {
            super("size", argv);
        }

        @Override
        public void execute() throws CommandExecutionException {
            checkArgumentNumber(this, 1, argv.length);
            if (checkTableNotNull(currentTable)) {
                System.out.println(currentTable.size());
            }
        }
    }
}
