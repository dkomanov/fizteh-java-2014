package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        if (System.getProperty("fizteh.db.dir") == null) {
            System.out.println("we need working directory");
            System.exit(6);
        }
        System.out.println(System.getProperty("fizteh.db.dir"));
        Path dataBaseDirectory
                = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));

        boolean allRight = true;
        if (Files.exists(dataBaseDirectory)) {
            if (!Files.isDirectory(dataBaseDirectory)) {
                System.err.println("working directory is a file");
                System.exit(4);
            }
        } else {
            try {
                Files.createDirectory(dataBaseDirectory);
            } catch (IOException e) {
                System.err.println("error while creating directory");
                allRight = false;
            } finally {
                if (!allRight) {
                    System.exit(5);
                }
            }
        }
        MFileHashMap myMFileHashMap = new MFileHashMap(dataBaseDirectory.toString());
        if (!myMFileHashMap.init()) {
            System.exit(3);
        }

        Shell<MFileHashMap> myShell = new Shell<>(myMFileHashMap);
        myShell.addCommand(new CommandCreate());
        myShell.addCommand(new CommandDrop());
        myShell.addCommand(new CommandUse());
        myShell.addCommand(new CommandGetDistribute());
        myShell.addCommand(new CommandPutDistribute());
        myShell.addCommand(new CommandListDistribute());
        myShell.addCommand(new CommandRemoveDistribute());
        myShell.addCommand(new CommandShowTables());

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
