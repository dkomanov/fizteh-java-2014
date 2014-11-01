package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

import java.nio.file.Path;

public class Put extends Command {

    private static final int SIZE = 16;

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        String tableName = table.currentTable;
        if (args.length > 3) {
            table.manyArgs(args[0]);
        }
        if (args.length < 3) {
            table.missingOperand(args[0]);
        }
        if (table.currentTable == null) {
            throw new Exception("no table");
        }
        Path path = table.path;
        String key = args[1];
        String value = args[2];
        byte b = key.getBytes()[0];
        int nDirectory = b % SIZE;
        int nFile = b / SIZE % SIZE;
        String s;

        s = String.valueOf(nDirectory);
        s = s.concat(".dir");
        Path pathDir = path.resolve(table.currentTable);
        pathDir = pathDir.resolve(s);
        try {
            if (!pathDir.toFile().exists()) {
                try {
                    pathDir.toFile().mkdir();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        s = String.valueOf(nFile);
        s = s.concat(".dat");
        Path pathFile = pathDir.resolve(s);
        try {
            if (!pathFile.toFile().exists()) {
                try {
                    pathFile.toFile().createNewFile();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (table.usingTable.tableDateBase[nDirectory][nFile] == null) {

            table.usingTable.tableDateBase[nDirectory][nFile] = new DataBase(pathFile.toString());
        }
        table.usingTable.tableDateBase[nDirectory][nFile].put(args[1], args[2]);
    }
}
