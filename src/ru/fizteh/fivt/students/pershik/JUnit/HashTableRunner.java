package ru.fizteh.fivt.students.pershik.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.pershik.FileMap.InvalidCommandException;
import ru.fizteh.fivt.students.pershik.FileMap.Runner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by pershik on 10/29/14.
 */
public class HashTableRunner extends Runner {

    HashTableProvider provider;
    HashTable table;

    public HashTableRunner(String dbDir) {
        provider = new HashTableProviderFactory().create(dbDir);
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
        } catch (InvalidCommandException e) {
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
        Map<String, Integer> tables = provider.getAllTables();
        for (String tableName : tables.keySet()) {
            System.out.println(tableName + " " + tables.get(tableName));
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

    private void commit(String[] args) throws InvalidCommandException {
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

    private void create(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("create");
        }
        String name = args[1];
        Table newTable = provider.createTable(name);
        if (newTable == null) {
            System.out.println(name + " exists");
        } else {
            System.out.println("created");
        }
    }

    private void use(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("use");
        }
        String name = args[1];
        HashTable newTable = provider.getTable(name);
        if (newTable == null) {
            System.out.println(name + " not exists");
        } else {
            int uncommitted = 0;
            if (table != null) {
                uncommitted = table.getUncommittedCnt();
            }
            if (uncommitted == 0) {
                System.out.println("using " + name);
                table = newTable;
            } else {
                System.out.println(uncommitted + " unsaved changes");
            }
        }
    }

    private void drop(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("drop");
        }
        boolean notExists = false;
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
        if (table != null && table.getName().equals(name)) {
            table = null;
        }
    }

    private void put(String[] args) throws InvalidCommandException {
        if (!checkArguments(2, 2, args.length - 1)) {
            errorCntArguments("put");
        }
        if (table == null) {
            noTable();
        } else {
            String oldValue = table.put(args[1], args[2]);
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(oldValue);
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
            String value = table.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
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
            String value = table.remove(args[1]);
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
