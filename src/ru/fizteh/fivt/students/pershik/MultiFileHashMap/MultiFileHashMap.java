package ru.fizteh.fivt.students.pershik.MultiFileHashMap;

import ru.fizteh.fivt.students.pershik.FileMap.InvalidCommandException;
import ru.fizteh.fivt.students.pershik.FileMap.Runner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pershik on 10/19/14.
 */
public class MultiFileHashMap extends Runner {

    protected Map<String, Integer> tables;
    protected File dbDir;
    protected String dbDirPath;
    protected File curDb;
    protected Table curTable;

    protected static final int MOD = 16;

    public MultiFileHashMap(File dir) {
        dbDir = dir;
        dbDirPath = dbDir.getAbsolutePath();
        tables = new HashMap<>();
        curDb = null;
        initTables();
    }

    protected void initTables() {
        try {
            for (File curDir : dbDir.listFiles()) {
                if (curDir.isDirectory()) {
                    Table table = new Table(curDir.getAbsolutePath());
                    int size = table.executeCommand("");
                    tables.put(curDir.getName(), size);
                }
            }
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
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
                case "exit":
                    exit(tokens);
                    break;
                case "":
                    break;
                default:
                    if (curDb == null) {
                        System.out.println("no table");
                    } else {
                        int size = curTable.executeCommand(command);
                        tables.put(curDb.getName(), size);
                    }
            }
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        }
    }

    protected void showTables(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("show tables");
        }
        if (!"tables".equals(args[1])) {
            errorUnknownCommand(args[0] + ' ' + args[1]);
            return;
        }
        System.out.println("table_name row_count");
        for (String tableName : tables.keySet()) {
            System.out.println(tableName + " " + tables.get(tableName));
        }
    }

    protected void create(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("create");
        }
        String tableName = args[1];
        if (tables.get(tableName) != null) {
            System.out.println(tableName + " exists");
        } else {
            tables.put(tableName, 0);
            String tableDirPath = dbDirPath + File.separator + tableName;
            File tableDir = new File(tableDirPath);
            if (!tableDir.mkdir())
                errorCannotMakeDir(tableDirPath);
            System.out.println("created");
        }
    }

    protected void drop(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("drop");
        }
        String tableName = args[1];
        if (tables.get(tableName) != null) {
            tables.remove(tableName);
            removeFromDisk(tableName);
            File toDropDir = new File(dbDirPath + File.separator + tableName);
            if (!toDropDir.delete()) {
                errorCannotDeleteFile(toDropDir.getAbsolutePath());
            }
            if (curDb != null && tableName.equals(curDb.getName())) {
                curDb = null;
                curTable = null;
            }
        } else {
            System.out.println(tableName + " not exists");
        }
    }

    protected void removeFromDisk(String tableName)
            throws InvalidCommandException {
        String pathTable = dbDirPath + File.separator + tableName;
        for (int i = 0; i < MOD; i++) {
            String pathDir = pathTable + File.separator +
                    Integer.toString(i) + ".dir";
            for (int j = 0; j < MOD; j++) {
                String pathFile = pathDir + File.separator +
                        Integer.toString(j) + ".dat";
                File curFile = new File(pathFile);
                if (curFile.exists() && !curFile.delete()) {
                    errorCannotDeleteFile(pathFile);
                }
            }
            File curDir = new File(pathDir);
            if (curDir.exists() && !curDir.delete()) {
                errorCannotDeleteFile(pathDir);
            }
        }
    }

    protected void use(String[] args) throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("use");
        }
        String tableName = args[1];
        if (tables.get(tableName) == null) {
            System.out.println(tableName + " not exists");
        } else {
            if (curTable != null) {
                removeFromDisk(curDb.getName());
                curTable.writeDb();
            }
            curDb = new File(dbDirPath + File.separator + tableName);
            curTable = new Table(curDb.getAbsolutePath());
            System.out.println("using " + tableName);
        }
    }

    protected void exit(String[] args) throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("exit");
        }
        exited = true;
        if (curTable != null) {
            removeFromDisk(curDb.getName());
            curTable.writeDb();
        }
    }

    protected void errorCannotMakeDir(String directory)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                "cannot create directory " + directory);
    }

    protected void errorCannotDeleteFile(String pathFile)
            throws InvalidCommandException {
        throw new InvalidCommandException("Can't delete " + pathFile);
    }
}
