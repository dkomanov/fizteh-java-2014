package multifilehashmap.util;

import java.io.File;
import java.util.Map;

public class TableLauncher {
    Launcher launcher;
    Map<String, String> data;
    File file;
    String currentDb;
    
    public String getCurrentDb() {
        return currentDb;
    }

    public TableLauncher(File file) throws Exception {
        if (!file.isDirectory()) {
            throw new Exception("Given property isn't a directory");
        }
        this.file = file;
        currentDb = "";
    }

    public void run(String[] cmd) throws Exception {
        switch (cmd[0]) {
            case "create":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                create(cmd);
                break;
            case "drop":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                drop(cmd);
                break;
            case "use":
                if (cmd.length != 2) {
                    throw new Exception("Wrong number of arguments");
                }
                use(cmd);
                break;
            case "show":
                if (cmd.length != 2 || !cmd[1].equals("tables")) {
                    throw new Exception("Bad command");
                }
                showTables();
                break;
            case "exit":
                if (cmd.length != 1) {
                    throw new Exception("Wrong number of arguments");
                }
                if (data != null) {
                    FolderData.saveDb(data, new File(file, currentDb));
                }
                throw new ExitException("Exit");
            default:
                if (launcher == null) {
                    System.out.println("no table");
                } else {
                    try {
                        launcher.run(cmd);
                    } catch (ExitException e) {
                        FolderData.saveDb(data, new File(file, currentDb));
                        throw e;
                    }
                }
        }
    }

    private void create(String[] cmd) {
        String tableName = cmd[1];
        File tableFile = new File(file, tableName);
        if (tableFile.exists()) {
            System.out.print("tablename exists");
        } else {
            tableFile.mkdir();
            System.out.println("created");
        }
    }

    private void drop(String[] cmd) {
        String tableName = cmd[1];
        File tableFile = new File(file, tableName);
        if (!tableFile.isDirectory()) {
            System.out.println("tablename not exist");
        } else {
            try {
                for (int i = 0; i < 16; i++) {
                    File folder = new File(tableFile, i + ".dir");
                    if (folder.exists()) {
                        for (int j = 0; j < 16; j++) {
                            File file = new File(folder, j + ".dat");
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        folder.delete();
                    }
                }
                tableFile.delete();
                System.out.println("dropped");
                if (currentDb.equals(tableName)) {
                    init();
                }
            } catch (Exception e) {
                System.out.println("Some of directories is not empty");
            }
        }
    }
    
    private void init() {
        currentDb = "";
        launcher = null;
        data = null;
    }

    private void use(String[] cmd) throws Exception {
        String tableName = cmd[1];
        File tableFile = new File(file, tableName);
        if (!tableFile.isDirectory()) {
            System.out.println("table not exists");
        } else {
            if (launcher != null) {
                FolderData.saveDb(data, new File(file, currentDb));
            }
            data = FolderData.loadDb(tableFile);
            launcher = new Launcher(data);
            currentDb = tableName;
            System.out.println("using " + tableName);
        }
    }

    private void showTables() {
        for (File table : file.listFiles()) {
            if (table.getName().equals(currentDb)) {
                System.out.println(table.getName() + " " + data.size());
            } else if (table.isDirectory()) {
                try {
                    System.out.println(table.getName() + " "
                            + FolderData.loadDb(table).size());
                } catch (Exception e) {
                    System.out.println("Problem with one of Data Bases");
                }
            }
        }
    }
}
