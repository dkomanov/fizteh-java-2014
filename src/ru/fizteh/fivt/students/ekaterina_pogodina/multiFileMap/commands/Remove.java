package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

import java.nio.file.Files;
import java.nio.file.Path;

public class Remove extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (args.length > 2) {
            table.manyArgs(args[0]);
        }
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        String key = args[1];
        byte b = key.getBytes()[0];
        int nDirectory = b % 16;
        int nFile = b / 16 % 16;
        table.usingTable.tableDateBase[nDirectory][nFile].remove(args);
        if (table.usingTable.tableDateBase[nDirectory][nFile].rowCount() == 0) {
            String s = null;
            s = s.concat(String.valueOf(nDirectory));
            s = s.concat(".dir");
            Path pathDir = table.usingTable.path.resolve(s);
            s = String.valueOf(nFile);
            s = s.concat(".dat");
            Path pathFile = pathDir.resolve(s);
            Files.deleteIfExists(pathFile);
        }


    }
}
