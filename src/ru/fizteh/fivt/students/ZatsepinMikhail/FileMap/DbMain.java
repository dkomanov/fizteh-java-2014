package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileMap
 * Version from 21.10.2014
 * Created by Mikhail Zatsepin, 395
 */

public class DbMain {
    public static void main(String[] args) {
        String dataBasePathString = System.getProperty("db.file");
        if (dataBasePathString == null) {
            System.out.println("need database file");
            System.exit(2);
        }
        if (Files.isDirectory(Paths.get(dataBasePathString))) {
            System.out.println("cannot access the database: it's a directory");
            System.exit(1);
        }

        boolean errorOccuried = false;
        if (!Files.exists(Paths.get(dataBasePathString))) {
            try {
                Files.createFile(Paths.get(dataBasePathString));
            } catch (IOException e) {
                System.err.println("error while creating database file");
                errorOccuried = true;
            } finally {
                if (errorOccuried) {
                    System.exit(4);
                }
            }
        }

        FileMap myFileMap = new FileMap(dataBasePathString);
        if (!myFileMap.init()) {
            System.exit(3);
        }
        Shell<FileMap> myShell = new Shell<>(myFileMap);
        myShell.addCommand(new Get());
        myShell.addCommand(new List());
        myShell.addCommand(new Put());
        myShell.addCommand(new Remove());

        if (args.length > 0) {
            if (!myShell.packetMode(args)) {
                errorOccuried = true;
            }
        } else {
            if (!myShell.interactiveMode()) {
                errorOccuried = true;
            }
        }
        if (errorOccuried) {
            System.exit(2);
        } else {
            System.exit(0);
        }
    }
}
