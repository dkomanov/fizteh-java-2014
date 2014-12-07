package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

public class ShowCommand implements Command {
    public void run(String[] args, DataBase dataBase)
            throws MultiFileMapException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        String[] tables = dataBase.getTableNames();
        for (String table : tables) {
            if (table.charAt(0) != '.') {
                System.out.println(table);
            }
        }
    }
}
