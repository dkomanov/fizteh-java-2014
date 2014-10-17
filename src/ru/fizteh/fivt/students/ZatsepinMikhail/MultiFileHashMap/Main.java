package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.Shell;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 16.10.14.
 */
public class Main {
    public static void main(String[] args) {
        //String dataBaseDirectory = System.getProperty("fizteh.db.dir");
        String dataBaseDirectory = "/home/mikhail/IdeaProjects/DataBase";
        if (!Files.exists(Paths.get(dataBaseDirectory))) {
            System.err.println("working directory doesn't exist");
            System.exit(4);
        }
        boolean allRight = true;
        MFileHashMap myMFileHashMap;
        myMFileHashMap = new MFileHashMap(dataBaseDirectory);
        if (!myMFileHashMap.init()) {
            System.out.println("error while init");
        }
        System.out.println("numberoftables = " + myMFileHashMap.getNumberOfTables());
        myMFileHashMap.printTables();
        Shell<MFileHashMap> myShell = new Shell<>(myMFileHashMap);
        myShell.addCommand(new CommandCreate());
        myShell.addCommand(new CommandDrop());

        if (args.length > 0) {
            allRight = myShell.packetMode(args);
        } else {
            allRight = myShell.interactiveMode();
        }
        if (allRight) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
