package ru.fizteh.fivt.students.pershik.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.pershik.FileMap.InvalidCommandException;
import ru.fizteh.fivt.students.pershik.FileMap.Runner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by pershik on 11/12/14.
 */
public class StoreableTableRunner extends Runner {

    StoreableTableProvider provider;
    StoreableTable table;

    public StoreableTableRunner(String dbDir) throws IOException {
        provider = new StoreableTableProviderFactory().create(dbDir);
    }

    @Override
    protected void execute(String command) {
        String[] tokens = parseCommand(command);
        try {
            switch (tokens[0]) {
                case "create":
                    create(tokens);
                    break;
                case "drop":
                    drop(tokens);
                    break;
                case "use":
                    use(tokens);
                    break;
                case "show":
                    showTables(tokens);
                    break;
                case "size":
                    size(tokens);
                    break;
                case "put":
                    put(tokens);
                    break;
                case "get":
                    get(tokens);
                    break;
                case "remove":
                    remove(tokens);
                    break;
                case "list":
                    list(tokens);
                    break;
                case "commit":
                    commit(tokens);
                    break;
                case "rollback":
                    rollback(tokens);
                    break;
                case "exit":
                    exit(tokens);
                    break;
                case "":
                    break;
                default:
                    errorUnknownCommand(tokens[0]);
                    break;
            }
        } catch (ParseException | ColumnFormatException | IndexOutOfBoundsException e) {
            System.err.println("wrong type (" + e.getMessage() + ")");
            if (batchMode) {
                System.exit(-1);
            }
        } catch (InvalidCommandException | IOException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        }
    }

    private void showTables(String[] args) throws InvalidCommandException {
        if (checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("show tables");
        }
        System.out.println("table_name row_count");
        Map<String, StoreableTable> tables = provider.getAllTables();
        for (String tableName : tables.keySet()) {
            System.out.println(tableName + " " + tables.get(tableName).size());
        }
    }

    private void rollback(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("rollback");
        }
        if (table == null) {
            noTable();
        } else {
            System.out.println(table.rollback());
        }
    }

    private void commit(String[] args)
            throws InvalidCommandException, IOException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("commit");
        }
        if (table == null) {
            noTable();
        } else {
            System.out.println(table.commit());
        }
    }

    private void size(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("size");
        }
        if (table == null) {
            noTable();
        } else {
            System.out.println(table.size());
        }
    }

    private void create(String[] args)
            throws InvalidCommandException, IOException {
        if (args.length < 3) {
            throw new InvalidCommandException("Invalid number of arguments");
        }
        String name = args[1];
        StringBuilder toParse = new StringBuilder("");
        for (int i = 2; i < args.length; i++) {
            toParse.append(args[i]);
            toParse.append(" ");
        }
        List<Class<?>> signature = parseSignature(toParse.toString());
        StoreableTable newTable = provider.createTable(name, signature);
        if (newTable == null) {
            System.out.println(name + " exists");
        } else {
            System.out.println("created");
        }
    }

    private List<Class<?>> parseSignature(String str)
            throws InvalidCommandException {
        List<Class<?>> signature = new ArrayList<>();
        str = str.trim();
        if (!str.startsWith("(") || !str.endsWith(")")) {
            throw new InvalidCommandException("Invalid signature format");
        } else {
            str = str.substring(1, str.length() - 1);
            String[] strArr = str.split(" ");
            for (String s : strArr) {
                switch (s) {
                    case "int":
                        signature.add(Integer.class);
                        break;
                    case "long":
                        signature.add(Long.class);
                        break;
                    case "byte":
                        signature.add(Byte.class);
                        break;
                    case "float":
                        signature.add(Float.class);
                        break;
                    case "double":
                        signature.add(Double.class);
                        break;
                    case "boolean":
                        signature.add(Boolean.class);
                        break;
                    case "String":
                        signature.add(String.class);
                        break;
                    default:
                        throw new InvalidCommandException("Invalid signature");
                }

            }
            return signature;
        }
    }

    private void use(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("use");
        }
        String name = args[1];
        StoreableTable newTable = provider.getTable(name);
        if (newTable == null) {
            System.out.println(name + " not exists");
        } else {
            int uncommitted = 0;
            if (table != null) {
                uncommitted = table.getNumberOfUncommittedChanges();
            }
            if (uncommitted == 0) {
                System.out.println("using " + name);
                table = newTable;
            } else {
                System.out.println(uncommitted + " unsaved changes");
            }
        }
    }

    private void drop(String[] args)
            throws InvalidCommandException, IOException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("drop");
        }
        boolean notExists = false;
        String oldName = null;
        if (table != null) {
            oldName = table.getName();
        }
        String name = args[1];
        try {
            provider.removeTable(name);
        } catch (IllegalStateException e) {
            notExists = true;
        }
        if (notExists) {
            System.out.println(name + " not exists");
        } else {
            System.out.println("dropped");
        }
        if (table != null && oldName.equals(name)) {
            table = null;
        }
    }

    private void put(String[] args)
            throws InvalidCommandException, ParseException {
        if (!checkArguments(2, 2, args.length - 1)) {
            errorCntArguments("put");
        }
        if (table == null) {
            noTable();
        } else {
            Storeable oldValue =
                    table.put(args[1], provider.deserialize(table, args[2]));
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(provider.serialize(table, oldValue));
            }
        }
    }

    private void get(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("get");
        }
        if (table == null) {
            noTable();
        } else {
            Storeable value = table.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(provider.serialize(table, value));
            }
        }
    }

    private void remove(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("remove");
        }
        if (table == null) {
            noTable();
        } else {
            Storeable value = table.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }

    private void list(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("list");
        }
        if (table == null) {
            noTable();
        } else {
            List<String> list = table.list();
            String res = Arrays.toString(list.toArray());
            System.out.println(res.substring(1, res.length() - 1));
        }
    }

    private void exit(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("exit");
        }
        exited = true;
    }

    private void noTable() {
        System.out.println("no table");
    }
}
